package org.singularux.music.feature.library.presentation;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.AsyncDifferConfig;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;

import com.squareup.picasso.Picasso;

import org.singularux.music.feature.library.R;
import org.singularux.music.feature.library.data.TrackItemData;

import java.util.concurrent.ExecutorService;

public class TrackListAdapter extends ListAdapter<TrackItemData, TrackItemViewHolder> {

    private final Picasso picasso;

    public TrackListAdapter(@NonNull DiffUtil.ItemCallback<TrackItemData> diffCallback,
                            ExecutorService computationExecutorService,
                            Picasso picasso) {
        super(new AsyncDifferConfig.Builder<>(diffCallback)
                .setBackgroundThreadExecutor(computationExecutorService)
                .build());
        this.picasso = picasso;
        setHasStableIds(true);
        setStateRestorationPolicy(StateRestorationPolicy.PREVENT_WHEN_EMPTY);
    }

    @Override
    public @NonNull TrackItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                                           int viewType) {
        return TrackItemViewHolder.create(parent);
    }

    @Override
    public void onBindViewHolder(@NonNull TrackItemViewHolder holder, int position) {
        // Retrieve data
        TrackItemData data = getItem(position);
        String artistName = data.getArtistName();
        if (artistName == null) {
            artistName = holder.itemView.getContext().getString(R.string.track_unknown_artist);
        }
        long durationMinutes = data.getDuration().getSeconds() / 60;
        long durationSeconds = data.getDuration().getSeconds() % 60;
        String artistsWithDuration = holder.itemView.getContext().getString(
                R.string.track_duration_artist, durationMinutes, durationSeconds, artistName);
        // Update appearance
        holder.bind();
        holder.title.setText(data.getTitle());
        holder.artistsWithDuration.setText(artistsWithDuration);
        // Load thumbnail or nothing
        if (data.getArtworkUri() != null) {
            picasso.load(data.getArtworkUri())
                    .resizeDimen(R.dimen.item_track_artwork, R.dimen.item_track_artwork)
                    .into(holder.artwork);
        } else {
            picasso.cancelRequest(holder.artwork);
            holder.artwork.setImageDrawable(null);
        }
    }

    @Override
    public void onViewRecycled(@NonNull TrackItemViewHolder holder) {
        // Stop loading thumbnail
        picasso.cancelRequest(holder.artwork);
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).getId();
    }

}
