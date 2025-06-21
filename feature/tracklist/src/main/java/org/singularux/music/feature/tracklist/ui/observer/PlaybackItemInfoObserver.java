package org.singularux.music.feature.tracklist.ui.observer;

import android.content.Context;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.lifecycle.Observer;

import com.squareup.picasso.Picasso;

import org.singularux.music.feature.playback.domain.model.PlaybackItemInfo;
import org.singularux.music.feature.tracklist.R;
import org.singularux.music.feature.tracklist.databinding.RouteTrackListBinding;

import java.util.Optional;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PlaybackItemInfoObserver implements Observer<Optional<PlaybackItemInfo>> {

    private final Context context;
    private final RouteTrackListBinding binding;
    private final Picasso picasso;

    @Override
    public void onChanged(@NonNull Optional<PlaybackItemInfo> maybePlaybackItemInfo) {
        String title;
        String artists;
        Uri artwork;
        // Extract data
        if (maybePlaybackItemInfo.isPresent()) {
            PlaybackItemInfo playbackItemInfo = maybePlaybackItemInfo.get();
            // Title
            title = playbackItemInfo.getTitle();
            // Artist
            if (playbackItemInfo.getArtistName() != null) {
                artists = playbackItemInfo.getArtistName();
            } else {
                artists = context.getString(R.string.playback_bar_unknown_artist);
            }
            // Artwork
            artwork = playbackItemInfo.getArtworkUri();
        } else {
            title = context.getString(R.string.playback_bar_unknown_track);
            artists = context.getString(R.string.playback_bar_unknown_artist);
            artwork = null;
        }
        // Apply UI
        binding.playbackBar.playbackBarTitle.setText(title);
        binding.playbackBar.playbackBarArtist.setText(artists);
        if (artwork != null) {
            picasso.load(artwork)
                    .resizeDimen(R.dimen.playback_bar_artwork_size, R.dimen.playback_bar_artwork_size)
                    .into(binding.playbackBar.playbackBarArtwork);
        } else {
            binding.playbackBar.playbackBarArtwork.setImageDrawable(null);
        }
    }

}
