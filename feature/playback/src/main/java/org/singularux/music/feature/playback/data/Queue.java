package org.singularux.music.feature.playback.data;

import androidx.annotation.NonNull;

import java.util.List;

import lombok.Value;

@Value
public class Queue {
    @NonNull List<QueueItem> items;
}
