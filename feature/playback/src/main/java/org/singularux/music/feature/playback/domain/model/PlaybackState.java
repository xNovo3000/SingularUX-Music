package org.singularux.music.feature.playback.domain.model;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
public class PlaybackState {
    private final @Accessors(fluent = true) boolean isReady;
    private final @Accessors(fluent = true) boolean isPlaying;
    private final @Accessors(fluent = true) boolean hasNextItem;
}
