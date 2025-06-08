package org.singularux.music.feature.tracklist.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;

import org.singularux.music.feature.tracklist.R;
import org.singularux.music.feature.tracklist.model.TrackItem;
import org.singularux.music.feature.tracklist.model.TrackItemDiffCallback;
import org.singularux.music.feature.tracklist.ui.component.TrackItemViewHolder;

import javax.inject.Inject;

public class TrackListAdapter extends ListAdapter<TrackItem, TrackItemViewHolder> {

    @Inject
    public TrackListAdapter(TrackItemDiffCallback diffCallback) {
        super(diffCallback);
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
        TrackItem trackItem = getCurrentList().get(position);
        // Title
        holder.getTitle().setText(trackItem.getTitle());
        // Duration and artists
        Context context = ContextCompat.getContextForLanguage(holder.itemView.getContext());
        String durationArtists = context.getString(R.string.track_item_duration_artists,
                trackItem.getDuration().getSeconds() / 60,
                trackItem.getDuration().getSeconds() % 60,
                trackItem.getArtistsName());
        holder.getDurationArtists().setText(durationArtists);
        // TODO: Artwork
    }

}
