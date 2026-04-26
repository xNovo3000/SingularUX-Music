package org.singularux.music.data.library.repository;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;

import androidx.annotation.NonNull;

import org.singularux.music.core.permission.MusicPermission;
import org.singularux.music.core.permission.MusicPermissionManager;
import org.singularux.music.data.library.entity.AlbumEntity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AlbumRepositoryAndroid implements AlbumRepository {

    private static final String TAG = "AlbumRepositoryAndroid";

    private static final Uri ARTWORK_BASE_PATH =
            Uri.parse("content://media/external/audio/albumart/");

    private final Context context;
    private final MusicPermissionManager musicPermissionManager;

    private static final Uri GET_ALL_URI = MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI;
    private static final String[] GET_ALL_PROJECTION = {
            MediaStore.Audio.Albums._ID, MediaStore.Audio.Albums.ALBUM,
            MediaStore.Audio.Albums.ARTIST_ID, MediaStore.Audio.Albums.ARTIST,
            MediaStore.Audio.Albums.NUMBER_OF_SONGS
    };
    private static final String GET_ALL_SORT_ORDER = MediaStore.Audio.Albums.DEFAULT_SORT_ORDER;

    @Override
    public @NonNull List<AlbumEntity> getAll() {
        // Check for permissions
        if (!musicPermissionManager.hasPermission(MusicPermission.READ_MEDIA_AUDIO)) {
            Log.i(TAG, "Missing READ_MEDIA_AUDIO permission");
            return Collections.emptyList();
        }
        // Create query
        Bundle queryArgs = new Bundle();
        queryArgs.putString(ContentResolver.QUERY_ARG_SQL_SORT_ORDER, GET_ALL_SORT_ORDER);
        // Execute query and return data
        try (Cursor cursor = context.getContentResolver()
                .query(GET_ALL_URI, GET_ALL_PROJECTION, queryArgs, null)) {
            if (cursor == null) {
                Log.i(TAG, "Cursor is null");
                return Collections.emptyList();
            }
            // Extract data
            AlbumEntityExtractor extractor = new AlbumEntityExtractor();
            List<AlbumEntity> result = new ArrayList<>();
            while (cursor.moveToNext()) {
                result.add(extractor.apply(cursor));
            }
            return result;
        } catch (Exception e) {
            Log.e(TAG, "Cannot execute statement", e);
            return Collections.emptyList();
        }
    }

    @Override
    public @NonNull Optional<AlbumEntity> getById(long id) {
        // Check for permissions
        if (!musicPermissionManager.hasPermission(MusicPermission.READ_MEDIA_AUDIO)) {
            Log.i(TAG, "Missing READ_MEDIA_AUDIO permission");
            return Optional.empty();
        }
        return Optional.empty();
    }

    @Override
    public @NonNull List<AlbumEntity> getAllByTitleLike(@NonNull String query) {
        // Check for permissions
        if (!musicPermissionManager.hasPermission(MusicPermission.READ_MEDIA_AUDIO)) {
            Log.i(TAG, "Missing READ_MEDIA_AUDIO permission");
            return Collections.emptyList();
        }
        return Collections.emptyList();
    }

    @Override
    public @NonNull List<AlbumEntity> getAllByArtistId(long artistId) {
        // Check for permissions
        if (!musicPermissionManager.hasPermission(MusicPermission.READ_MEDIA_AUDIO)) {
            Log.i(TAG, "Missing READ_MEDIA_AUDIO permission");
            return Collections.emptyList();
        }
        return Collections.emptyList();
    }

    private static final class AlbumEntityExtractor implements Function<Cursor, AlbumEntity> {

        @Override
        public @NonNull AlbumEntity apply(@NonNull Cursor cursor) {
            long id = cursor.getLong(0);
            String title = null, artistName = null;
            Long artistId = null;
            int numberOfTracks = cursor.getInt(4);
            Uri artworkPath = null;
            // Album
            if (!cursor.isNull(1)) {
                title = cursor.getString(1);
            }
            if (title == null || title.equals("<unknown>")) {
                title = null;
            }
            // Artist
            if (!cursor.isNull(2)) {
                artistId = cursor.getLong(2);
            }
            if (!cursor.isNull(3)) {
                artistName = cursor.getString(3);
            }
            if (artistName == null || artistName.equals("<unknown>")) {
                artistId = null;
                artistName = null;
            }
            // Artwork path
            if (title != null) {
                artworkPath = Uri.withAppendedPath(ARTWORK_BASE_PATH, String.valueOf(id));
            }
            // Result
            return new AlbumEntity(id, title, artistId, artistName, artworkPath, numberOfTracks);
        }

    }

}
