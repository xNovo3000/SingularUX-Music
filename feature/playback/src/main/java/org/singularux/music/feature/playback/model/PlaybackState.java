package org.singularux.music.feature.playback.model;

import lombok.Value;
import lombok.experimental.Accessors;

@Value
public class PlaybackState {
    @Accessors(fluent = true) boolean isReady;
    @Accessors(fluent = true) boolean isPlaying;
    @Accessors(fluent = true) boolean hasNextItem;
}