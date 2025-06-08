package org.singularux.music.feature.tracklist.model;

import android.net.Uri;

import androidx.annotation.NonNull;

import java.time.Duration;

import lombok.Data;

@Data
public class TrackItem {
    private final int id;
    private final @NonNull String title;
    private final String artistsName;
    private final Uri artworkUri;
    private final Duration duration;
}
