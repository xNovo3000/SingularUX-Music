package org.singularux.music.data.library.repository;

import androidx.annotation.NonNull;

import org.singularux.music.data.library.entity.TrackEntity;

import java.util.List;

public interface TrackRepository {
    @NonNull List<TrackEntity> getAll();
    @NonNull List<TrackEntity> getAllByTitleLike(@NonNull String title);
}
