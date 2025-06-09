package org.singularux.music.feature.tracklist.ui;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import org.singularux.music.feature.tracklist.R;
import org.singularux.music.feature.tracklist.databinding.RouteTrackListBinding;
import org.singularux.music.feature.tracklist.ui.inset.TrackListSearchBarInsetListener;
import org.singularux.music.feature.tracklist.viewmodel.TrackListViewModel;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class TrackListRoute extends Fragment {

    @Inject public TrackListSearchBarInsetListener trackListSearchBarInsetListener;
    @Inject public TrackListAdapter trackListAdapter;

    public RouteTrackListBinding binding;
    private TrackListViewModel viewModel;

    public TrackListRoute() {
        super(R.layout.route_track_list);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        binding = RouteTrackListBinding.bind(view);
        viewModel = new ViewModelProvider(this).get(TrackListViewModel.class);
        // Add InsetListeners
        ViewCompat.setOnApplyWindowInsetsListener(
                binding.trackListSearchBar, trackListSearchBarInsetListener);
        // Listen data
        binding.trackListRecyclerview.setAdapter(trackListAdapter);
        viewModel.getTracks().observe(getViewLifecycleOwner(),
                trackItems -> trackListAdapter.submitList(trackItems));
    }

}
