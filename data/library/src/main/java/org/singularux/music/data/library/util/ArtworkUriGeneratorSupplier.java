package org.singularux.music.data.library.util;

import android.content.Context;
import android.os.Build;

import java.util.function.Supplier;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ArtworkUriGeneratorSupplier implements Supplier<ArtworkUriGenerator> {

    private final Context context;

    @Override
    public ArtworkUriGenerator get() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            return new ArtworkUriGenerator29(context);
        } else {
            return new ArtworkUriGenerator26();
        }
    }

}
