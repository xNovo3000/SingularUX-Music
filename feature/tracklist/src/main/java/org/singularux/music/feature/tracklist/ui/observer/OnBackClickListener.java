package org.singularux.music.feature.tracklist.ui.observer;

import android.net.Uri;
import android.view.View;

import androidx.navigation.NavController;
import androidx.navigation.NavDeepLinkRequest;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class OnBackClickListener implements View.OnClickListener {

    private final NavController navController;

    @Override
    public void onClick(View v) {
        NavDeepLinkRequest request = NavDeepLinkRequest.Builder
                .fromUri(Uri.parse("nav://org.singularux.music/now_playing"))
                .build();
        navController.navigate(request);
    }

}
