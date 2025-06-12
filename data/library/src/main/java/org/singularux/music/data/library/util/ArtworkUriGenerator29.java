package org.singularux.music.data.library.util;

import android.content.Context;
import android.net.Uri;
import android.provider.MediaStore;

import androidx.annotation.RequiresApi;

import java.util.Optional;

import lombok.RequiredArgsConstructor;

@RequiresApi(29)
@RequiredArgsConstructor
public class ArtworkUriGenerator29 implements ArtworkUriGenerator {

    private static final String TAG = "ArtworkUriGenerator29";

    private final Context context;

    @Override
    public Optional<Uri> maybeGet(int albumId) {
        // TODO: Check if Uri exists
        return Optional.of(Uri.withAppendedPath(MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
                String.valueOf(albumId)));
    }

}
