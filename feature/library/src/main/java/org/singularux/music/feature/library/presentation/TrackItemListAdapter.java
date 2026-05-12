package org.singularux.music.feature.library.presentation;

import android.content.Context;
import android.net.Uri;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.AsyncDifferConfig;
import androidx.recyclerview.widget.ListAdapter;

import com.google.android.material.listitem.SwipeableListItem;
import com.squareup.picasso.Picasso;

import org.singularux.music.feature.library.R;
import org.singularux.music.feature.library.data.TrackItemData;

import java.time.Duration;
import java.util.concurrent.ExecutorService;

public class TrackItemListAdapter extends ListAdapter<TrackItemData, TrackItemViewHolder> {
    
    private final Picasso picasso;
    private final OnActionListener onActionListener;
    
    public TrackItemListAdapter(ExecutorService computationExecutorService,
                                Picasso picasso,
                                OnActionListener onActionListener) {
        super(new AsyncDifferConfig.Builder<>(new TrackItemData.Differ())
                .setBackgroundThreadExecutor(computationExecutorService)
                .build());
        this.picasso = picasso;
        this.onActionListener = onActionListener;
        setHasStableIds(true);
        setStateRestorationPolicy(StateRestorationPolicy.PREVENT_WHEN_EMPTY);
    }
    
    @Override
    public @NonNull TrackItemViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent, int viewType) {
        return TrackItemViewHolder.create(parent);
    }

    @Override
    public void onBindViewHolder(@NonNull TrackItemViewHolder holder, int position) {
        TrackItemData item = getItem(position);
        Context context = holder.itemView.getContext();
        // Extract data
        boolean isPlaying = item.isPlaying();
        String title = item.getTitle(), artistName;
        Duration duration = item.getDuration();
        Uri artworkPath = item.getArtworkPath();
        if (item.getArtistName() != null) {
            artistName = item.getArtistName();
        } else {
            artistName = context.getString(R.string.item_track_unknown_artist);
        }
        String durationArtist = context.getString(R.string.item_track_duration_artist,
                duration.getSeconds() / 60, duration.getSeconds() % 60, artistName);
        // Apply data
        holder.bind(position, getItemCount());
        holder.itemView.setSelected(isPlaying);
        holder.title.setText(title);
        holder.durationArtist.setText(durationArtist);
        if (artworkPath != null) {
            picasso.load(artworkPath)
                    .resizeDimen(R.dimen.item_track_artwork, R.dimen.item_track_artwork)
                    .into(holder.artwork);
        } else {
            picasso.cancelRequest(holder.artwork);
            holder.artwork.setImageDrawable(null);
        }
        // Apply listeners
        holder.viewContent.setOnClickListener(v ->
                onActionListener.onAction(position, Action.PLAY));
        holder.addToQueue.setOnClickListener(v -> {
            onActionListener.onAction(position, Action.ADD_TO_QUEUE);
            // Always return to non-swiped state
            holder.root.setSwipeState(SwipeableListItem.STATE_CLOSED, holder.viewScroll);
        });
    }

    @Override
    public void onViewRecycled(@NonNull TrackItemViewHolder holder) {
        // Cancel any pending image request and set element non-swiped
        picasso.cancelRequest(holder.artwork);
        holder.root.setSwipeState(SwipeableListItem.STATE_CLOSED, holder.viewScroll, false);
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).getId();
    }
    
    public enum Action {
        PLAY, ADD_TO_QUEUE
    }
    
    public interface OnActionListener {
        void onAction(int position, Action action);
    }
    
}
