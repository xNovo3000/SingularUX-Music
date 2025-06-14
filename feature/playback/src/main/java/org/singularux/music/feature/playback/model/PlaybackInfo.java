package org.singularux.music.feature.playback.model;

import android.net.Uri;

import androidx.annotation.Nullable;

import lombok.Data;

@Data
public class PlaybackInfo {
    private final int id;
    private final @Nullable String title;
    private final @Nullable String artistsName;
    private final @Nullable Uri artworkUri;
    private final boolean isPlaying;
    private final boolean hasPrevious;
    private final boolean hasNext;
}
