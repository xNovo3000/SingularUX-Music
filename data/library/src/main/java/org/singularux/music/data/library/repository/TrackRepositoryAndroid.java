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
import org.singularux.music.data.library.entity.TrackEntity;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class TrackRepositoryAndroid implements TrackRepository {

    private static final String TAG = "TrackRepositoryAndroid";

    private final Context context;
    private final MusicPermissionManager musicPermissionManager;

    private static final Uri GET_ALL_URI = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
    private static final String[] GET_ALL_PROJECTION = {
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE, MediaStore.Audio.Media.DISPLAY_NAME,
            MediaStore.Audio.Media.ARTIST_ID, MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ALBUM_ID, MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.DURATION
    };
    private static final String GET_ALL_SELECTION = MediaStore.Audio.Media.IS_MUSIC +
            " = ? AND " + MediaStore.Audio.Media.IS_TRASHED + " = ?";
    private static final String[] GET_ALL_SELECTION_ARGS = {"1", "0"};
    private static final String GET_ALL_SORT_ORDER = MediaStore.Audio.Media.DEFAULT_SORT_ORDER;

    private static final Uri ARTWORK_BASE_PATH =
            Uri.parse("content://media/external/audio/albumart/");

    @Override
    public @NonNull List<TrackEntity> getAll() {
        // Check for permissions
        if (!musicPermissionManager.hasPermission(MusicPermission.READ_MEDIA_AUDIO)) {
            Log.i(TAG, "Missing READ_MEDIA_AUDIO permission");
            return Collections.emptyList();
        }
        // Create query
        Uri uri = GET_ALL_URI;
        String[] projection = GET_ALL_PROJECTION;
        Bundle queryArgs = new Bundle();
        queryArgs.putString(ContentResolver.QUERY_ARG_SQL_SELECTION, GET_ALL_SELECTION);
        queryArgs.putStringArray(ContentResolver.QUERY_ARG_SQL_SELECTION_ARGS, GET_ALL_SELECTION_ARGS);
        queryArgs.putString(ContentResolver.QUERY_ARG_SQL_SORT_ORDER, GET_ALL_SORT_ORDER);
        // Execute query and return data
        try (Cursor cursor = context.getContentResolver()
                .query(GET_ALL_URI, GET_ALL_PROJECTION, queryArgs, null)) {
            if (cursor == null) {
                Log.i(TAG, "Cursor is null");
                return Collections.emptyList();
            }
            // Extract data
            TrackEntityExtractor extractor = new TrackEntityExtractor();
            List<TrackEntity> result = new ArrayList<>();
            while (cursor.moveToNext()) {
                result.add(extractor.apply(cursor));
            }
            return result;
        } catch (Exception e) {
            Log.e(TAG, "Cannot execute statement", e);
            return Collections.emptyList();
        }
    }

    private static final class TrackEntityExtractor implements Function<Cursor, TrackEntity> {

        @Override
        public @NonNull TrackEntity apply(@NonNull Cursor cursor) {
            long id = cursor.getLong(0);
            Long artistId = null, albumId = null;
            String title, artistName = null, albumName = null;
            Duration duration = Duration.ofMillis(cursor.getLong(7));
            Uri artworkPath = null;
            // Title
            if (!cursor.isNull(1)) {
                title = cursor.getString(1);
            } else {
                title = cursor.getString(2);
            }
            // Artist
            if (!cursor.isNull(3)) {
                artistId = cursor.getLong(3);
            }
            if (!cursor.isNull(4)) {
                artistName = cursor.getString(4);
            }
            // Album
            if (!cursor.isNull(5)) {
                albumId = cursor.getLong(5);
            }
            if (!cursor.isNull(6)) {
                albumName = cursor.getString(6);
            }
            // Artwork path
            if (albumName != null && albumId != null && !albumName.equals("<unknown>")) {
                artworkPath = Uri.withAppendedPath(ARTWORK_BASE_PATH, albumId.toString());
            }
            // Result
            return new TrackEntity(id, title, artistId, artistName, albumId, albumName,
                    duration, artworkPath);
        }

    }

}
