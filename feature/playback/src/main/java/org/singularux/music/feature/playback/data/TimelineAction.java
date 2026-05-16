package org.singularux.music.feature.playback.data;

import androidx.annotation.NonNull;

import java.util.List;

import lombok.EqualsAndHashCode;
import lombok.Value;

public class TimelineAction {

    @Value
    @EqualsAndHashCode(callSuper = false)
    public static class ReplaceMediaItems extends TimelineAction {
        @NonNull List<QueueItem> queueItemList;
        int index;
        boolean shuffled;
    }

    @Value
    @EqualsAndHashCode(callSuper = false)
    public static class AddToCustomQueue extends TimelineAction {
        @NonNull QueueItem mediaItem;
    }

}
