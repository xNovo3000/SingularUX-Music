package org.singularux.music.feature.nowplaying.ui.utils;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.slider.LabelFormatter;

import org.singularux.music.feature.nowplaying.R;

import java.time.Duration;

import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
public class SliderDurationLabelFormatter implements LabelFormatter {

    private @Setter @Nullable Duration contentDuration = null;
    private final Context context;

    @Override
    public @NonNull String getFormattedValue(float value) {
        // Extract the duration for that percentage of the track
        long contentDurationTotalSeconds = 0;
        if (contentDuration != null) {
            contentDurationTotalSeconds = contentDuration.getSeconds();
        }
        long sliderLabelTotalSeconds = (long) (contentDurationTotalSeconds * value);
        // Extract minutes and seconds part
        long sliderLabelSecondsPart = sliderLabelTotalSeconds % 60;
        long sliderLabelMinutesPart = sliderLabelTotalSeconds / 60;
        // Build the string
        return context.getString(R.string.now_playing_duration,
                sliderLabelMinutesPart, sliderLabelSecondsPart);
    }

}
