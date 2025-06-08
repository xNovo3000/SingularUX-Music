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
        if (permission == MusicPermission.READ_MUSIC) {
            return Manifest.permission.READ_MEDIA_AUDIO;
        }
        return "";
    }

}
