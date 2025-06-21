package org.singularux.music.feature.playback.domain.model;

import androidx.annotation.NonNull;

import java.util.List;

import lombok.Data;

@Data
public class PlaybackQueue {
    private final @NonNull List<PlaybackItemInfo> items;
    private final int currentlyPlayingIndex;
    // Maybe??
}
