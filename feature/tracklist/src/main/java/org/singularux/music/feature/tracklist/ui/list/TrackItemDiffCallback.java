package org.singularux.music.feature.tracklist.ui.list;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

import org.singularux.music.feature.tracklist.domain.model.TrackItem;

import java.util.Objects;

class TrackItemDiffCallback extends DiffUtil.ItemCallback<TrackItem> {

    @Override
    public boolean areItemsTheSame(@NonNull TrackItem oldItem, @NonNull TrackItem newItem) {
        return oldItem.getId() == newItem.getId();
    }

    @Override
    public boolean areContentsTheSame(@NonNull TrackItem oldItem, @NonNull TrackItem newItem) {
        return Objects.equals(oldItem, newItem);
    }

}
