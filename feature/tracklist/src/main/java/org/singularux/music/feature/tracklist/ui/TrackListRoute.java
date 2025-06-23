package org.singularux.music.feature.tracklist.ui;

import android.os.Bundle;
import android.view.View;

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
import org.singularux.music.feature.tracklist.ui.observer.PlaybackItemInfoObserver;
import org.singularux.music.feature.tracklist.ui.observer.PlaybackStateObserver;
import org.singularux.music.feature.tracklist.ui.search.SearchViewOnBackPressedCallback;
import org.singularux.music.feature.tracklist.ui.observer.SearchViewTextChangedListener;
import org.singularux.music.feature.tracklist.ui.search.SearchViewTransitionObserver;
import org.singularux.music.feature.tracklist.ui.inset.PlaybackBarInsetListener;
import org.singularux.music.feature.tracklist.ui.inset.TrackListInsetListener;
import org.singularux.music.feature.tracklist.ui.search.SearchBarInsetListener;
import org.singularux.music.feature.tracklist.ui.observer.OnNavigateToNowPlayingClickListener;
import org.singularux.music.feature.tracklist.ui.observer.PlaybackPositionObserver;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class TrackListRoute extends Fragment {

    @Inject public PlaybackBarInsetListener playbackBarInsetListener;
    @Inject public SearchBarInsetListener searchBarInsetListener;
    @Inject public TrackListInsetListener trackListInsetListener;

    @Inject public SearchViewOnBackPressedCallback searchViewOnBackPressedCallback;
    @Inject public SearchViewTransitionObserver searchViewTransitionObserver;
    public @Inject SearchViewTextChangedListener searchViewTextChangedListener;

    @Inject public MusicPermissionManager musicPermissionManager;
    @Inject public TrackListAdapter trackListAdapter;
    public @Inject TrackListAdapter searchTrackListAdapter;
    @Inject public Picasso picasso;

    public TrackListRoute() {
        super(R.layout.route_track_list);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        RouteTrackListBinding binding = RouteTrackListBinding.bind(view);
        TrackListViewModel viewModel = new ViewModelProvider(this)
                .get(TrackListViewModel.class);
        NavController navController = NavHostFragment.findNavController(this);
        // Navigation - Search View
        searchViewOnBackPressedCallback.setSearchView(binding.trackListSearchView);
        binding.trackListSearchView.addTransitionListener(searchViewTransitionObserver);
        requireActivity().getOnBackPressedDispatcher()
                .addCallback(getViewLifecycleOwner(), searchViewOnBackPressedCallback);
        // Navigation - Now Playing
        binding.playbackBar.playbackBarContainer
                .setOnClickListener(new OnNavigateToNowPlayingClickListener(navController));
        // Inset listeners
        ViewCompat.setOnApplyWindowInsetsListener(
                binding.trackListSearchBar, searchBarInsetListener);
        ViewCompat.setOnApplyWindowInsetsListener(
                binding.trackListRecyclerview, trackListInsetListener);
        ViewCompat.setOnApplyWindowInsetsListener(
                binding.playbackBar.playbackBarContainer, playbackBarInsetListener);
        // Permission
        ActivityResultLauncher<String> readMusicPermissionRequest = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                result -> {
                    if (result) {
                        viewModel.getTrackList().observe(getViewLifecycleOwner(),
                                trackItems -> trackListAdapter.submitList(trackItems));
                    }
                });
        // Adapters
        trackListAdapter.setOnItemClickListener(viewModel::playFromSpecificTrackListIndex);
        binding.trackListRecyclerview.setAdapter(trackListAdapter);
        searchTrackListAdapter.setOnItemClickListener(viewModel::playSpecificTrackListIndex);
        binding.trackListSearchRecyclerview.setAdapter(searchTrackListAdapter);
        // Callbacks
        binding.trackListSearchView.getEditText()
                .addTextChangedListener(searchViewTextChangedListener);
        // Listen data
        readMusicPermissionRequest.launch(musicPermissionManager.getPermissionString(MusicPermission.READ_MUSIC));
        viewModel.getPlaybackPosition().observe(getViewLifecycleOwner(),
                new PlaybackPositionObserver(binding));
        viewModel.getPlaybackItemInfo().observe(getViewLifecycleOwner(),
                new PlaybackItemInfoObserver(ContextCompat.getContextForLanguage(requireContext()),
                        binding, picasso));
        viewModel.getPlaybackState().observe(getViewLifecycleOwner(),
                new PlaybackStateObserver(ContextCompat.getContextForLanguage(requireContext()),
                        binding, viewModel));
        viewModel.getSearchTrackList().observe(getViewLifecycleOwner(),
                trackItems -> searchTrackListAdapter.submitList(trackItems));
    }

}
