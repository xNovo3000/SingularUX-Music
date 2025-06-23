package org.singularux.music.feature.tracklist.ui.list.item;

import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.time.Duration;

import lombok.Data;

@Data
public class TrackListItem {
    private final long id;
    private final @NonNull String title;
    private final @Nullable Long artistId;
    private final @Nullable String artistName;
    private final @Nullable Long albumId;
    private final @Nullable String albumTitle;
    private final @Nullable Uri artworkUri;
    private final @NonNull Duration duration;
    private final boolean isCurrentlyPlaying;
}

