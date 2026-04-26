package org.singularux.music.feature.tracklist.util;

import androidx.annotation.NonNull;

import org.singularux.music.data.library.entity.TrackEntity;
import org.singularux.music.feature.tracklist.ui.search.item.SearchListItem;
import org.singularux.music.feature.tracklist.ui.search.item.SearchListItemTrack;

import java.util.function.Function;

public final class TrackEntityToSearchListItemMapper
        implements Function<TrackEntity, SearchListItem> {

    @Override
    public @NonNull SearchListItem apply(@NonNull TrackEntity trackEntity) {
        return SearchListItemTrack.builder()
                .id(trackEntity.getId())
                .title(trackEntity.getTitle())
                .artistId(trackEntity.getArtistId())
                .artistName(trackEntity.getArtistName())
                .albumId(trackEntity.getAlbumId())
                .albumTitle(trackEntity.getAlbumTitle())
                .artworkUri(trackEntity.getArtworkPath())
                .duration(trackEntity.getDuration())
                .build();
    }

}
