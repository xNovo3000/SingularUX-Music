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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class TrackRepositoryAndroid implements TrackRepository {

    private static final String TAG = "TrackRepositoryAndroid";

    public static final Uri URI = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
    private static final String[] PROJECTION = {
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE, MediaStore.Audio.Media.DISPLAY_NAME,
            MediaStore.Audio.Media.ARTIST_ID, MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ALBUM_ID, MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.DURATION
    };
    private static final String SORT_ORDER = MediaStore.Audio.Media.DEFAULT_SORT_ORDER;

    private final Context context;
    private final MusicPermissionManager musicPermissionManager;

    private static final String GET_ALL_SELECTION = MediaStore.Audio.Media.IS_MUSIC + " = ? AND " +
            MediaStore.Audio.Media.IS_TRASHED + " = ? AND " +
            MediaStore.Audio.Media.DURATION + " > ?";
    private static final String[] GET_ALL_SELECTION_ARGS = {"1", "0", "60000"};

    @Override
    public @NonNull List<TrackEntity> getAll() {
        // Check for permission
        if (!musicPermissionManager.hasPermission(MusicPermission.READ_MUSIC)) {
            Log.d(TAG, "Cannot load tracks, missing READ_MUSIC permission");
            return Collections.emptyList();
        }
        // Create query args
        Bundle queryArgs = new Bundle();
        queryArgs.putString(ContentResolver.QUERY_ARG_SQL_SELECTION, GET_ALL_SELECTION);
        queryArgs.putStringArray(ContentResolver.QUERY_ARG_SQL_SELECTION_ARGS, GET_ALL_SELECTION_ARGS);
        queryArgs.putString(ContentResolver.QUERY_ARG_SQL_SORT_ORDER, SORT_ORDER);
        // Execute query and return data
        try (Cursor cursor = context.getContentResolver()
                .query(URI, PROJECTION, queryArgs, null)) {
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
            Log.e(TAG, "Cannot execute getAll statement", e);
            return Collections.emptyList();
        }
    }

    private static final String GET_ALL_BY_TITLE_LIKE_SELECTION = MediaStore.Audio.Media.IS_MUSIC + " = ? AND " +
            MediaStore.Audio.Media.IS_TRASHED + " = ? AND (" +
            MediaStore.Audio.Media.TITLE + " LIKE ? OR " +
            MediaStore.Audio.Media.DISPLAY_NAME + " LIKE ?) AND " +
            MediaStore.Audio.Media.DURATION + " > ?";
    private static final String[] GET_ALL_BY_TITLE_SELECTION_ARGS = {"1", "0", "", "", "60000"};

    @Override
    public @NonNull List<TrackEntity> getAllByTitleLike(@NonNull String query) {
        // Title must contain at least one character
        if (query.isEmpty()) {
            Log.d(TAG, "Query is empty");
            return Collections.emptyList();
        }
        // Check for permission
        if (!musicPermissionManager.hasPermission(MusicPermission.READ_MUSIC)) {
            Log.d(TAG, "Cannot load tracks, missing READ_MUSIC permission");
            return Collections.emptyList();
        }
        // Create selection args
        String[] selectionArgs = Arrays.copyOf(GET_ALL_BY_TITLE_SELECTION_ARGS,
                GET_ALL_BY_TITLE_SELECTION_ARGS.length);
        selectionArgs[2] = "%" + query + "%";
        selectionArgs[3] = "%" + query + "%";
        // Create query args
        Bundle queryArgs = new Bundle();
        queryArgs.putString(ContentResolver.QUERY_ARG_SQL_SELECTION, GET_ALL_BY_TITLE_LIKE_SELECTION);
        queryArgs.putStringArray(ContentResolver.QUERY_ARG_SQL_SELECTION_ARGS, selectionArgs);
        queryArgs.putString(ContentResolver.QUERY_ARG_SQL_SORT_ORDER, SORT_ORDER);
        // Execute query and return data
        try (Cursor cursor = context.getContentResolver()
                .query(URI, PROJECTION, queryArgs, null)) {
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
            Log.e(TAG, "Cannot execute getAllByTitleLike statement", e);
            return Collections.emptyList();
        }
    }

    private static final class TrackEntityExtractor implements Function<Cursor, TrackEntity> {

        private static final Uri ARTWORK_URI =
                Uri.parse("content://media/external/audio/albumart");

        @Override
        public @NonNull TrackEntity apply(@NonNull Cursor cursor) {
            // ID
            long id = cursor.getLong(0);
            // Title (prefer TITLE, fallback to DISPLAY_NAME if not present)
            String title;
            if (!cursor.isNull(1)) {
                title = cursor.getString(1);
            } else {
                title = cursor.getString(2);
            }
            // Artist (in case of unknown artist the ID is present but not the name)
            Long artistId;
            if (!cursor.isNull(3)) {
                artistId = cursor.getLong(3);
            } else {
                artistId = null;
            }
            String artistName;
            if (!cursor.isNull(4)) {
                if (cursor.getString(4).equals("<unknown>")) {
                    artistName = null;
                } else {
                    artistName = cursor.getString(4);
                }
            } else {
                artistName = null;
            }
            // Album (in case of unknown album the ID is present but not the name)
            Long albumId;
            if (!cursor.isNull(5)) {
                albumId = cursor.getLong(5);
            } else {
                albumId = null;
            }
            String albumTitle;
            if (!cursor.isNull(6)) {
                if (cursor.getString(6).equals("<unknown>")) {
                    albumTitle = null;
                } else {
                    albumTitle = cursor.getString(6);
                }
            } else {
                albumTitle = null;
            }
            // Duration
            Duration duration = Duration.ofMillis(cursor.getLong(7));
            // Uri
            Uri uri = Uri.withAppendedPath(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    String.valueOf(id));
            // Artwork path
            Uri artworkPath;
            if (albumId != null) {
                artworkPath = Uri.withAppendedPath(ARTWORK_URI, String.valueOf(albumId));
            } else {
                artworkPath = null;
            }
            // Create object
            return new TrackEntity(id, title, artistId, artistName,
                    albumId, albumTitle, duration, uri, artworkPath);
        }

    }

}
