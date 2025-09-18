package org.singularux.music.feature.playback.domain.model;

import androidx.annotation.NonNull;

import java.util.List;

import lombok.Value;

@Value
public class PlaybackQueue {
    @NonNull List<PlaybackItemInfo> items;
    int currentlyPlayingIndex;
}