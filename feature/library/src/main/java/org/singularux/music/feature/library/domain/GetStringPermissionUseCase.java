package org.singularux.music.feature.library.domain;

import androidx.annotation.NonNull;

import org.singularux.music.core.permission.MusicPermission;
import org.singularux.music.core.permission.MusicPermissionManager;

import javax.inject.Inject;

public class GetStringPermissionUseCase {

    private final MusicPermissionManager musicPermissionManager;

    @Inject
    public GetStringPermissionUseCase(MusicPermissionManager musicPermissionManager) {
        this.musicPermissionManager = musicPermissionManager;
    }

    public @NonNull String get(MusicPermission musicPermission) {
        return musicPermissionManager.getStringPermission(musicPermission);
    }

}
