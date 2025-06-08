package org.singularux.music.feature.tracklist.ui.component;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textview.MaterialTextView;

import org.singularux.music.feature.tracklist.databinding.ComponentTrackItemBinding;

import lombok.Getter;

@Getter
public class TrackItemViewHolder extends RecyclerView.ViewHolder {

    private final ShapeableImageView artwork;
    private final MaterialTextView title;
    private final MaterialTextView durationArtists;

    public TrackItemViewHolder(@NonNull View itemView) {
        super(itemView);
        ComponentTrackItemBinding binding = ComponentTrackItemBinding.bind(itemView);
        this.artwork = binding.trackItemArtwork;
        this.title = binding.trackItemTitle;
        this.durationArtists = binding.trackItemDurationArtists;
    }

}
