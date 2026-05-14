package org.singularux.music.feature.library.presentation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.listitem.ListItemCardView;
import com.google.android.material.listitem.ListItemLayout;
import com.google.android.material.listitem.ListItemRevealLayout;
import com.google.android.material.listitem.ListItemViewHolder;
import com.google.android.material.textview.MaterialTextView;

import org.singularux.music.feature.library.databinding.ItemSearchTrackBinding;

public abstract class SearchItemViewHolder extends ListItemViewHolder {

    public SearchItemViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    public static final class Track extends SearchItemViewHolder {

        public final ListItemLayout root;

        public final ListItemCardView viewContent;
        public final ShapeableImageView artwork;
        public final MaterialTextView title, durationArtist;

        public final ListItemRevealLayout viewScroll;
        public final MaterialButton addToQueue;

        public Track(@NonNull ItemSearchTrackBinding binding) {
            super(binding.getRoot());
            this.root = binding.getRoot();
            this.viewContent = binding.viewContent;
            this.artwork = binding.artwork;
            this.title = binding.title;
            this.durationArtist = binding.durationArtist;
            this.viewScroll = binding.viewScroll;
            this.addToQueue = binding.addToQueue;
        }

        public static @NonNull Track create(@NonNull ViewGroup parent) {
            ItemSearchTrackBinding binding = ItemSearchTrackBinding.inflate(LayoutInflater
                    .from(parent.getContext()), parent, false);
            return new Track(binding);
        }

    }

}
