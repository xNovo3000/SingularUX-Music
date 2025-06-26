package org.singularux.music.core.permission;

import android.Manifest;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import lombok.experimental.SuperBuilder;

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@SuperBuilder
public class MusicPermissionManagerAndroid33 extends MusicPermissionManagerAndroid {

    @Override
    public @NonNull String getPermissionString(@NonNull MusicPermission permission) {
        switch (permission) {
            case READ_PHONE_STATE:
                return Manifest.permission.READ_PHONE_STATE;
            case READ_MUSIC:
                return Manifest.permission.READ_MEDIA_AUDIO;
            default:
                throw new IllegalArgumentException("Unexpected value: " + permission);
        }
    }

}
