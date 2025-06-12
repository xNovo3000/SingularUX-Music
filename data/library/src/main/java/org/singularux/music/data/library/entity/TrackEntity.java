package org.singularux.music.data.library.entity;

import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.time.Duration;

import lombok.Data;

@Data
public class TrackEntity {
    private final int id;
    private final @NonNull String title;
    private final @Nullable String artistsName;
    private final @Nullable Uri artworkUri;
    private final @NonNull Duration duration;
}
