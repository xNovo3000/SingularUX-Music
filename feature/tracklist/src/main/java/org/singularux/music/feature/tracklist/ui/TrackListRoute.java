package org.singularux.music.feature.tracklist.ui;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.progressindicator.LinearProgressIndicator;

import org.singularux.music.core.permission.MusicPermissionManager;
import org.singularux.music.feature.playback.model.PlaybackInfo;
import org.singularux.music.feature.playback.model.PlaybackPosition;
import org.singularux.music.feature.tracklist.R;
import org.singularux.music.feature.tracklist.databinding.ComponentPlaybackBarBinding;
import org.singularux.music.feature.tracklist.databinding.RouteTrackListBinding;
import org.singularux.music.feature.tracklist.ui.inset.PlaybackBarInsetListener;
import org.singularux.music.feature.tracklist.ui.inset.TrackListInsetListener;
import org.singularux.music.feature.tracklist.ui.inset.TrackListSearchBarInsetListener;
import org.singularux.music.feature.tracklist.viewmodel.TrackListViewModel;

import java.util.Optional;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import lombok.RequiredArgsConstructor;

@AndroidEntryPoint
public class TrackListRoute extends Fragment {

    @Inject public MusicPermissionManager musicPermissionManager;
    @Inject public PlaybackBarInsetListener playbackBarInsetListener;
    @Inject public TrackListSearchBarInsetListener trackListSearchBarInsetListener;
    @Inject public TrackListInsetListener trackListInsetListener;
    @Inject public TrackListAdapter trackListAdapter;

    public TrackListRoute() {
        super(R.layout.route_track_list);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        RouteTrackListBinding binding = RouteTrackListBinding.bind(view);
        TrackListViewModel viewModel = new ViewModelProvider(this)
                .get(TrackListViewModel.class);
        // Add InsetListeners
        ViewCompat.setOnApplyWindowInsetsListener(
                binding.trackListSearchBar, trackListSearchBarInsetListener);
        ViewCompat.setOnApplyWindowInsetsListener(
                binding.trackListRecyclerview, trackListInsetListener);
        ViewCompat.setOnApplyWindowInsetsListener(
                binding.playbackBar.playbackBarContainer, playbackBarInsetListener);
        // Set binders and adapters
        binding.trackListRecyclerview.setAdapter(trackListAdapter);
        viewModel.getTracks().observe(getViewLifecycleOwner(),
                trackItems -> trackListAdapter.submitList(trackItems));
        viewModel.getPlaybackPosition().observe(getViewLifecycleOwner(),
                new PlaybackPositionObserver(binding.playbackBar.playbackBarProgress));
        viewModel.getPlaybackInfo().observe(getViewLifecycleOwner(),
                new PlaybackInfoObserver(binding.playbackBar, requireContext()));
    }

    @RequiredArgsConstructor
    private static class PlaybackPositionObserver implements Observer<PlaybackPosition> {

        private final LinearProgressIndicator playbackBarProgress;

        @Override
        public void onChanged(PlaybackPosition playbackPosition) {
            int position = (int) (playbackPosition.getPosition() * 1000.0F);
            playbackBarProgress.setProgress(position);
        }

    }

    @RequiredArgsConstructor
    private static class PlaybackInfoObserver implements Observer<Optional<PlaybackInfo>> {

        private final ComponentPlaybackBarBinding binding;
        private final Context baseContext;

        @Override
        public void onChanged(Optional<PlaybackInfo> maybePlaybackInfo) {
            Context context = ContextCompat.getContextForLanguage(baseContext);
            // Extract data
            String title;
            String artist;
            Drawable icon;
            boolean enabled;
            if (maybePlaybackInfo.isPresent()) {
                PlaybackInfo playbackInfo = maybePlaybackInfo.get();
                if (playbackInfo.getTitle() != null) {
                    title = playbackInfo.getTitle();
                } else {
                    title = context.getString(R.string.track_item_unknown_track);
                }
                if (playbackInfo.getArtistsName() != null) {
                    artist = playbackInfo.getArtistsName();
                } else {
                    artist = context.getString(R.string.track_item_unknown_artist);
                }
                if (playbackInfo.isPlaying()) {
                    icon = ContextCompat.getDrawable(context, R.drawable.round_pause_24);
                } else {
                    icon = ContextCompat.getDrawable(context, R.drawable.round_play_arrow_24);
                }
                enabled = true;
            } else {
                title = context.getString(R.string.track_item_unknown_track);
                artist = context.getString(R.string.track_item_unknown_artist);
                icon = ContextCompat.getDrawable(context, R.drawable.round_play_arrow_24);
                enabled = false;
            }
            // Apply
            binding.playbackBarTitle.setText(title);
            binding.playbackBarArtist.setText(artist);
            binding.playbackBarPlayPause.setIcon(icon);
            binding.playbackBarPlayPause.setEnabled(enabled);
            // TODO: Artwork
        }

    }

}
