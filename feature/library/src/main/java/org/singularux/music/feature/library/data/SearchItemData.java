package org.singularux.music.feature.library.data;

import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;

import java.time.Duration;
import java.util.Objects;

import lombok.EqualsAndHashCode;
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
