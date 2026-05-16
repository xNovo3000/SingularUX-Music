package org.singularux.music.data.library;

import android.net.Uri;
import android.provider.MediaStore;

public class DataLibraryUtils {

    private DataLibraryUtils() {}

    public static final Uri TRACKS_URI = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

}
