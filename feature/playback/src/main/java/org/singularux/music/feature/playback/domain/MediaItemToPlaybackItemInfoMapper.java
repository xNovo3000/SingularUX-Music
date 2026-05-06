package org.singularux.music.feature.playback.domain;

import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.media3.common.MediaItem;

import org.singularux.music.feature.playback.data.PlaybackItemInfo;

import java.time.Duration;
import java.util.function.Function;

final class MediaItemToPlaybackItemInfoMapper
        implements Function<MediaItem, PlaybackItemInfo> {

    @Override
    public @NonNull PlaybackItemInfo apply(@NonNull MediaItem mediaItem) {
        // ID
        long id;
        try {
            id = Long.parseLong(mediaItem.mediaId);
        } catch (NumberFormatException e) {
            id = -1;
        }
        // Title
        String title = "";
        if (mediaItem.mediaMetadata.title != null) {
            title = mediaItem.mediaMetadata.title.toString();
        }
        // Artist ID
        Long artistId = null;
        if (mediaItem.mediaMetadata.extras != null &&
                mediaItem.mediaMetadata.extras.containsKey("artist_id")) {
            artistId = mediaItem.mediaMetadata.extras.getLong("artist_id");
        }
        // Artist name
        String artistName = null;
        if (mediaItem.mediaMetadata.artist != null) {
            artistName = mediaItem.mediaMetadata.artist.toString();
        }
        // Album ID
        Long albumId = null;
        if (mediaItem.mediaMetadata.extras != null &&
                mediaItem.mediaMetadata.extras.containsKey("album_id")) {
            albumId = mediaItem.mediaMetadata.extras.getLong("album_id");
        }
        // Album title
        String albumTitle = null;
        if (mediaItem.mediaMetadata.albumTitle != null) {
            albumTitle = mediaItem.mediaMetadata.albumTitle.toString();
        }
        // Duration
        Duration duration = Duration.ZERO;
        if (mediaItem.mediaMetadata.durationMs != null) {
            duration = Duration.ofMillis(mediaItem.mediaMetadata.durationMs);
        }
        // Playback token
        String playingFrom = null;
        if (mediaItem.mediaMetadata.extras != null &&
                mediaItem.mediaMetadata.extras.containsKey("playing_from")) {
            playingFrom = mediaItem.mediaMetadata.extras.getString("playing_from");
        }
        // Artwork
        Uri artworkUri = mediaItem.mediaMetadata.artworkUri;
        // Create PlaybackItemInfo
        return new PlaybackItemInfo(id, title, artistId, artistName,
                albumId, albumTitle, artworkUri, duration, playingFrom);
    }

}
