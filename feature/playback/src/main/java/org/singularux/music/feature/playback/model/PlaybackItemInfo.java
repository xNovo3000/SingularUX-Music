package org.singularux.music.feature.playback.model;

import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.time.Duration;

import lombok.Value;

@Value
public class PlaybackItemInfo {
    long id;
    @NonNull String title;
    @Nullable Long artistId;
    @Nullable String artistName;
    @Nullable Long albumId;
    @Nullable String albumTitle;
    @Nullable Uri artworkPath;
    @NonNull Duration duration;
    @Nullable String playingFrom;
}
