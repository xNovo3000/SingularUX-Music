package org.singularux.music.feature.playback.data;

import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.media3.common.MediaItem;
import androidx.media3.common.MediaMetadata;

import java.time.Duration;
import java.util.function.Function;

import lombok.Value;

@Value
public class QueueItem {

    long id;
    @NonNull String title;
    @Nullable Long artistId;
    @Nullable String artistName;
    @Nullable Long albumId;
    @Nullable String albumTitle;
    @NonNull Duration duration;
    @NonNull Uri uri;
    @Nullable Uri artworkPath;
    @Nullable String playingFrom;
    boolean customQueue;

    public static final class FromMediaItemMapper implements Function<MediaItem, QueueItem> {

        @Override
        public @NonNull QueueItem apply(@NonNull MediaItem mediaItem) {
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
            // URI
            Uri uri;
            if (mediaItem.localConfiguration != null) {
                uri = mediaItem.localConfiguration.uri;
            } else {
                // Fallback, should never happen
                uri = Uri.withAppendedPath(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                        String.valueOf(id));
            }
            // Artwork
            Uri artworkUri = mediaItem.mediaMetadata.artworkUri;
            // Playback token
            String playingFrom = null;
            if (mediaItem.mediaMetadata.extras != null &&
                    mediaItem.mediaMetadata.extras.containsKey("playing_from")) {
                playingFrom = mediaItem.mediaMetadata.extras.getString("playing_from");
            }
            // Custom queue
            boolean customQueue = mediaItem.mediaMetadata.extras != null &&
                    mediaItem.mediaMetadata.extras.containsKey("custom_queue");
            // Create final object
            return new QueueItem(id, title, artistId, artistName, albumId, albumTitle,
                    duration, uri, artworkUri, playingFrom, customQueue);
        }

    }

    public static final class ToMediaItemMapper implements Function<QueueItem, MediaItem> {

        @Override
        public @NonNull MediaItem apply(@NonNull QueueItem queueItem) {
            Bundle extras = new Bundle();
            if (queueItem.getArtistId() != null) {
                extras.putLong("artist_id", queueItem.getArtistId());
            }
            if (queueItem.getAlbumId() != null) {
                extras.putLong("album_id", queueItem.getAlbumId());
            }
            if (queueItem.getPlayingFrom() != null) {
                extras.putString("playing_from", queueItem.getPlayingFrom());
            }
            if (queueItem.isCustomQueue()) {
                extras.putBoolean("custom_queue", true);
            }
            MediaMetadata mediaMetadata = new MediaMetadata.Builder()
                    .setTitle(queueItem.getTitle())
                    .setAlbumArtist(queueItem.getAlbumTitle())
                    .setArtist(queueItem.getArtistName())
                    .setArtworkUri(queueItem.getArtworkPath())
                    .setDurationMs(queueItem.getDuration().toMillis())
                    .setExtras(extras)
                    .build();
            return new MediaItem.Builder()
                    .setMediaId(String.valueOf(queueItem.getId()))
                    .setUri(queueItem.getUri())
                    .setMediaMetadata(mediaMetadata)
                    .build();
        }

    }

}
