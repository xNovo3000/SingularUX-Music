package org.singularux.music.presentation;

import android.os.Bundle;

import androidx.activity.ComponentActivity;
import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.core.splashscreen.SplashScreen;
import androidx.core.view.ViewCompat;

import org.singularux.music.core.playback.MusicControllerFacade;
import org.singularux.music.databinding.ActivityHomeBinding;
import org.singularux.music.presentation.inset.HomeTrackListInsetListener;
import org.singularux.music.presentation.inset.HomeSearchBarInsetListener;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class HomeActivity extends ComponentActivity {

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
        ActivityHomeBinding binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        // Install inset listeners
        ViewCompat.setOnApplyWindowInsetsListener(binding.activityHomeSearchBar,
                new HomeSearchBarInsetListener());
        ViewCompat.setOnApplyWindowInsetsListener(binding.activityHomeTrackList,
                new HomeTrackListInsetListener());
    }

    @Override
    protected void onDestroy() {
        // Release current session before destroying
        musicControllerFacade.release();
        super.onDestroy();
    }

}
