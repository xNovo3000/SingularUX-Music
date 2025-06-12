package org.singularux.music.data.library.util;

import android.net.Uri;

import java.util.Optional;

public interface ArtworkUriGenerator {
    Optional<Uri> maybeGet(int albumId);
}
