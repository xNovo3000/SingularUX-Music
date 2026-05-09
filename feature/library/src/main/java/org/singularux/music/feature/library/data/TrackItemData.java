package org.singularux.music.feature.library.data;

import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;

import java.time.Duration;

import lombok.Value;

@Value
public class TrackItemData {

    long id;
    @NonNull String title;
    @Nullable String artistName;
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

}
