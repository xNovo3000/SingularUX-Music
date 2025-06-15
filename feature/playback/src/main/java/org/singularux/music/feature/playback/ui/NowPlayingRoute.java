package org.singularux.music.feature.playback.ui;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import org.singularux.music.feature.playback.R;
import org.singularux.music.feature.playback.databinding.RouteNowPlayingBinding;
import org.singularux.music.feature.playback.ui.listener.ContainerInsetListener;
import org.singularux.music.feature.playback.viewmodel.NowPlayingViewModel;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class NowPlayingRoute extends Fragment {

    @Inject public ContainerInsetListener containerInsetListener;

    public NowPlayingRoute() {
        super(R.layout.route_now_playing);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        RouteNowPlayingBinding binding = RouteNowPlayingBinding.bind(view);
        NowPlayingViewModel nowPlayingViewModel = new ViewModelProvider(this)
                .get(NowPlayingViewModel.class);
        NavController navController = NavHostFragment.findNavController(this);
        // Add back button callbacks
        binding.nowPlayingClose.setOnClickListener(v -> navController.navigateUp());
        // Add listeners
        ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(), containerInsetListener);
    }

}
