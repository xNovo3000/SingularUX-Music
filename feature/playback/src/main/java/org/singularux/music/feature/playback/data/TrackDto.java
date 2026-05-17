package org.singularux.music.feature.playback.data;

import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.media3.common.MediaItem;
import androidx.media3.common.MediaMetadata;

import java.time.Duration;
import java.util.function.Function;

import lombok.RequiredArgsConstructor;
import lombok.Value;

@Value
public class TrackDto {
    
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

    @RequiredArgsConstructor
    public static final class ToMediaItemMapper implements Function<TrackDto, MediaItem> {

        private final boolean customQueue;

        @Override
        public @NonNull MediaItem apply(@NonNull TrackDto trackDto) {
            Bundle extras = new Bundle();
            if (trackDto.getArtistId() != null) {
                extras.putLong("artist_id", trackDto.getArtistId());
            }
            if (trackDto.getAlbumId() != null) {
                extras.putLong("album_id", trackDto.getAlbumId());
            }
            if (trackDto.getPlayingFrom() != null) {
                extras.putString("playing_from", trackDto.getPlayingFrom());
            }
            if (customQueue) {
                extras.putBoolean("custom_queue", true);
            }
            MediaMetadata mediaMetadata = new MediaMetadata.Builder()
                    .setTitle(trackDto.getTitle())
                    .setAlbumArtist(trackDto.getAlbumTitle())
                    .setArtist(trackDto.getArtistName())
                    .setArtworkUri(trackDto.getArtworkPath())
                    .setDurationMs(trackDto.getDuration().toMillis())
                    .setExtras(extras)
                    .build();
            return new MediaItem.Builder()
                    .setMediaId(String.valueOf(trackDto.getId()))
                    .setUri(trackDto.getUri())
                    .setMediaMetadata(mediaMetadata)
                    .build();
        }

    }
    
}
