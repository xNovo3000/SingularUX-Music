package org.singularux.music.feature.playback.data;

import androidx.annotation.FloatRange;

import java.time.Duration;

import lombok.Value;

@Value
public class PlaybackPosition {
    @FloatRange(from = 0.0, to = 1.0) float progress;
    Duration position;
}
