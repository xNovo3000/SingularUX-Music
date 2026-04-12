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
    public @NonNull String getPermissionName(@NonNull MusicPermission permission) {
        switch (permission) {
            case READ_MEDIA_AUDIO:
                return Manifest.permission.READ_MEDIA_AUDIO;
            case READ_PHONE_STATE:
                return Manifest.permission.READ_PHONE_STATE;
        }
        throw new IllegalArgumentException("Invalid permission enum: " + permission);
    }

}
