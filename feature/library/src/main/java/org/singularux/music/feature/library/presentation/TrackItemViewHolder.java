package org.singularux.music.feature.library.presentation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textview.MaterialTextView;

import org.singularux.music.feature.library.R;
import org.singularux.music.feature.library.databinding.ItemTrackBinding;

import lombok.Getter;

public class TrackItemViewHolder extends RecyclerView.ViewHolder {

    private final @Getter ShapeableImageView artwork;
    private final @Getter MaterialTextView title, artistsWithDuration;
    private final @Getter MaterialButton addToQueue;

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
