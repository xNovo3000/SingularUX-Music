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

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import javax.inject.Inject;

import dagger.hilt.android.qualifiers.ApplicationContext;

public class TrackRepositoryAndroid implements TrackRepository {

    private static final String TAG = "TrackRepositoryAndroid";

    public static final Uri URI = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
    private static final Uri ARTWORK_URI = Uri.parse("content://media/external/audio/albumart");

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

    private final Context context;
    private final MusicPermissionManager musicPermissionManager;

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
        try (Cursor cursor = context.getContentResolver().query(URI,
                GET_ALL_PROJECTION, GET_ALL_SELECTION, GET_ALL_SELECTION_ARGS, GET_ALL_SORT_ORDER)
        ) {
            if (cursor == null) {
                Log.e(TAG, "Invalid query, cursor is null");
                return Collections.emptyList();
            }
            // Build the list of tracks
            List<TrackEntity> result = new ArrayList<>();
            while (cursor.moveToNext()) {
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
                TrackEntity trackEntity = new TrackEntity(id, title, artistId, artistName,
                        albumId, albumTitle, artworkUri, duration);
                result.add(trackEntity);
            }
            return result;
        } catch (Exception e) {
            Log.e(TAG, "Failed to fetch tracks", e);
            return Collections.emptyList();
        }
    }

}
