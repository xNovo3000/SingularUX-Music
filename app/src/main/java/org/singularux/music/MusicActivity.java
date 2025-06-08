package org.singularux.music;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.core.splashscreen.SplashScreen;
import androidx.fragment.app.FragmentActivity;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MusicActivity extends FragmentActivity {

    public MusicActivity() {
        super(R.layout.activity_music);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        SplashScreen.installSplashScreen(this);
        EdgeToEdge.enable(this);
        super.onCreate(savedInstanceState);
    }

}
