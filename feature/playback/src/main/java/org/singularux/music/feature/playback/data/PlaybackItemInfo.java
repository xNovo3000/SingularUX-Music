package org.singularux.music.feature.playback.data;

import androidx.annotation.Nullable;

import lombok.Value;

@Value
public class PlaybackItemInfo {
    @Nullable QueueItem queueItem;
}
