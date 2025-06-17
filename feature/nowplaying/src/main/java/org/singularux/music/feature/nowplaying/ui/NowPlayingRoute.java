package org.singularux.music.feature.nowplaying.ui;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.squareup.picasso.Picasso;

import org.singularux.music.feature.nowplaying.R;
import org.singularux.music.feature.nowplaying.databinding.RouteNowPlayingBinding;
import org.singularux.music.feature.nowplaying.ui.inset.ContainerInsetListener;
import org.singularux.music.feature.nowplaying.ui.observer.PlaybackInfoObserver;
import org.singularux.music.feature.nowplaying.ui.observer.PlaybackPositionObserver;
import org.singularux.music.feature.nowplaying.ui.observer.ProgressSliderListener;
import org.singularux.music.feature.nowplaying.ui.utils.SliderDurationLabelFormatter;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class NowPlayingRoute extends Fragment {

    public @Inject ContainerInsetListener containerInsetListener;
    public @Inject SliderDurationLabelFormatter sliderDurationLabelFormatter;
    public @Inject Picasso picasso;

    public NowPlayingRoute() {
        super(R.layout.route_now_playing);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        RouteNowPlayingBinding binding = RouteNowPlayingBinding.bind(view);
        NowPlayingViewModel viewModel = new ViewModelProvider(this)
                .get(NowPlayingViewModel.class);
        // Create observers and listeners
        PlaybackPositionObserver playbackPositionObserver =
                new PlaybackPositionObserver(requireContext(), binding);
        ProgressSliderListener progressSliderListener = new ProgressSliderListener(viewModel,
                getViewLifecycleOwner(), playbackPositionObserver);
        PlaybackInfoObserver playbackInfoObserver = new PlaybackInfoObserver(requireContext(),
                binding, viewModel, picasso, progressSliderListener, sliderDurationLabelFormatter);
        // Add inset listeners
        ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(), containerInsetListener);
        // Add custom formatters
        binding.nowPlayingProgress.setLabelFormatter(sliderDurationLabelFormatter);
        // Listen for navigation actions
        NavController navController = NavHostFragment.findNavController(this);
        binding.nowPlayingClose.setOnClickListener(v -> navController.navigateUp());
        // Listen for data
        viewModel.getPlaybackPosition().observe(getViewLifecycleOwner(), playbackPositionObserver);
        viewModel.getPlaybackInfo().observe(getViewLifecycleOwner(), playbackInfoObserver);
        binding.nowPlayingProgress.addOnChangeListener(progressSliderListener);
        binding.nowPlayingProgress.addOnSliderTouchListener(progressSliderListener);
    }

}
