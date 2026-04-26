package org.singularux.music.feature.tracklist.ui.search;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.AsyncDifferConfig;
import androidx.recyclerview.widget.ListAdapter;

import com.squareup.picasso.Picasso;

import org.singularux.music.core.threading.ComputationExecutorService;
import org.singularux.music.feature.tracklist.R;
import org.singularux.music.feature.tracklist.ui.search.item.SearchListItem;
import org.singularux.music.feature.tracklist.ui.search.item.SearchListItemTrack;
import org.singularux.music.feature.tracklist.ui.search.viewholder.SearchListViewHolder;
import org.singularux.music.feature.tracklist.ui.search.viewholder.SearchListViewHolderTrack;

import java.util.concurrent.ExecutorService;

import javax.inject.Inject;

import lombok.Setter;

public class SearchListAdapter extends ListAdapter<SearchListItem, SearchListViewHolder> {

    private final Picasso picasso;
    private @Setter @Nullable SearchListOnClickListener onItemClickListener = null;

    @Inject
    public SearchListAdapter(Picasso picasso,
                             @ComputationExecutorService ExecutorService executorService) {
        super(new AsyncDifferConfig.Builder<>(new SearchListItemDiffCallback())
                .setBackgroundThreadExecutor(executorService)
                .build());
        setHasStableIds(true);
        this.picasso = picasso;
    }

    @NonNull
    @Override
    public SearchListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == SearchListViewHolderTrack.VIEW_TYPE) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.component_search_item_track, parent, false);
            return new SearchListViewHolderTrack(view);
        }
        throw new IllegalArgumentException("viewType is invalid: " + viewType);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchListViewHolder holder, int position) {
        // Strategy pattern embedded into VH
        holder.onBind(position, getItem(position), picasso, onItemClickListener);
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).getId();
    }

    @Override
    public int getItemViewType(int position) {
        if (getItem(position) instanceof SearchListItemTrack) {
            return SearchListViewHolderTrack.VIEW_TYPE;
        }
        return -1;
    }

}
