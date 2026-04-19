package org.singularux.music.feature.library.presentation;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.singularux.music.feature.library.R;
import org.singularux.music.feature.library.databinding.RouteTracksBinding;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class TracksRoute extends Fragment {

    public TracksRoute() {
        super(R.layout.route_tracks);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        RouteTracksBinding binding = RouteTracksBinding.bind(view);
    }

}
