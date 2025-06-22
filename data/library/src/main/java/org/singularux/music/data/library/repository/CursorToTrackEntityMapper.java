package org.singularux.music.data.library.repository;

import android.database.Cursor;
import android.net.Uri;

import androidx.annotation.NonNull;

import org.singularux.music.data.library.entity.TrackEntity;

import java.time.Duration;
import java.util.Objects;
import java.util.function.Function;

final class CursorToTrackEntityMapper implements Function<Cursor, TrackEntity> {

    private static final Uri ARTWORK_URI = Uri.parse("content://media/external/audio/albumart");

    @Override
    public @NonNull TrackEntity apply(@NonNull Cursor cursor) {
        // ID
        long id = cursor.getInt(0);
        // Title (or display name if not present)
        String title;
        if (!cursor.isNull(1)) {
            title = cursor.getString(1);
        } else {
            title = cursor.getString(2);
        }
        // Artist ID and name
        Long artistId = null;
        if (!cursor.isNull(3)) {
            artistId = cursor.getLong(3);
        }
        String artistName = null;
        if (!cursor.isNull(4)) {
            artistName = cursor.getString(4);
            if (Objects.equals(artistName, "<unknown>")) {
                artistName = null;
            }
        }
        // Album ID and name
        Long albumId = null;
        if (!cursor.isNull(5)) {
            albumId = cursor.getLong(5);
        }
        String albumTitle = null;
        if (!cursor.isNull(6)) {
            albumTitle = cursor.getString(6);
            if (Objects.equals(albumTitle, "<unknown>")) {
                albumTitle = null;
            }
        }
        // Artwork
        Uri artworkUri = null;
        if (albumId != null) {
            artworkUri = Uri.withAppendedPath(ARTWORK_URI, String.valueOf(albumId));
        }
        // Duration
        Duration duration = Duration.ofMillis(cursor.getLong(7));
        // Create TrackEntity
        return new TrackEntity(id, title, artistId, artistName,
                albumId, albumTitle, artworkUri, duration);
    }

}
