package org.singularux.music;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.core.splashscreen.SplashScreen;
import androidx.fragment.app.FragmentActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import org.singularux.music.feature.playback.foreground.MusicControllerFacade;

import java.util.Objects;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MusicActivity extends FragmentActivity {

    @Inject public MusicControllerFacade musicControllerFacade;

    public MusicActivity() {
        super(R.layout.activity_music);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        // Splash screen until service is ready
        SplashScreen.installSplashScreen(this)
                .setKeepOnScreenCondition(() -> !musicControllerFacade.isReady());
        // Edge-to-edge always
        EdgeToEdge.enable(this);
        // System
        super.onCreate(savedInstanceState);
        // Add listener for notification click listener
        // TODO: Works with duct tape, should be refactored better
        addOnNewIntentListener(intent -> {
            if (Objects.equals(intent.getStringExtra("origin"), "system_ui_notification")) {
                NavController navController = Navigation
                        .findNavController(this, R.id.navigation_root);
                if (Objects.requireNonNull(navController.getCurrentDestination()).getId() == R.id.track_list) {
                    navController.navigate(R.id.track_list_go_to_now_playing);
                }
            }
        });
    }

}
