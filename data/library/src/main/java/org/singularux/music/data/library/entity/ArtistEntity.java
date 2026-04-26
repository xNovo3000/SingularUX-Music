package org.singularux.music.data.library.entity;

import androidx.annotation.Nullable;

import lombok.Value;

@Value
public class ArtistEntity {
    long id;
    @Nullable String name;
    int numberOfTracks;
    int numberOfAlbums;
}
