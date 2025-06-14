package org.singularux.music.data.library.entity;

import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.time.Duration;

import lombok.Data;

@Data
public class TrackEntity {
    private final long id;
    private final @NonNull String title;
    private final @Nullable Long artistId;
    private final @Nullable String artistName;
    private final @Nullable Long albumId;
    private final @Nullable String albumTitle;
    private final @Nullable Uri artworkUri;
    private final @NonNull Duration duration;
}
