package org.singularux.music.data.library.entity;

import android.net.Uri;

import androidx.annotation.Nullable;

import lombok.Data;

@Data
public class AlbumEntity {
    private final int id;
    private final @Nullable Uri artworkUri;
    // TODO: At the moment we are only interested in the artwork Uri
}
