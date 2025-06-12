package org.singularux.music.data.library.repository;

import org.singularux.music.data.library.entity.TrackEntity;

import java.util.List;

public interface TrackRepository {
    List<TrackEntity> getAll();
}
