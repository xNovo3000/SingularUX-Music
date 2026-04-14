package org.singularux.music.feature.library.presentation;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.core.splashscreen.SplashScreen;
import androidx.fragment.app.FragmentActivity;

import org.singularux.music.core.playback.MusicControllerFacade;
import org.singularux.music.feature.library.databinding.ActivityLibraryBinding;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class LibraryActivity extends FragmentActivity {

    public @Inject MusicControllerFacade musicControllerFacade;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        // Start activity with splash screen and edge-to-edge enabled
        // Keep splash screen until controller is ready
        SplashScreen.installSplashScreen(this)
                .setKeepOnScreenCondition(() -> !musicControllerFacade.isReady());
        EdgeToEdge.enable(this);
        super.onCreate(savedInstanceState);
        // Populate view
        ActivityLibraryBinding binding = ActivityLibraryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }

    @Override
    protected void onDestroy() {
        // Release current session before destroying
        musicControllerFacade.release();
        super.onDestroy();
    }

}
