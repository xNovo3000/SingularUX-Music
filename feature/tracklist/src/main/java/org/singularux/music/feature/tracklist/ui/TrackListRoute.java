package org.singularux.music.feature.tracklist.ui;

import androidx.fragment.app.Fragment;

import org.singularux.music.feature.tracklist.R;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class TrackListRoute extends Fragment {

    public TrackListRoute() {
        super(R.layout.route_track_list);
    }

}
