package org.singularux.music.feature.playback.data;

import android.net.Uri;

import androidx.annotation.Nullable;

import java.time.Duration;

import lombok.Value;

@Value
public class PlaybackInfo {
    long id;
    String title;
    @Nullable Long artistId;
    @Nullable String artistName;
    @Nullable Long albumId;
    @Nullable String albumName;
    Duration duration;
    @Nullable Uri artworkUri;
    @Nullable String playbackToken;
}
