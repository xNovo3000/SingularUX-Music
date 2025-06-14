package org.singularux.music.data.library.entity;

import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.time.Duration;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TrackEntity {
    private final long id;
    private final @NonNull String title;
    private final @Nullable Long artistId;
    private final @Nullable String artistName;
    private final @Nullable Long albumId;
    private final @Nullable String albumName;
    private final @Nullable Uri artworkUri;
    @Builder.Default private final @NonNull Duration duration = Duration.ZERO;
}
