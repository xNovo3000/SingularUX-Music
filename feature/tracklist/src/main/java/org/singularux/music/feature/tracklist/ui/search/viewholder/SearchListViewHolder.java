package org.singularux.music.feature.tracklist.ui.search.viewholder;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import org.singularux.music.feature.tracklist.ui.search.SearchListOnClickListener;
import org.singularux.music.feature.tracklist.ui.search.model.SearchListItem;

public abstract class SearchListViewHolder extends RecyclerView.ViewHolder {

    public SearchListViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    public abstract void onBind(int position,
                                @NonNull SearchListItem item,
                                @NonNull Picasso picasso,
                                @Nullable SearchListOnClickListener onClickListener);

}
