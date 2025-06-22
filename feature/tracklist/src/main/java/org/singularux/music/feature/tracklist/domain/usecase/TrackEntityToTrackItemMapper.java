package org.singularux.music.feature.tracklist.domain.usecase;

import androidx.annotation.NonNull;

import org.singularux.music.data.library.entity.TrackEntity;
import org.singularux.music.feature.tracklist.domain.model.TrackItem;

import java.util.function.Function;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
class TrackEntityToTrackItemMapper implements Function<TrackEntity, TrackItem> {

    private final long currentPlayingId;

    @Override
    public @NonNull TrackItem apply(@NonNull TrackEntity trackEntity) {
        return new TrackItem(trackEntity.getId(), trackEntity.getTitle(),
                trackEntity.getArtistId(), trackEntity.getArtistName(),
                trackEntity.getAlbumId(), trackEntity.getAlbumTitle(),
                trackEntity.getArtworkUri(), trackEntity.getDuration(),
                trackEntity.getId() == currentPlayingId);
    }

}
