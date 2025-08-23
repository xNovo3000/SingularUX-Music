package org.singularux.music;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.core.splashscreen.SplashScreen;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import org.singularux.music.core.playback.MusicControllerFacade;
import org.singularux.music.core.playback.MusicPlaybackService;

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

        // Setup splash screen and edge-to-edge
        SplashScreen.installSplashScreen(this)
                .setKeepOnScreenCondition(() -> !musicControllerFacade.isReady());
        EdgeToEdge.enable(this);
        super.onCreate(savedInstanceState);

        // This VM is required because when the activity is being killed for the last time
        // the controller resources are released
        //noinspection unused
        MusicViewModel viewModel = new ViewModelProvider(this).get(MusicViewModel.class);

        // Add listener for notification click listener
        // TODO: Works with duct tape, should be refactored better
        addOnNewIntentListener(intent -> {
            if (Objects.equals(intent.getStringExtra("origin"), MusicPlaybackService.INTENT_ORIGIN)) {
                NavController navController = Navigation
                        .findNavController(this, R.id.navigation_root);
                if (Objects.requireNonNull(navController.getCurrentDestination()).getId() == R.id.track_list) {
                    navController.navigate(R.id.track_list_go_to_now_playing);
                }
            }
        });

    }

}
