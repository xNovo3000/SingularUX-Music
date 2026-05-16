package org.singularux.music.data.library.entity;

import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.time.Duration;

import lombok.Value;

@Value
public class TrackEntity {
    long id;
    @NonNull String title;
    @Nullable Long artistId;
    @Nullable String artistName;
    @Nullable Long albumId;
    @Nullable String albumTitle;
    @NonNull Duration duration;
    @NonNull Uri uri;
    @Nullable Uri artworkPath;
}
