package org.singularux.music.feature.tracklist.util;

import androidx.annotation.NonNull;

import org.singularux.music.data.library.entity.TrackEntity;
import org.singularux.music.feature.tracklist.ui.list.item.TrackListItem;

import java.util.function.Function;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class TrackEntityToTrackListItemMapper implements Function<TrackEntity, TrackListItem> {

    private final long currentPlayingId;

    @Override
    public @NonNull TrackListItem apply(@NonNull TrackEntity trackEntity) {
        return new TrackListItem(trackEntity.getId(), trackEntity.getTitle(),
                trackEntity.getArtistId(), trackEntity.getArtistName(),
                trackEntity.getAlbumId(), trackEntity.getAlbumTitle(),
                trackEntity.getArtworkUri(), trackEntity.getDuration(),
                trackEntity.getId() == currentPlayingId);
    }

}
