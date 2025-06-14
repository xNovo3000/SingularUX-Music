package org.singularux.music.feature.tracklist.model;

import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;

import androidx.annotation.NonNull;
import androidx.media3.common.MediaItem;
import androidx.media3.common.MediaMetadata;

import java.util.function.Function;

import javax.inject.Inject;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(onConstructor_ = {@Inject})
public class TrackItemToMediaItemMapper implements Function<TrackItem, MediaItem> {

    @Override
    public MediaItem apply(@NonNull TrackItem trackItem) {
        Uri uri = Uri.withAppendedPath(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                String.valueOf(trackItem.getId()));
        // Extras that does not fit the mediaMetadata
        Bundle extras = new Bundle();
        extras.putLong("playback_id", trackItem.getId());
        if (trackItem.getArtistId() != null) {
            extras.putLong("artist_id", trackItem.getArtistId());
        }
        if (trackItem.getAlbumId() != null) {
            extras.putLong("album_id", trackItem.getAlbumId());
        }
        // Basic mediaMetadata
        MediaMetadata mediaMetadata = new MediaMetadata.Builder()
                .setTitle(trackItem.getTitle())
                .setArtist(trackItem.getArtistName())
                .setAlbumTitle(trackItem.getAlbumName())
                .setArtworkUri(trackItem.getArtworkUri())
                .setExtras(extras)
                .build();
        // Full MediaItem
        return new MediaItem.Builder()
                .setMediaId(String.valueOf(trackItem.getId()))
                .setUri(uri)
                .setMediaMetadata(mediaMetadata)
                .build();
    }

}
