package org.singularux.music.feature.nowplaying.presentation;

import android.net.Uri;
import android.os.Bundle;

import androidx.activity.ComponentActivity;
import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.slider.LabelFormatter;
import com.google.android.material.slider.Slider;
import com.squareup.picasso.Picasso;

import org.singularux.music.core.playback.MusicControllerFacade;
import org.singularux.music.feature.nowplaying.R;
import org.singularux.music.feature.nowplaying.databinding.ActivityNowPlaying2Binding;
import org.singularux.music.feature.playback.data.PlaybackItemInfo;
import org.singularux.music.feature.playback.data.PlaybackPosition;
import org.singularux.music.feature.playback.data.PlaybackState;

import java.time.Duration;
import java.util.Optional;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class NowPlaying2Activity extends ComponentActivity {

    public @Inject MusicControllerFacade musicControllerFacade;
    public @Inject Picasso picasso;

    private ActivityNowPlaying2Binding binding;
    private NowPlaying2ViewModel viewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        // Create activity with edge-to-edge support
        EdgeToEdge.enable(this);
        super.onCreate(savedInstanceState);
        // Extract XML elements
        binding = ActivityNowPlaying2Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        // Add inset listeners
        ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(),
                new NowPlayingContainerInsetListener());
        // Extract ViewModel
        viewModel = new ViewModelProvider(this).get(NowPlaying2ViewModel.class);
        // Add static action listeners
        binding.topBar.back.setOnClickListener(v -> finish());
        binding.playerController.playPause.setOnClickListener(v -> viewModel.play());
        binding.playerController.skipPrev.setOnClickListener(v -> viewModel.skipPrev());
        binding.playerController.skipNext.setOnClickListener(v -> viewModel.skipNext());
        // Listen data
        PlaybackPositionObserver playbackPositionObserver = new PlaybackPositionObserver();
        viewModel.getPlaybackState().observe(this, new PlaybackStateObserver());
        viewModel.getPlaybackItemInfo().observe(this, new PlaybackItemInfoObserver());
        viewModel.getPlaybackPosition().observe(this, playbackPositionObserver);
        binding.slider.progressBar.setLabelFormatter(playbackPositionObserver);
        binding.slider.progressBar.addOnChangeListener(playbackPositionObserver);
        binding.slider.progressBar.addOnSliderTouchListener(playbackPositionObserver);
    }

    @Override
    protected void onDestroy() {
        // Release controller before finishing this activity
        if (isFinishing())
            musicControllerFacade.release();
        super.onDestroy();
    }

    private final class PlaybackStateObserver implements Observer<PlaybackState> {

        @Override
        public void onChanged(@NonNull PlaybackState playbackState) {
            // Set enabled buttons
            boolean skipPrevEnabled = playbackState.isReady();
            boolean skipNextEnabled = playbackState.isReady() && playbackState.hasNextItem();
            boolean playPauseEnabled = playbackState.isReady();
            binding.playerController.skipPrev.setEnabled(skipPrevEnabled);
            binding.playerController.skipNext.setEnabled(skipNextEnabled);
            binding.playerController.playPause.setEnabled(playPauseEnabled);
            // Set play/pause drawable and action
            if (playbackState.isPlaying()) {
                // Set pause action
                binding.playerController.playPause.setIconResource(R.drawable.pause_32);
                binding.playerController.playPause.setOnClickListener(v -> viewModel.pause());
            } else {
                // Set play action
                binding.playerController.playPause.setIconResource(R.drawable.play_32);
                binding.playerController.playPause.setOnClickListener(v -> viewModel.play());
            }
        }

    }

    private final class PlaybackItemInfoObserver implements Observer<Optional<PlaybackItemInfo>> {

        @Override
        public void onChanged(@NonNull Optional<PlaybackItemInfo> maybePlaybackItemInfo) {
            // Get title, titleDuration, artist (if present) and artwork (if present)
            String title, artist, sliderDuration;
            Uri artworkPath;
            if (maybePlaybackItemInfo.isPresent()) {
                PlaybackItemInfo playbackItemInfo = maybePlaybackItemInfo.get();
                title = playbackItemInfo.getTitle();
                if (playbackItemInfo.getArtistName() != null) {
                    artist = playbackItemInfo.getArtistName();
                } else {
                    artist = getString(R.string.title_unknown_artist);
                }
                long labelMinutes = playbackItemInfo.getDuration().getSeconds() / 60;
                long labelSeconds = playbackItemInfo.getDuration().getSeconds() % 60;
                sliderDuration = getString(R.string.duration_formatted, labelMinutes, labelSeconds);
                if (playbackItemInfo.getArtworkPath() != null) {
                    artworkPath = playbackItemInfo.getArtworkPath();
                } else {
                    artworkPath = null;
                }
            } else {
                title = getString(R.string.title_no_track);
                artist = getString(R.string.title_no_artist);
                sliderDuration = getString(R.string.duration_placeholder);
                artworkPath = null;
            }
            // Apply them
            binding.title.title.setText(title);
            binding.title.artist.setText(artist);
            binding.slider.duration.setText(sliderDuration);
            if (artworkPath != null) {
                picasso.load(artworkPath)
                        .into(binding.artwork.artwork);
            } else {
                picasso.cancelRequest(binding.artwork.artwork);
                binding.artwork.artwork.setImageDrawable(null);
            }
        }

    }

    private final class PlaybackPositionObserver implements Observer<PlaybackPosition>,
            LabelFormatter, Slider.OnChangeListener, Slider.OnSliderTouchListener {

        private Duration contentDuration = Duration.ofMillis(1);
        private boolean updateSlider = true;
        private float newValue = 0.0F;

        @Override
        public void onChanged(@NonNull PlaybackPosition playbackPosition) {
            contentDuration = playbackPosition.getContentDuration();
            // Update slider value only if user is not interacting with
            if (updateSlider)
                binding.slider.progressBar.setValue(playbackPosition.getProgress());
            // Update current duration text
            long labelMinutes = playbackPosition.getCurrentPosition().getSeconds() / 60;
            long labelSeconds = playbackPosition.getCurrentPosition().getSeconds() % 60;
            String labelText = getString(R.string.duration_formatted, labelMinutes, labelSeconds);
            binding.slider.position.setText(labelText);
        }

        @Override
        public @NonNull String getFormattedValue(float value) {
            // Calculate the pointed position and apply to the label
            Duration pointedDuration = Duration
                    .ofMillis((long) (value * contentDuration.toMillis()));
            long labelMinutes = pointedDuration.getSeconds() / 60;
            long labelSeconds = pointedDuration.getSeconds() % 60;
            return getString(R.string.duration_formatted, labelMinutes, labelSeconds);
        }

        @Override
        public void onValueChange(@NonNull Slider slider, float value, boolean fromUser) {
            // Update the new value only if user asks to
            if (fromUser)
                newValue = value;
        }

        @Override
        public void onStartTrackingTouch(@NonNull Slider slider) {
            // Stop updating the slider progress, user is interacting
            updateSlider = false;
        }

        @Override
        public void onStopTrackingTouch(@NonNull Slider slider) {
            // Restart updating the slider progress
            updateSlider = true;
            // Seek to that value
            viewModel.seekTo((long) (newValue * contentDuration.toMillis()));
        }

    }

}
