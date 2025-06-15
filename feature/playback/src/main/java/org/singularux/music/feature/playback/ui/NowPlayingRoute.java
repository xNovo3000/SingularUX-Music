package org.singularux.music.feature.playback.ui;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.slider.LabelFormatter;
import com.google.android.material.slider.Slider;
import com.squareup.picasso.Picasso;

import org.singularux.music.feature.playback.R;
import org.singularux.music.feature.playback.databinding.RouteNowPlayingBinding;
import org.singularux.music.feature.playback.model.PlaybackInfo;
import org.singularux.music.feature.playback.model.PlaybackPosition;
import org.singularux.music.feature.playback.ui.listener.ContainerInsetListener;
import org.singularux.music.feature.playback.viewmodel.NowPlayingViewModel;

import java.time.Duration;
import java.util.Optional;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@AndroidEntryPoint
public class NowPlayingRoute extends Fragment {

    @Inject public ContainerInsetListener containerInsetListener;
    @Inject public Picasso picasso;

    private RouteNowPlayingBinding binding;
    private NowPlayingViewModel viewModel;

    private PlaybackPositionObserver playbackPositionObserver;
    private SliderListener sliderListener;

    public NowPlayingRoute() {
        super(R.layout.route_now_playing);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        binding = RouteNowPlayingBinding.bind(view);
        viewModel = new ViewModelProvider(this).get(NowPlayingViewModel.class);
        playbackPositionObserver = new PlaybackPositionObserver();
        sliderListener = new SliderListener();
        NavController navController = NavHostFragment.findNavController(this);
        // Add back button callbacks
        binding.nowPlayingClose.setOnClickListener(v -> navController.navigateUp());
        // Add listeners
        ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(), containerInsetListener);
        binding.nowPlayingProgress.addOnSliderTouchListener(sliderListener);
        binding.nowPlayingProgress.addOnChangeListener(sliderListener);
        // Listen for data
        viewModel.getPlaybackInfo().observe(getViewLifecycleOwner(), new PlaybackInfoObserver());
        viewModel.getPlaybackPosition().observe(getViewLifecycleOwner(), playbackPositionObserver);
    }

    private class SliderListener implements Slider.OnSliderTouchListener, Slider.OnChangeListener {

        private @Setter @Nullable Duration duration = null;
        private float newValue = 0.0F;

        @Override
        public void onValueChange(@NonNull Slider slider, float value, boolean fromUser) {
            if (fromUser) {
                newValue = value;
            }
        }

        @Override
        public void onStartTrackingTouch(@NonNull Slider slider) {
            viewModel.getPlaybackPosition().removeObserver(playbackPositionObserver);
        }

        @Override
        public void onStopTrackingTouch(@NonNull Slider slider) {
            if (duration != null) {
                viewModel.seekTo((long) (duration.getSeconds() * newValue));
            }
            viewModel.getPlaybackPosition().observe(getViewLifecycleOwner(), playbackPositionObserver);
        }

    }

    private class PlaybackPositionObserver implements Observer<PlaybackPosition> {

        @Override
        public void onChanged(@NonNull PlaybackPosition playbackPosition) {
            Context context = ContextCompat.getContextForLanguage(requireContext());
            // Set progress bar value
            binding.nowPlayingProgress.setValue(playbackPosition.getPosition());
            // Set current time
            long currentMinutes = playbackPosition.getCurrent().getSeconds() / 60;
            long currentSeconds = playbackPosition.getCurrent().getSeconds() % 60;
            String currentString = context.getString(R.string.now_playing_duration,
                    currentMinutes, currentSeconds);
            binding.nowPlayingCurrentTime.setText(currentString);
        }

    }

    private class PlaybackInfoObserver implements Observer<Optional<PlaybackInfo>> {

        @Override
        public void onChanged(@NonNull Optional<PlaybackInfo> maybePlaybackInfo) {
            Context context = ContextCompat.getContextForLanguage(requireContext());
            String title;
            String artist;
            Uri artworkUri = null;
            boolean canPlay = false;
            boolean isPlaying = false;
            boolean hasPrevious = false;
            boolean hasNext = false;
            Duration duration = Duration.ZERO;
            // Extract
            if (maybePlaybackInfo.isPresent()) {
                PlaybackInfo playbackInfo = maybePlaybackInfo.get();
                // Title
                title = playbackInfo.getTitle();
                // Artist
                if (playbackInfo.getArtistName() != null) {
                    artist = playbackInfo.getArtistName();
                } else {
                    artist = ContextCompat.getString(requireContext(), R.string.now_playing_unknown_artist);
                }
                // Artwork
                artworkUri = playbackInfo.getArtworkUri();
                // Duration
                duration = playbackInfo.getDuration();
                // Other
                canPlay = true;
                isPlaying = playbackInfo.isPlaying();
                hasPrevious = playbackInfo.isHasPrevious();
                hasNext = playbackInfo.isHasNext();
            } else {
                title = ContextCompat.getString(requireContext(), R.string.now_playing_unknown_track);
                artist = ContextCompat.getString(requireContext(), R.string.now_playing_unknown_artist);
            }
            // Apply
            binding.nowPlayingProgress.setLabelFormatter(new MusicLabelFormatter(duration));
            sliderListener.setDuration(duration);
            binding.nowPlayingTitle.setText(title);
            binding.nowPlayingArtist.setText(artist);
            binding.nowPlayingPrev.setEnabled(hasPrevious);
            binding.nowPlayingPrev.setOnClickListener(v -> viewModel.skipPrev());
            binding.nowPlayingNext.setEnabled(hasNext);
            binding.nowPlayingNext.setOnClickListener(v -> viewModel.skipNext());
            binding.nowPlayingPlayPause.setEnabled(canPlay);
            if (isPlaying) {
                binding.nowPlayingPlayPause.setIconResource(R.drawable.round_pause_24);
                binding.nowPlayingPlayPause.setOnClickListener(v -> viewModel.pause());
            } else {
                binding.nowPlayingPlayPause.setIconResource(R.drawable.round_play_arrow_24);
                binding.nowPlayingPlayPause.setOnClickListener(v -> viewModel.play());
            }
            if (artworkUri != null) {
                picasso.load(artworkUri)
                        .into(binding.nowPlayingArtwork);
            } else {
                binding.nowPlayingArtwork.setImageDrawable(null);
            }
            long totalMinutes = duration.getSeconds() / 60;
            long totalSeconds = duration.getSeconds() % 60;
            String currentString = context.getString(R.string.now_playing_duration,
                    totalMinutes, totalSeconds);
            binding.nowPlayingTotalTime.setText(currentString);
        }

    }

    @RequiredArgsConstructor
    private class MusicLabelFormatter implements LabelFormatter {

        private final Duration total;
        private final Context context = ContextCompat.getContextForLanguage(requireContext());

        @Override
        public @NonNull String getFormattedValue(float value) {
            long totalSeconds = (long) (total.getSeconds() * value);
            long minutesPart = totalSeconds / 60;
            long secondsPart = totalSeconds % 60;
            // TODO: Check the efficiency of this
            // return context.getString(R.string.now_playing_duration, minutesPart, secondsPart);
            return minutesPart + ":" + secondsPart;
        }

    }

}
