package org.singularux.music.feature.tracklist.util;

import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;

import androidx.annotation.NonNull;
import androidx.media3.common.MediaItem;
import androidx.media3.common.MediaMetadata;

import org.singularux.music.feature.tracklist.ui.list.item.TrackListItem;

import java.util.function.Function;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class TrackListItemToMediaItemMapper implements Function<TrackListItem, MediaItem> {

    private final String playbackToken;

    @Override
    public @NonNull MediaItem apply(@NonNull TrackListItem trackListItem) {
        Uri uri = Uri.withAppendedPath(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                String.valueOf(trackListItem.getId()));
        // Extras that does not fit the mediaMetadata
        Bundle extras = new Bundle();
        extras.putString("playing_from", playbackToken);
        if (trackListItem.getArtistId() != null) {
            extras.putLong("artist_id", trackListItem.getArtistId());
        }
        if (trackListItem.getAlbumId() != null) {
            extras.putLong("album_id", trackListItem.getAlbumId());
        }
        // Basic mediaMetadata
        MediaMetadata mediaMetadata = new MediaMetadata.Builder()
                .setTitle(trackListItem.getTitle())
                .setArtist(trackListItem.getArtistName())
                .setDurationMs(trackListItem.getDuration().toMillis())
                .setAlbumTitle(trackListItem.getAlbumTitle())
                .setArtworkUri(trackListItem.getArtworkUri())
                .setExtras(extras)
                .build();
        // Full MediaItem
        return new MediaItem.Builder()
                .setMediaId(String.valueOf(trackListItem.getId()))
                .setUri(uri)
                .setMediaMetadata(mediaMetadata)
                .build();
    }

}
