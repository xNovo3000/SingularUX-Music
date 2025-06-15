package org.singularux.music.feature.playback.ui;

import androidx.fragment.app.Fragment;

import org.singularux.music.feature.playback.R;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class NowPlayingRoute extends Fragment {

    public NowPlayingRoute() {
        super(R.layout.route_now_playing);
    }

}
