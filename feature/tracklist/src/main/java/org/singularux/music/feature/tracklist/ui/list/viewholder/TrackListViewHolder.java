package org.singularux.music.feature.tracklist.ui.list.viewholder;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textview.MaterialTextView;

import org.singularux.music.feature.tracklist.databinding.ComponentTrackListItemBinding;

import lombok.Getter;

@Getter
public class TrackListViewHolder extends RecyclerView.ViewHolder {

    private final ShapeableImageView artwork;
    private final MaterialTextView title;
    private final MaterialTextView durationArtists;

    public TrackListViewHolder(@NonNull View itemView) {
        super(itemView);
        ComponentTrackListItemBinding binding = ComponentTrackListItemBinding.bind(itemView);
        this.artwork = binding.trackItemArtwork;
        this.title = binding.trackItemTitle;
        this.durationArtists = binding.trackItemDurationArtists;
    }

}
