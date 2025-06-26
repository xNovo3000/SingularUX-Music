package org.singularux.music.core.permission;

import android.Manifest;

import androidx.annotation.NonNull;

import lombok.experimental.SuperBuilder;

@SuperBuilder
public class MusicPermissionManagerAndroid26 extends MusicPermissionManagerAndroid {

    @Override
    public @NonNull String getPermissionString(@NonNull MusicPermission permission) {
        switch (permission) {
            case READ_PHONE_STATE:
                return Manifest.permission.READ_PHONE_STATE;
            case READ_MUSIC:
                return Manifest.permission.READ_EXTERNAL_STORAGE;
            default:
                throw new IllegalArgumentException("Unexpected value: " + permission);
        }
    }

}
