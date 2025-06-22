package org.singularux.music.data.library.repository;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import androidx.annotation.NonNull;

import org.singularux.music.core.permission.MusicPermission;
import org.singularux.music.core.permission.MusicPermissionManager;
import org.singularux.music.data.library.entity.TrackEntity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.qualifiers.ApplicationContext;

public class TrackRepositoryAndroid implements TrackRepository {

    private static final String TAG = "TrackRepositoryAndroid";

    public static final Uri URI = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

    private static final String[] GET_ALL_PROJECTION = {
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE, MediaStore.Audio.Media.DISPLAY_NAME,
            MediaStore.Audio.Media.ARTIST_ID, MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ALBUM_ID, MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.DURATION
    };
    private static final String GET_ALL_SELECTION =
            MediaStore.Audio.Media.IS_MUSIC + " = ? AND " +
            MediaStore.Audio.Media.IS_TRASHED + " = ?";
    private static final String[] GET_ALL_SELECTION_ARGS = {"1", "0"};
    private static final String GET_ALL_SORT_ORDER = MediaStore.Audio.Media.DEFAULT_SORT_ORDER;

    private static final String GET_ALL_BY_TITLE_LIKE_SELECTION =
            MediaStore.Audio.Media.IS_MUSIC + " = ? AND " +
            MediaStore.Audio.Media.IS_TRASHED + " = ? AND (" +
            MediaStore.Audio.Media.TITLE + " LIKE ? OR " +
            MediaStore.Audio.Media.DISPLAY_NAME + " LIKE ?)";

    private final Context context;
    private final MusicPermissionManager musicPermissionManager;
    private final CursorToTrackEntityMapper mapper = new CursorToTrackEntityMapper();

    @Inject
    public TrackRepositoryAndroid(
            @ApplicationContext Context context,
            MusicPermissionManager musicPermissionManager
    ) {
        this.context = context;
        this.musicPermissionManager = musicPermissionManager;
    }

    @Override
    public @NonNull List<TrackEntity> getAll() {
        // Check for permission
        if (!musicPermissionManager.hasPermission(MusicPermission.READ_MUSIC)) {
            Log.d(TAG, "Cannot load tracks, missing READ_MUSIC permission");
            return Collections.emptyList();
        }
        // Query system
        try (Cursor cursor = context.getContentResolver().query(URI, GET_ALL_PROJECTION,
                GET_ALL_SELECTION, GET_ALL_SELECTION_ARGS, GET_ALL_SORT_ORDER)
        ) {
            if (cursor == null) {
                Log.e(TAG, "Invalid query, cursor is null");
                return Collections.emptyList();
            }
            // Build the list of tracks
            List<TrackEntity> result = new ArrayList<>();
            while (cursor.moveToNext()) {
                result.add(mapper.apply(cursor));
            }
            return result;
        } catch (Exception e) {
            Log.e(TAG, "Failed to fetch tracks", e);
            return Collections.emptyList();
        }
    }

    @Override
    public @NonNull List<TrackEntity> getAllByTitleLike(@NonNull String title) {
        // Title must contain at least one character
        if (title.isEmpty()) {
            Log.d(TAG, "Title is empty");
            return Collections.emptyList();
        }
        // Check for permission
        if (!musicPermissionManager.hasPermission(MusicPermission.READ_MUSIC)) {
            Log.d(TAG, "Cannot load tracks, missing READ_MUSIC permission");
            return Collections.emptyList();
        }
        // Create selection args
        String[] selectionArgs = Arrays.copyOf(GET_ALL_SELECTION_ARGS,
                GET_ALL_SELECTION_ARGS.length + 2);
        selectionArgs[selectionArgs.length - 2] = "%" + title + "%";
        selectionArgs[selectionArgs.length - 1] = "%" + title + "%";
        // Query system
        try (Cursor cursor = context.getContentResolver().query(URI, GET_ALL_PROJECTION,
                GET_ALL_BY_TITLE_LIKE_SELECTION, selectionArgs, GET_ALL_SORT_ORDER)
        ) {
            if (cursor == null) {
                Log.e(TAG, "Invalid query, cursor is null");
                return Collections.emptyList();
            }
            // Build the list of tracks
            List<TrackEntity> result = new ArrayList<>();
            while (cursor.moveToNext()) {
                result.add(mapper.apply(cursor));
            }
            return result;
        } catch (Exception e) {
            Log.e(TAG, "Failed to fetch tracks", e);
            return Collections.emptyList();
        }
    }

}
