package org.singularux.music.feature.library.domain;

import androidx.annotation.NonNull;

import org.singularux.music.core.permission.MusicPermission;
import org.singularux.music.core.permission.MusicPermissionManager;

import javax.inject.Inject;

public class GetReadMusicPermissionUseCase {

    private final MusicPermissionManager musicPermissionManager;

    @Inject
    public GetReadMusicPermissionUseCase(MusicPermissionManager musicPermissionManager) {
        this.musicPermissionManager = musicPermissionManager;
    }

    public @NonNull String get() {
        return musicPermissionManager.getPermissionString(MusicPermission.READ_MUSIC);
    }

}
