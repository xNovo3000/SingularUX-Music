package org.singularux.music.data.library.util;

import android.content.Context;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

import lombok.RequiredArgsConstructor;

@RequiresApi(29)
@RequiredArgsConstructor
public class ArtworkUriGenerator29 implements ArtworkUriGenerator {

    private static final String TAG = "ArtworkUriGenerator29";

    private final Context context;

    @Override
    public Optional<Uri> maybeGet(int albumId) {
        // Generate Uri, try to open it and if it opens, than the album art exists
        Uri maybeAlbumArtUri = Uri.withAppendedPath(MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
                String.valueOf(albumId));
        try (InputStream _is = context.getContentResolver().openInputStream(maybeAlbumArtUri)) {
            return Optional.of(maybeAlbumArtUri);
        } catch (FileNotFoundException e) {
            Log.d(TAG, "Uri for albumId " + albumId + " does not exists", e);
            return Optional.empty();
        } catch (IOException e) {
            Log.d(TAG, "Failed to load albumId " + albumId, e);
            return Optional.empty();
        }
    }

}
