package org.singularux.music.feature.tracklist.ui.observer;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;

import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;

import com.squareup.picasso.Picasso;

import org.singularux.music.feature.playback.domain.model.PlaybackItemInfo;
import org.singularux.music.feature.tracklist.R;
import org.singularux.music.feature.tracklist.databinding.RouteTrackListBinding;
import org.singularux.music.feature.tracklist.ui.TrackListViewModel;

import java.util.Optional;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PlaybackInfoObserver implements Observer<Optional<PlaybackItemInfo>> {

    private final Context context;
    private final RouteTrackListBinding binding;
    private final Picasso picasso;
    private final TrackListViewModel viewModel;

    @Override
    public void onChanged(Optional<PlaybackItemInfo> maybePlaybackInfo) {
        // Extract data
        String title;
        String artist;
        boolean isPlaying = false;
        Drawable icon;
        boolean enabled;
        Uri artworkUri = null;
        // Apply data
        if (maybePlaybackInfo.isPresent()) {
            PlaybackItemInfo playbackItemInfo = maybePlaybackInfo.get();
            title = playbackItemInfo.getTitle();
            if (playbackItemInfo.getArtistName() != null) {
                artist = playbackItemInfo.getArtistName();
            } else {
                artist = context.getString(R.string.track_item_unknown_artist);
            }
            isPlaying = playbackItemInfo.isPlaying();
            if (playbackItemInfo.isPlaying()) {
                icon = ContextCompat.getDrawable(context, R.drawable.round_pause_24);
            } else {
                icon = ContextCompat.getDrawable(context, R.drawable.round_play_arrow_24);
            }
            enabled = true;
            artworkUri = playbackItemInfo.getArtworkUri();
        } else {
            title = context.getString(R.string.track_item_unknown_track);
            artist = context.getString(R.string.track_item_unknown_artist);
            icon = ContextCompat.getDrawable(context, R.drawable.round_play_arrow_24);
            enabled = false;
        }
        // Apply UI
        binding.playbackBar.playbackBarTitle.setText(title);
        binding.playbackBar.playbackBarArtist.setText(artist);
        binding.playbackBar.playbackBarPlayPause.setIcon(icon);
        binding.playbackBar.playbackBarPlayPause.setEnabled(enabled);
        if (artworkUri != null) {
            picasso.load(artworkUri)
                    .resizeDimen(R.dimen.playback_bar_artwork_size, R.dimen.playback_bar_artwork_size)
                    .into(binding.playbackBar.playbackBarArtwork);
        } else {
            binding.playbackBar.playbackBarArtwork.setImageDrawable(null);
        }
        if (isPlaying) {
            binding.playbackBar.playbackBarPlayPause.setOnClickListener(
                    v -> viewModel.pause());
        } else {
            binding.playbackBar.playbackBarPlayPause.setOnClickListener(
                    v -> viewModel.play());
        }
    }

}
