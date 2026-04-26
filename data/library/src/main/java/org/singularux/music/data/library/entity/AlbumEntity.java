package org.singularux.music.data.library.entity;

import android.net.Uri;

import androidx.annotation.Nullable;

import lombok.Value;

@Value
public class AlbumEntity {
    long id;
    @Nullable String title;
    @Nullable Long artistId;
    @Nullable String artistName;
    @Nullable Uri artworkPath;
    int numberOfTracks;
}
