package org.singularux.music.feature.playback.data;

import lombok.Value;
import lombok.experimental.Accessors;

@Value
@Accessors(fluent = true)
public class PlaybackState {
    boolean isReady;
    boolean isPlaying;
    boolean hasNextItem;
}
