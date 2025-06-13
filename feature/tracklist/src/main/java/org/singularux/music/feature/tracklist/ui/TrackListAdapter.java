package org.singularux.music.feature.tracklist.ui;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ListAdapter;

import com.squareup.picasso.Picasso;

import org.singularux.music.feature.tracklist.R;
import org.singularux.music.feature.tracklist.model.TrackItem;
import org.singularux.music.feature.tracklist.model.TrackItemDiffCallback;
import org.singularux.music.feature.tracklist.ui.component.TrackItemViewHolder;

import javax.inject.Inject;

public class TrackListAdapter extends ListAdapter<TrackItem, TrackItemViewHolder> {

    private final Picasso picasso;

    @Inject
    public TrackListAdapter(TrackItemDiffCallback diffCallback, Picasso picasso) {
        super(diffCallback);
        setHasStableIds(true);
        this.picasso = picasso;
    }

    @NonNull
    @Override
    public TrackItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.component_track_item, parent, false);
        return new TrackItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TrackItemViewHolder holder, int position) {
        Context context = ContextCompat.getContextForLanguage(holder.itemView.getContext());
        TrackItem trackItem = getItem(position);
        // Extract data
        String title = trackItem.getTitle();
        String artistsName = trackItem.getArtistsName();
        if (artistsName == null) {
            artistsName = context.getString(R.string.track_item_unknown_artist);
        }
        long durationMinutes = trackItem.getDuration().getSeconds() / 60;
        long durationSeconds = trackItem.getDuration().getSeconds() % 60;
        boolean isCurrentlyPlaying = trackItem.isCurrentlyPlaying();
        Uri artworkUri = trackItem.getArtworkUri();
        // Apply
        holder.itemView.setSelected(isCurrentlyPlaying);
        holder.getTitle().setText(title);
        String durationArtistsName = context.getString(R.string.track_item_duration_artists,
                durationMinutes, durationSeconds, artistsName);
        holder.getDurationArtists().setText(durationArtistsName);
        picasso.load(artworkUri)
                .into(holder.getArtwork());
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).getId();
    }

}
