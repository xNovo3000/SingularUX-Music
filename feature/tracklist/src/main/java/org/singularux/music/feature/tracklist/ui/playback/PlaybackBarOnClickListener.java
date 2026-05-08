package org.singularux.music.feature.tracklist.ui.playback;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.View;

import androidx.navigation.NavController;
import androidx.navigation.NavDeepLinkRequest;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PlaybackBarOnClickListener implements View.OnClickListener {

    private final NavController navController;
    private final Context context;

    @Override
    public void onClick(View v) {
        /* Old system
        NavDeepLinkRequest request = NavDeepLinkRequest.Builder
                .fromUri(Uri.parse("nav://org.singularux.music/now_playing"))
                .build();
        navController.navigate(request);
        */
        String className = "org.singularux.music.feature.nowplaying.presentation.NowPlaying2Activity";
        try {
            context.startActivity(new Intent(context, Class.forName(className)));
        } catch (ClassNotFoundException e) {
            Log.e("PlaybackBarOnClickListener", "Cannot start activity");
        }
    }

}
