package org.singularux.music.feature.tracklist.ui.search.viewholder;

import android.content.Context;
import android.net.Uri;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textview.MaterialTextView;
import com.squareup.picasso.Picasso;

import org.singularux.music.feature.tracklist.R;
import org.singularux.music.feature.tracklist.databinding.ComponentSearchItemTrackBinding;
import org.singularux.music.feature.tracklist.ui.search.SearchListOnClickListener;
import org.singularux.music.feature.tracklist.ui.search.item.SearchListItem;
import org.singularux.music.feature.tracklist.ui.search.item.SearchListItemTrack;

import lombok.Getter;

@Getter
public class SearchListViewHolderTrack extends SearchListViewHolder {

    public static final int VIEW_TYPE = 887;

    private final ShapeableImageView artwork;
    private final MaterialTextView title;
    private final MaterialTextView durationArtists;

    public SearchListViewHolderTrack(@NonNull View itemView) {
        super(itemView);
        ComponentSearchItemTrackBinding binding = ComponentSearchItemTrackBinding.bind(itemView);
        this.artwork = binding.searchItemTrackArtwork;
        this.title = binding.searchItemTrackTitle;
        this.durationArtists = binding.searchItemTrackDurationArtists;
    }

    @Override
    public void onBind(int position,
                       @NonNull SearchListItem item,
                       @NonNull Picasso picasso,
                       @Nullable SearchListOnClickListener onClickListener)
    {
        String title;
        String artists;
        long durationMinutesMs;
        long durationSecondsMs;
        String durationArtists;
        Uri artwork;
        // Extract data
        Context context = ContextCompat.getContextForLanguage(itemView.getContext());
        SearchListItemTrack trackItem = (SearchListItemTrack) item;
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
        this.title.setText(title);
        this.durationArtists.setText(durationArtists);
        if (artwork != null) {
            picasso.load(artwork)
                    .resizeDimen(R.dimen.track_item_artwork_size, R.dimen.track_item_artwork_size)
                    .into(this.artwork);
        } else {
            this.artwork.setImageDrawable(null);
        }
        if (onClickListener != null) {
            itemView.setOnClickListener(v -> onClickListener.onClick(position));
        } else {
            itemView.setOnClickListener(null);
        }

    }

}
