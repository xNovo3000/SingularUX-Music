package org.singularux.music.feature.tracklist.ui.list;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

import org.singularux.music.feature.tracklist.ui.list.item.TrackListItem;

import java.util.Objects;

final class TrackListItemDiffCallback extends DiffUtil.ItemCallback<TrackListItem> {

    @Override
    public boolean areItemsTheSame(
            @NonNull TrackListItem oldItem,
            @NonNull TrackListItem newItem
    ) {
        return oldItem.getId() == newItem.getId();
    }

    @Override
    public boolean areContentsTheSame(
            @NonNull TrackListItem oldItem,
            @NonNull TrackListItem newItem
    ) {
        return Objects.equals(oldItem, newItem);
    }

}
