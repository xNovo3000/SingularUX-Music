package org.singularux.music.feature.playback.model;

import androidx.annotation.FloatRange;
import androidx.annotation.NonNull;

import java.time.Duration;

import lombok.Data;

@Data
public class PlaybackPosition {
    @FloatRange(from = 0.0, to = 1.0) private final float position;
    private final @NonNull Duration current;
}
