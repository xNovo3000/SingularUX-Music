package org.singularux.music.feature.library.presentation;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.AsyncDifferConfig;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;

import org.singularux.music.feature.library.data.TrackItemData;

import java.util.concurrent.ExecutorService;

public class TrackListAdapter extends ListAdapter<TrackItemData, TrackItemViewHolder> {

    public TrackListAdapter(@NonNull DiffUtil.ItemCallback<TrackItemData> diffCallback,
                            ExecutorService computationExecutorService) {
        super(new AsyncDifferConfig.Builder<>(diffCallback)
                .setBackgroundThreadExecutor(computationExecutorService)
                .build());
        setHasStableIds(true);
        setStateRestorationPolicy(StateRestorationPolicy.PREVENT_WHEN_EMPTY);
    }

    @Override
    public @NonNull TrackItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                                           int viewType) {
        return TrackItemViewHolder.create(parent);
    }

    @Override
    public void onBindViewHolder(@NonNull TrackItemViewHolder holder, int position) {

    }

}
