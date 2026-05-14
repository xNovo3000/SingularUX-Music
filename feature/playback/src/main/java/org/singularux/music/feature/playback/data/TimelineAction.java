package org.singularux.music.feature.playback.data;

import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.time.Duration;
import java.util.List;

import lombok.EqualsAndHashCode;
import lombok.Value;

public class TimelineAction {

    @Value
    public static class MediaItem {
        long id;
        @NonNull String title;
        @Nullable Long artistId;
        @Nullable String artistName;
        @Nullable Long albumId;
        @Nullable String albumTitle;
        @Nullable Uri artworkPath;
        @NonNull Duration duration;
        @Nullable String playingFrom;
    }

    @Value
    @EqualsAndHashCode(callSuper = false)
    public static class ReplaceMediaItems extends TimelineAction {
        List<MediaItem> mediaItemList;
        int index;
    }

}
