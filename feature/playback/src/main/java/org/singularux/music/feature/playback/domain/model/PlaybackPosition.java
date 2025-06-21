package org.singularux.music.feature.playback.domain.model;

import androidx.annotation.FloatRange;
import androidx.annotation.NonNull;

import java.time.Duration;

import lombok.Data;

@Data
public class PlaybackPosition {
    private final @FloatRange(from = 0.0, to = 1.0) float currentPosition;
    private final @NonNull Duration contentDuration;
}
