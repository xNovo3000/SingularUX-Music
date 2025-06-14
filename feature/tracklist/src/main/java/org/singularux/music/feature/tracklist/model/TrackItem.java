package org.singularux.music.feature.tracklist.model;

import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.time.Duration;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TrackItem {
    private final long id;
    private final @NonNull String title;
    private final @Nullable Long artistId;
    private final @Nullable String artistName;
    private final @Nullable Long albumId;
    private final @Nullable String albumName;
    private final @Nullable Uri artworkUri;
    @Builder.Default private final @NonNull Duration duration = Duration.ZERO;
    private final boolean isCurrentlyPlaying;
}
