package org.singularux.music.feature.library.presentation;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.core.splashscreen.SplashScreen;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;

import org.singularux.music.core.playback.MusicControllerFacade;
import org.singularux.music.feature.library.R;
import org.singularux.music.feature.library.databinding.ActivityLibraryBinding;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class LibraryActivity extends FragmentActivity {

    public @Inject MusicControllerFacade musicControllerFacade;

    private ActivityLibraryBinding binding;
    private LibraryViewModel viewModel;

    public LibraryActivity() {
        super(R.layout.activity_library);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        // Create activity with edge-to-edge support and splash screen until controller is ready
        SplashScreen.installSplashScreen(this)
                .setKeepOnScreenCondition(() -> !musicControllerFacade.isReady());
        EdgeToEdge.enable(this);
        super.onCreate(savedInstanceState);
        // Extract XML elements
        binding = ActivityLibraryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        // TODO: Add inset listeners
        // Extract ViewModel
        viewModel = new ViewModelProvider(this).get(LibraryViewModel.class);
        // TODO: Add static action listeners
        // TODO: Listen data
    }

    @Override
    protected void onDestroy() {
        // Release controller before finishing this activity
        if (isFinishing())
            musicControllerFacade.release();
        super.onDestroy();
    }

}
