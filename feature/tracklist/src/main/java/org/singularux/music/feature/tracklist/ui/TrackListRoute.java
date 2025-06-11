package org.singularux.music.feature.tracklist.ui;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.progressindicator.LinearProgressIndicator;

import org.singularux.music.core.permission.MusicPermissionManager;
import org.singularux.music.feature.playback.model.PlaybackPosition;
import org.singularux.music.feature.tracklist.R;
import org.singularux.music.feature.tracklist.databinding.RouteTrackListBinding;
import org.singularux.music.feature.tracklist.ui.inset.PlaybackBarInsetListener;
import org.singularux.music.feature.tracklist.ui.inset.TrackListInsetListener;
import org.singularux.music.feature.tracklist.ui.inset.TrackListSearchBarInsetListener;
import org.singularux.music.feature.tracklist.viewmodel.TrackListViewModel;

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

}
