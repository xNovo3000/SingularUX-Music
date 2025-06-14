package org.singularux.music.data.library.util;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.function.Function;

import javax.inject.Inject;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(onConstructor_ = {@Inject})
public class ArtworkUriRetriever implements Function<Long, Uri> {

    private static final String TAG = "ArtworkUriRetriever";

    private final Context context;

    private static final Uri ARTWORK_URI = Uri
            .parse("content://media/external/audio/albumart");

    @Override
    public @Nullable Uri apply(@NonNull Long albumId) {
        // Create Uri, check if exists and return the value if it is ok
        Uri artworkUri = Uri.withAppendedPath(ARTWORK_URI, String.valueOf(albumId));
        try (InputStream _is = context.getContentResolver().openInputStream(artworkUri)) {
            return artworkUri;
        } catch (IOException e) {
            Log.d(TAG, "Artwork Uri is not valid, skipping");
            return null;
        }
    }

}
