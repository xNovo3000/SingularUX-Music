package org.singularux.music.feature.library.data;

import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;

import org.singularux.music.feature.playback.data.TimelineAction;

import java.time.Duration;
import java.util.function.Function;

import lombok.RequiredArgsConstructor;
import lombok.Value;

@Value
public class TrackItemData {

    long id;
    @NonNull String title;
    @Nullable Long artistId;
    @Nullable String artistName;
    @Nullable Long albumId;
    @Nullable String albumTitle;
    @NonNull Duration duration;
    @Nullable Uri artworkPath;
    boolean isPlaying;

    public static final class Differ extends DiffUtil.ItemCallback<TrackItemData> {

        @Override
        public boolean areItemsTheSame(@NonNull TrackItemData oldItem,
                                       @NonNull TrackItemData newItem) {
            return newItem.id == oldItem.id;
        }

        @Override
        public boolean areContentsTheSame(@NonNull TrackItemData oldItem,
                                          @NonNull TrackItemData newItem) {
            return newItem.equals(oldItem);
        }

    }

    @RequiredArgsConstructor
    public static final class ToTimelineMediaItemMapper
            implements Function<TrackItemData, TimelineAction.MediaItem> {

        private final String playingFrom;

        @Override
        public @NonNull TimelineAction.MediaItem apply(@NonNull TrackItemData item) {
            return new TimelineAction.MediaItem(item.id, item.title,
                    item.artistId, item.artistName, item.albumId, item.albumTitle,
                    item.duration, item.artworkPath, playingFrom);
        }

    }

}
