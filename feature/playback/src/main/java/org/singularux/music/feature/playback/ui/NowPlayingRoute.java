package org.singularux.music.feature.playback.ui;

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

import com.squareup.picasso.Picasso;

import org.singularux.music.feature.playback.R;
import org.singularux.music.feature.playback.databinding.RouteNowPlayingBinding;
import org.singularux.music.feature.playback.model.PlaybackInfo;
import org.singularux.music.feature.playback.ui.listener.ContainerInsetListener;
import org.singularux.music.feature.playback.viewmodel.NowPlayingViewModel;

import java.util.Optional;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class NowPlayingRoute extends Fragment {

    @Inject public ContainerInsetListener containerInsetListener;

    @Inject public Picasso picasso;

    private RouteNowPlayingBinding binding;
    private NowPlayingViewModel viewModel;

    public NowPlayingRoute() {
        super(R.layout.route_now_playing);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        binding = RouteNowPlayingBinding.bind(view);
        viewModel = new ViewModelProvider(this).get(NowPlayingViewModel.class);
        NavController navController = NavHostFragment.findNavController(this);
        // Add back button callbacks
        binding.nowPlayingClose.setOnClickListener(v -> navController.navigateUp());
        // Add listeners
        ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(), containerInsetListener);
        // Listen for data
        viewModel.getPlaybackInfo().observe(getViewLifecycleOwner(), new PlaybackInfoObserver());
    }

    private class PlaybackInfoObserver implements Observer<Optional<PlaybackInfo>> {

        @Override
        public void onChanged(Optional<PlaybackInfo> maybePlaybackInfo) {
            String title;
            String artist;
            Uri artworkUri = null;
            boolean canPlay = false;
            boolean isPlaying = false;
            boolean hasPrevious = false;
            boolean hasNext = false;
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
            binding.nowPlayingTitle.setText(title);
            binding.nowPlayingArtist.setText(artist);
            binding.nowPlayingPrev.setEnabled(hasPrevious);
            binding.nowPlayingNext.setEnabled(hasNext);
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
        }

    }

}
