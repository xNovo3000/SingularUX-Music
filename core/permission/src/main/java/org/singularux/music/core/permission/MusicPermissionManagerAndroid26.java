package org.singularux.music.core.permission;

import android.Manifest;

import androidx.annotation.NonNull;

import lombok.experimental.SuperBuilder;

@SuperBuilder
public class MusicPermissionManagerAndroid26 extends MusicPermissionManagerAndroid {

    @Override
    public @NonNull String getPermissionString(@NonNull MusicPermission permission) {
        if (permission == MusicPermission.READ_MUSIC) {
            return Manifest.permission.READ_EXTERNAL_STORAGE;
        }
        return "";
    }

}
