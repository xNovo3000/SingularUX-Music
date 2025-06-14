package org.singularux.music.feature.tracklist.model;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

import java.util.Objects;

public class TrackItemDiffCallback extends DiffUtil.ItemCallback<TrackItem> {

    @Override
    public boolean areItemsTheSame(@NonNull TrackItem oldItem, @NonNull TrackItem newItem) {
        return oldItem.getId() == newItem.getId();
    }

    @Override
    public boolean areContentsTheSame(@NonNull TrackItem oldItem, @NonNull TrackItem newItem) {
        return Objects.equals(oldItem, newItem);
    }

}
