package org.singularux.music.feature.nowplaying.ui.observer;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;

import com.google.android.material.slider.Slider;

import org.singularux.music.feature.nowplaying.ui.NowPlayingViewModel;
import org.singularux.music.feature.playback.model.PlaybackPosition;

import java.time.Duration;

import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
public class ProgressSliderListener implements Slider.OnChangeListener, Slider.OnSliderTouchListener {

    private final NowPlayingViewModel viewModel;
    private final LifecycleOwner lifecycleOwner;
    private final Observer<PlaybackPosition> playbackPositionObserver;

    private float newValue = 0.0F;
    private @Setter @Nullable Duration contentDuration = null;

    @Override
    public void onValueChange(@NonNull Slider slider, float value, boolean fromUser) {
        // Update newValue only if user is updating it
        if (fromUser) {
            newValue = value;
        }
    }

    @Override
    public void onStartTrackingTouch(@NonNull Slider slider) {
        // Stop observing
        viewModel.getPlaybackPosition().removeObserver(playbackPositionObserver);
    }

    @Override
    public void onStopTrackingTouch(@NonNull Slider slider) {
        // Seek to where the user wants and restart observing
        if (contentDuration != null) {
            viewModel.seekTo((long) (contentDuration.getSeconds() * newValue));
        }
        viewModel.getPlaybackPosition().observe(lifecycleOwner, playbackPositionObserver);
    }

}
