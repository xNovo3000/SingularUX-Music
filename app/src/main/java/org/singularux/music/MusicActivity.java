package org.singularux.music;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.core.splashscreen.SplashScreen;
import androidx.fragment.app.FragmentActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import org.singularux.music.core.playback.MusicControllerFacade;
import org.singularux.music.feature.playback.presentation.MusicPlaybackService;

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
    }

    @Override
    protected void onDestroy() {
        // Destroy MediaController connection when the activity is stopping
        if (isFinishing()) {
            musicControllerFacade.release();
        }
        super.onDestroy();
    }

}
