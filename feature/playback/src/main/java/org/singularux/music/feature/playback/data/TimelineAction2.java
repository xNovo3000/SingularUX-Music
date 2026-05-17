package org.singularux.music.feature.playback.data;

import androidx.annotation.NonNull;

import java.util.List;

import lombok.EqualsAndHashCode;
import lombok.Value;

public abstract class TimelineAction2 {

    @Value
    @EqualsAndHashCode(callSuper = false)
    public static class ReplaceMediaItems extends TimelineAction2 {
        @NonNull List<TrackDto> trackDtoList;
        int index;
        boolean shuffled;
    }

    @Value
    @EqualsAndHashCode(callSuper = false)
    public static class AddToCustomQueue extends TimelineAction2 {
        @NonNull TrackDto trackDto;
    }

}
