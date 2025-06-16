package org.singularux.music.feature.nowplaying.ui.utils;

import android.content.Context;

import androidx.annotation.FloatRange;
import androidx.annotation.NonNull;

import com.google.android.material.slider.LabelFormatter;

import org.singularux.music.feature.nowplaying.R;

import java.time.Duration;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SliderDurationLabelFormatter implements LabelFormatter {

    private final Duration contentDuration;
    private final Context context;

    @Override
    public @NonNull String getFormattedValue(@FloatRange(from = 0.0, to = 1.0) float value) {
        // Extract the duration for that percentage of the track
        long contentDurationTotalSeconds = contentDuration.getSeconds();
        long sliderLabelTotalSeconds = (long) (contentDurationTotalSeconds * value);
        // Extract minutes and seconds part
        long sliderLabelSecondsPart = sliderLabelTotalSeconds % 60;
        long sliderLabelMinutesPart = sliderLabelTotalSeconds / 60;
        // Build the string
        return context.getString(R.string.now_playing_duration,
                sliderLabelMinutesPart, sliderLabelSecondsPart);
    }

}
