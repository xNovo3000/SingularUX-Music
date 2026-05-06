package org.singularux.music.feature.nowplaying.presentation;

import android.os.Bundle;

import androidx.activity.ComponentActivity;
import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import org.singularux.music.core.playback.MusicControllerFacade;
import org.singularux.music.feature.nowplaying.databinding.ActivityNowPlaying2Binding;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class NowPlaying2Activity extends ComponentActivity {

    private @Inject MusicControllerFacade musicControllerFacade;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        // Create activity with edge-to-edge support
        EdgeToEdge.enable(this);
        super.onCreate(savedInstanceState);
        // Extract XML elements
        ActivityNowPlaying2Binding binding = ActivityNowPlaying2Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        // TODO: Add inset listeners
        // Extract ViewModel data
        NowPlaying2ViewModel viewModel = new ViewModelProvider(this)
                .get(NowPlaying2ViewModel.class);
    }

    @Override
    protected void onDestroy() {
        // Release controller before finishing this activity
        if (isFinishing()) musicControllerFacade.release();
        super.onDestroy();
    }

}
