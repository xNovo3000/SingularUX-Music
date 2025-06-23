package org.singularux.music.feature.tracklist.ui.search.item;

import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.time.Duration;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class SearchListItemTrack extends SearchListItem {
    private final @NonNull String title;
    private final @Nullable Long artistId;
    private final @Nullable String artistName;
    private final @Nullable Long albumId;
    private final @Nullable String albumTitle;
    private final @Nullable Uri artworkUri;
    private final @NonNull Duration duration;
}
