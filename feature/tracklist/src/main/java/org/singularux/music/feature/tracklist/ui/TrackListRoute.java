package org.singularux.music.feature.tracklist.ui;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;

import org.singularux.music.feature.tracklist.R;
import org.singularux.music.feature.tracklist.databinding.RouteTrackListBinding;
import org.singularux.music.feature.tracklist.ui.inset.TrackListSearchBarInsetListener;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class TrackListRoute extends Fragment {

    @Inject public TrackListSearchBarInsetListener trackListSearchBarInsetListener;
    @Inject public TrackListAdapter trackListAdapter;

    public RouteTrackListBinding binding;

    public TrackListRoute() {
        super(R.layout.route_track_list);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        binding = RouteTrackListBinding.bind(view);
        // Add InsetListeners
        ViewCompat.setOnApplyWindowInsetsListener(
                binding.trackListSearchBar, trackListSearchBarInsetListener);
        // Set data
        binding.trackListRecyclerview.setAdapter(trackListAdapter);
    }

}
