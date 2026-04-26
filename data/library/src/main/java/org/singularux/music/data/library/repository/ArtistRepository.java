package org.singularux.music.data.library.repository;

import androidx.annotation.NonNull;

import org.singularux.music.data.library.entity.ArtistEntity;

import java.util.List;
import java.util.Optional;

public interface ArtistRepository {
    @NonNull List<ArtistEntity> getAll();
    @NonNull Optional<ArtistEntity> getById(long id);
    @NonNull List<ArtistEntity> getAllByTitleLike(@NonNull String query);
}
