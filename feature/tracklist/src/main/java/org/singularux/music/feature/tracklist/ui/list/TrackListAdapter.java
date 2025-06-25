package org.singularux.music.feature.tracklist.ui.list;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ListAdapter;

import com.squareup.picasso.Picasso;

import org.singularux.music.feature.tracklist.R;
import org.singularux.music.feature.tracklist.ui.list.item.TrackListItem;
import org.singularux.music.feature.tracklist.ui.list.viewholder.TrackListViewHolder;

import javax.inject.Inject;

import lombok.Setter;

public class TrackListAdapter extends ListAdapter<TrackListItem, TrackListViewHolder> {

    private final Picasso picasso;
    private @Setter @Nullable TrackListOnClickListener onItemClickListener = null;

    @Inject
    public TrackListAdapter(Picasso picasso) {
        super(new TrackListItemDiffCallback());
        setHasStableIds(true);
        this.picasso = picasso;
    }

    @NonNull
    @Override
    public TrackListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.component_track_list_item, parent, false);
        return new TrackListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TrackListViewHolder holder, int position) {
        String title;
        String artists;
        long durationMinutesMs;
        long durationSecondsMs;
        String durationArtists;
        Uri artwork;
        // Extract data
        Context context = ContextCompat.getContextForLanguage(holder.itemView.getContext());
        TrackListItem trackItem = getItem(position);
        title = trackItem.getTitle();
        if (trackItem.getArtistName() != null) {
            artists = trackItem.getArtistName();
        } else {
            artists = context.getString(R.string.track_item_unknown_artist);
        }
        durationMinutesMs = trackItem.getDuration().getSeconds() / 60;
        durationSecondsMs = trackItem.getDuration().getSeconds() % 60;
        durationArtists = context.getString(R.string.track_item_duration_artists,
                durationMinutesMs, durationSecondsMs, artists);
        artwork = trackItem.getArtworkUri();
        // Apply
        holder.itemView.setSelected(trackItem.isCurrentlyPlaying());
        holder.getTitle().setText(title);
        holder.getDurationArtists().setText(durationArtists);
        if (artwork != null) {
            picasso.load(artwork)
                    .resizeDimen(R.dimen.track_item_artwork_size, R.dimen.track_item_artwork_size)
                    .into(holder.getArtwork());
        } else {
            holder.getArtwork().setImageDrawable(null);
        }
        if (onItemClickListener != null) {
            holder.itemView.setOnClickListener(v -> onItemClickListener.onClick(position));
        } else {
            holder.itemView.setOnClickListener(null);
        }
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).getId();
    }

}
