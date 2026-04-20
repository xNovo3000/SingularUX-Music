package org.singularux.music.feature.library.data;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

public class TrackItemDataDiffItemCallback extends DiffUtil.ItemCallback<TrackItemData> {

    @Override
    public boolean areItemsTheSame(@NonNull TrackItemData oldItem,
                                   @NonNull TrackItemData newItem) {
        return oldItem.getId() == newItem.getId();
    }

    @Override
    public boolean areContentsTheSame(@NonNull TrackItemData oldItem,
                                      @NonNull TrackItemData newItem) {
        return oldItem.equals(newItem);
    }

}
