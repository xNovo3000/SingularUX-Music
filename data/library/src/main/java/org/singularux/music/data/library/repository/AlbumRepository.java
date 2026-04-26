package org.singularux.music.data.library.repository;

import androidx.annotation.NonNull;

import org.singularux.music.data.library.entity.AlbumEntity;

import java.util.List;
import java.util.Optional;

public interface AlbumRepository {
    @NonNull List<AlbumEntity> getAll();
    @NonNull Optional<AlbumEntity> getById(long id);
    @NonNull List<AlbumEntity> getAllByTitleLike(@NonNull String query);
    @NonNull List<AlbumEntity> getAllByArtistId(long artistId);
}
