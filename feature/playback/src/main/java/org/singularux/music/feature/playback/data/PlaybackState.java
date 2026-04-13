package org.singularux.music.feature.playback.data;

import lombok.Value;
import lombok.experimental.Accessors;

@Value
public class PlaybackState {
    boolean isReady;
    boolean isPlaying;
    @Accessors(fluent = true) boolean hasNextItem;
}
