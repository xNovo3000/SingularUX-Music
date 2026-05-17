package org.singularux.music.feature.library.data;

import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;

import org.singularux.music.feature.playback.data.QueueItem;
import org.singularux.music.feature.playback.data.TimelineAction;
import org.singularux.music.feature.playback.data.TrackDto;

import java.time.Duration;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;

import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.Value;

public abstract class SearchItemData {

    public abstract int getViewType();
    public abstract long getUniqueId();

    @Value
    @EqualsAndHashCode(callSuper = false)
    public static class Track extends SearchItemData {

        public static final int VIEW_TYPE = 1;

        long id;
        @NonNull String title;
        @Nullable Long artistId;
        @Nullable String artistName;
        @Nullable Long albumId;
        @Nullable String albumTitle;
        @NonNull Duration duration;
        @NonNull Uri uri;
        @Nullable Uri artworkPath;
        boolean isPlaying;

        @Override
        public int getViewType() {
            return VIEW_TYPE;
        }

        @Override
        public long getUniqueId() {
            return ((long) VIEW_TYPE << 32) | Objects.hash(id);
        }

        public static final class Filter implements Predicate<SearchItemData> {

            @Override
            public boolean test(SearchItemData searchItemData) {
                return searchItemData instanceof Track;
            }

        }

        public static final class MapAfterFilter implements Function<SearchItemData, Track> {

            @Override
            public @NonNull Track apply(@NonNull SearchItemData item) {
                return (Track) item;
            }

        }

        public static final class ToTrackDtoMapper implements Function<Track, TrackDto> {

            private static final String PLAYING_FROM = "search";

            @Override
            public @NonNull TrackDto apply(@NonNull Track item) {
                return new TrackDto(item.id, item.title, item.artistId, item.artistName,
                        item.albumId, item.albumTitle, item.duration,
                        item.uri, item.artworkPath, PLAYING_FROM);
            }

        }

    }

    public static final class Differ extends DiffUtil.ItemCallback<SearchItemData> {

        @Override
        public boolean areItemsTheSame(@NonNull SearchItemData oldItem,
                                       @NonNull SearchItemData newItem) {
            return newItem.getUniqueId() == oldItem.getUniqueId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull SearchItemData oldItem,
                                          @NonNull SearchItemData newItem) {
            return Objects.equals(oldItem, newItem);
        }

    }

}
