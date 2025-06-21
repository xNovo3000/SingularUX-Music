package org.singularux.music.feature.nowplaying.ui.observer;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.slider.Slider;

import org.singularux.music.feature.nowplaying.ui.NowPlayingViewModel;

import java.time.Duration;

import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
public class ProgressSliderListener
        implements Slider.OnChangeListener, Slider.OnSliderTouchListener {

    private final NowPlayingViewModel viewModel;
    private final PlaybackPositionObserver playbackPositionObserver;

    private float newValue = 0.0F;
    private @Setter @Nullable Duration duration = null;

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
        playbackPositionObserver.setShouldUpdateSlider(false);
    }

    @Override
    public void onStopTrackingTouch(@NonNull Slider slider) {
        // Seek to where the user wants and restart observing
        if (duration != null) {
            viewModel.seekTo((long) (newValue * duration.toMillis()));
        }
        playbackPositionObserver.setShouldUpdateSlider(true);
    }

}
