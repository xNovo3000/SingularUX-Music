package org.singularux.music.feature.playback.model;

import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.time.Duration;

import lombok.Data;

@Data
public class PlaybackInfo {
    private final long id;
    private final @NonNull String title;
    private final @Nullable Long artistId;
    private final @Nullable String artistName;
    private final @Nullable Long albumId;
    private final @Nullable String albumTitle;
    private final @Nullable Uri artworkUri;
    private final @NonNull Duration duration;
    private final boolean isPlaying;
    private final boolean hasNext;
}
