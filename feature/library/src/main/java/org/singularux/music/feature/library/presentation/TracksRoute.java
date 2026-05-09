package org.singularux.music.feature.library.presentation;

import androidx.fragment.app.Fragment;

import org.singularux.music.feature.library.R;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class TracksRoute extends Fragment {

    public TracksRoute() {
        super(R.layout.route_track_list);
    }

}
