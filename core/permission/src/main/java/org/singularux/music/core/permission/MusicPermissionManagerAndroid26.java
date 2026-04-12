package org.singularux.music.core.permission;

import android.Manifest;

import androidx.annotation.NonNull;

import lombok.experimental.SuperBuilder;

@SuperBuilder
public class MusicPermissionManagerAndroid26 extends MusicPermissionManagerAndroid {

    @Override
    public @NonNull String getPermissionName(@NonNull MusicPermission permission) {
        switch (permission) {
            case READ_MEDIA_AUDIO:
                return Manifest.permission.READ_EXTERNAL_STORAGE;
            case READ_PHONE_STATE:
                return Manifest.permission.READ_PHONE_STATE;
        }
        throw new IllegalArgumentException("Invalid permission enum: " + permission);
    }

}
