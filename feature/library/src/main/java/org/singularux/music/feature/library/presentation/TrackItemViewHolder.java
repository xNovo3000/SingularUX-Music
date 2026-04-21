package org.singularux.music.feature.library.presentation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.listitem.ListItemViewHolder;
import com.google.android.material.textview.MaterialTextView;

import org.singularux.music.feature.library.R;
import org.singularux.music.feature.library.databinding.ItemTrackBinding;

public class TrackItemViewHolder extends ListItemViewHolder {

    public final ShapeableImageView artwork;
    public final MaterialTextView title, artistsWithDuration;
    public final MaterialButton addToQueue;

    public TrackItemViewHolder(@NonNull View itemView) {
        super(itemView);
        ItemTrackBinding binding = ItemTrackBinding.bind(itemView);
        this.artwork = binding.artwork;
        this.title = binding.title;
        this.artistsWithDuration = binding.artistWithDuration;
        this.addToQueue = binding.addToQueue;
    }

    public static @NonNull TrackItemViewHolder create(@NonNull ViewGroup parent) {
        return new TrackItemViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_track, parent, false));
    }

}
