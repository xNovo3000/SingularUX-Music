package org.singularux.music.feature.library.presentation;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.listitem.ListItemCardView;
import com.google.android.material.listitem.ListItemLayout;
import com.google.android.material.listitem.ListItemRevealLayout;
import com.google.android.material.listitem.ListItemViewHolder;
import com.google.android.material.textview.MaterialTextView;

import org.singularux.music.feature.library.databinding.ItemTrackBinding;

public class TrackItemViewHolder extends ListItemViewHolder {

    public final ListItemLayout root;

    public final ListItemCardView viewContent;
    public final ShapeableImageView artwork;
    public final MaterialTextView title, durationArtist;

    public final ListItemRevealLayout viewScroll;
    public final MaterialButton addToQueue;

    public TrackItemViewHolder(@NonNull ItemTrackBinding binding) {
        super(binding.getRoot());
        this.root = binding.getRoot();
        this.viewContent = binding.viewContent;
        this.artwork = binding.artwork;
        this.title = binding.title;
        this.durationArtist = binding.durationArtist;
        this.viewScroll = binding.viewScroll;
        this.addToQueue = binding.addToQueue;
    }

    public static @NonNull TrackItemViewHolder create(@NonNull ViewGroup parent) {
        ItemTrackBinding binding = ItemTrackBinding.inflate(LayoutInflater
                .from(parent.getContext()), parent, false);
        return new TrackItemViewHolder(binding);
    }

}
