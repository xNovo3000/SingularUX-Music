package org.singularux.music.feature.nowplaying.ui;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.squareup.picasso.Picasso;

import org.singularux.music.feature.nowplaying.R;
import org.singularux.music.feature.nowplaying.databinding.RouteNowPlayingBinding;
import org.singularux.music.feature.nowplaying.ui.inset.ContainerInsetListener;
import org.singularux.music.feature.nowplaying.ui.observer.PlaybackItemInfoObserver;
import org.singularux.music.feature.nowplaying.ui.observer.PlaybackPositionObserver;
import org.singularux.music.feature.nowplaying.ui.observer.PlaybackStateObserver;
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
        NavController navController = NavHostFragment.findNavController(this);
        // Navigation - Back
        binding.nowPlayingClose.setOnClickListener(v -> navController.navigateUp());
        // Listeners - Insects
        ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(), containerInsetListener);
        // Listeners - Action
        PlaybackPositionObserver playbackPositionObserver = new PlaybackPositionObserver(
                ContextCompat.getContextForLanguage(requireContext()), binding);
        ProgressSliderListener progressSliderListener = new ProgressSliderListener(viewModel,
                getViewLifecycleOwner(), playbackPositionObserver);
        PlaybackItemInfoObserver playbackItemInfoObserver = new PlaybackItemInfoObserver(
                ContextCompat.getContextForLanguage(requireContext()), binding, picasso,
                progressSliderListener, sliderDurationLabelFormatter);
        PlaybackStateObserver playbackStateObserver = new PlaybackStateObserver(
                ContextCompat.getContextForLanguage(requireContext()), binding, viewModel);
        // Listen for data
        binding.nowPlayingProgress.setLabelFormatter(sliderDurationLabelFormatter);
        binding.nowPlayingProgress.addOnChangeListener(progressSliderListener);
        binding.nowPlayingProgress.addOnSliderTouchListener(progressSliderListener);
        viewModel.getPlaybackPosition().observe(getViewLifecycleOwner(), playbackPositionObserver);
        viewModel.getPlaybackItemInfo().observe(getViewLifecycleOwner(), playbackItemInfoObserver);
        viewModel.getPlaybackState().observe(getViewLifecycleOwner(), playbackStateObserver);
    }

}
