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
import org.singularux.music.data.library.util.ArtworkUriGenerator;
import org.singularux.music.data.library.util.ArtworkUriGeneratorSupplier;

import java.time.Duration;
import java.util.ArrayList;
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
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ALBUM_ID,
            MediaStore.Audio.Media.DURATION
    };
    private static final String GET_ALL_SELECTION = MediaStore.Audio.Media.IS_MUSIC +
            " = ? AND " + MediaStore.Audio.Media.IS_TRASHED + " = ?";
    private static final String[] GET_ALL_SELECTION_ARGS = {"1", "0"};
    private static final String GET_ALL_SORT_ORDER = MediaStore.Audio.Media.DEFAULT_SORT_ORDER;

    private final Context context;
    private final MusicPermissionManager musicPermissionManager;
    private final ArtworkUriGeneratorSupplier artworkUriGeneratorSupplier;

    @Inject
    public TrackRepositoryAndroid(
            @ApplicationContext Context context,
            MusicPermissionManager musicPermissionManager,
            ArtworkUriGeneratorSupplier artworkUriGeneratorSupplier
    ) {
        this.context = context;
        this.musicPermissionManager = musicPermissionManager;
        this.artworkUriGeneratorSupplier = artworkUriGeneratorSupplier;
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
            ArtworkUriGenerator artworkUriGenerator = artworkUriGeneratorSupplier.get();
            List<TrackEntity> result = new ArrayList<>();
            while (cursor.moveToNext()) {
                // Extract data
                int id = cursor.getInt(0);
                String title;
                if (!cursor.isNull(1)) {
                    title = cursor.getString(1);
                } else {
                    title = cursor.getString(2);
                }
                String artistsName = null;
                if (!cursor.isNull(3)) {
                    artistsName = cursor.getString(3);
                }
                Uri artworkUri = null;
                if (!cursor.isNull(4)) {
                    int albumId = cursor.getInt(4);
                    artworkUri = artworkUriGenerator.maybeGet(albumId).orElse(null);
                }
                Duration duration = Duration.ZERO;
                if (!cursor.isNull(5)) {
                    int durationMillis = cursor.getInt(5);
                    duration = Duration.ofSeconds(durationMillis / 1_000, durationMillis % 1_000 * 1_000_000);
                }
                // Generate class and push into list
                TrackEntity track = new TrackEntity(id, title, artistsName, artworkUri, duration);
                result.add(track);
            }
            return result;
        } catch (Exception e) {
            Log.e(TAG, "Failed to fetch tracks", e);
            return Collections.emptyList();
        }
    }

}
