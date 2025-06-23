package org.singularux.music.feature.tracklist.ui.search;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

import org.singularux.music.feature.tracklist.ui.search.item.SearchListItem;

import java.util.Objects;

final class SearchListItemDiffCallback extends DiffUtil.ItemCallback<SearchListItem> {

    @Override
    public boolean areItemsTheSame(
            @NonNull SearchListItem oldItem,
            @NonNull SearchListItem newItem
    ) {
        return Objects.equals(oldItem.getClass(), newItem.getClass()) &&
                oldItem.getId() == newItem.getId();
    }

    @Override
    public boolean areContentsTheSame(
            @NonNull SearchListItem oldItem,
            @NonNull SearchListItem newItem
    ) {
        return Objects.equals(oldItem, newItem);
    }

}
