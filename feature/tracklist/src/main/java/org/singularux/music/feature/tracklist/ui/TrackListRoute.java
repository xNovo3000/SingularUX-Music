package org.singularux.music.feature.tracklist.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.squareup.picasso.Picasso;

import org.singularux.music.core.permission.MusicPermission;
import org.singularux.music.core.permission.MusicPermissionManager;
import org.singularux.music.feature.tracklist.R;
import org.singularux.music.feature.tracklist.databinding.RouteTrackListBinding;
import org.singularux.music.feature.tracklist.ui.list.TrackListAdapter;
import org.singularux.music.feature.tracklist.ui.observer.SearchViewOnBackPressedCallback;
import org.singularux.music.feature.tracklist.ui.observer.SearchViewTransitionObserver;
import org.singularux.music.feature.tracklist.ui.inset.PlaybackBarInsetListener;
import org.singularux.music.feature.tracklist.ui.inset.TrackListInsetListener;
import org.singularux.music.feature.tracklist.ui.inset.TrackListSearchBarInsetListener;
import org.singularux.music.feature.tracklist.ui.observer.OnBackClickListener;
import org.singularux.music.feature.tracklist.ui.observer.PlaybackInfoObserver;
import org.singularux.music.feature.tracklist.ui.observer.PlaybackPositionObserver;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class TrackListRoute extends Fragment {

    private static final String TAG = "TrackListRoute";

    @Inject public PlaybackBarInsetListener playbackBarInsetListener;
    @Inject public TrackListSearchBarInsetListener trackListSearchBarInsetListener;
    @Inject public TrackListInsetListener trackListInsetListener;

    @Inject public SearchViewOnBackPressedCallback searchViewOnBackPressedCallback;
    @Inject public SearchViewTransitionObserver searchViewTransitionObserver;

    @Inject public MusicPermissionManager musicPermissionManager;
    @Inject public TrackListAdapter trackListAdapter;
    @Inject public Picasso picasso;

    private TrackListViewModel viewModel;

    public TrackListRoute() {
        super(R.layout.route_track_list);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        RouteTrackListBinding binding = RouteTrackListBinding.bind(view);
        viewModel = new ViewModelProvider(this).get(TrackListViewModel.class);
        NavController navController = NavHostFragment.findNavController(this);
        // Add back button callbacks
        searchViewOnBackPressedCallback.setSearchView(binding.trackListSearchView);
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(),
                searchViewOnBackPressedCallback);
        // Add navigation callbacks
        binding.playbackBar.playbackBarContainer
                .setOnClickListener(new OnBackClickListener(navController));
        // Add listeners
        ViewCompat.setOnApplyWindowInsetsListener(
                binding.trackListSearchBar, trackListSearchBarInsetListener);
        ViewCompat.setOnApplyWindowInsetsListener(
                binding.trackListRecyclerview, trackListInsetListener);
        ViewCompat.setOnApplyWindowInsetsListener(
                binding.playbackBar.playbackBarContainer, playbackBarInsetListener);
        binding.trackListSearchView.addTransitionListener(searchViewTransitionObserver);
        trackListAdapter.setOnItemClickListener(viewModel::playFromSpecificTrackListIndex);
        // Request permission to read music
        ActivityResultLauncher<String> readMusicPermissionRequest = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                new RequestMusicPermissionResultCallback());
        // Set binders
        binding.trackListRecyclerview.setAdapter(trackListAdapter);
        // Listen for data
        readMusicPermissionRequest.launch(musicPermissionManager.getPermissionString(MusicPermission.READ_MUSIC));
        viewModel.getPlaybackPosition().observe(getViewLifecycleOwner(),
                new PlaybackPositionObserver(binding));
        viewModel.getPlaybackInfo().observe(getViewLifecycleOwner(),
                new PlaybackInfoObserver(ContextCompat.getContextForLanguage(requireContext()),
                        binding, picasso, viewModel));
    }

    private class RequestMusicPermissionResultCallback implements ActivityResultCallback<Boolean> {

        @Override
        public void onActivityResult(Boolean result) {
            if (result) {
                Log.i(TAG, "Permission READ_MUSIC granted");
                // Observe track list
                viewModel.getTrackList().observe(getViewLifecycleOwner(),
                        trackItems -> trackListAdapter.submitList(trackItems));
            } else {
                Log.i(TAG, "Permission READ_MUSIC denied");
            }
        }

    }

}
