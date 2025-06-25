package org.singularux.music.feature.tracklist.util;

import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;

import androidx.annotation.NonNull;
import androidx.media3.common.MediaItem;
import androidx.media3.common.MediaMetadata;

import org.singularux.music.feature.tracklist.ui.search.item.SearchListItemTrack;

import java.util.function.Function;

public class SearchListItemTrackToMediaItemMapper
        implements Function<SearchListItemTrack, MediaItem> {

    @Override
    public @NonNull MediaItem apply(@NonNull SearchListItemTrack searchListItemTrack) {
        Uri uri = Uri.withAppendedPath(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                String.valueOf(searchListItemTrack.getId()));
        // Extras that does not fit the mediaMetadata
        Bundle extras = new Bundle();
        if (searchListItemTrack.getArtistId() != null) {
            extras.putLong("artist_id", searchListItemTrack.getArtistId());
        }
        if (searchListItemTrack.getAlbumId() != null) {
            extras.putLong("album_id", searchListItemTrack.getAlbumId());
        }
        // Basic mediaMetadata
        MediaMetadata mediaMetadata = new MediaMetadata.Builder()
                .setTitle(searchListItemTrack.getTitle())
                .setArtist(searchListItemTrack.getArtistName())
                .setDurationMs(searchListItemTrack.getDuration().toMillis())
                .setAlbumTitle(searchListItemTrack.getAlbumTitle())
                .setArtworkUri(searchListItemTrack.getArtworkUri())
                .setExtras(extras)
                .build();
        // Full MediaItem
        return new MediaItem.Builder()
                .setMediaId(String.valueOf(searchListItemTrack.getId()))
                .setUri(uri)
                .setMediaMetadata(mediaMetadata)
                .build();
    }

}
