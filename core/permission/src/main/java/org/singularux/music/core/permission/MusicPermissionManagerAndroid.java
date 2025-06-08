package org.singularux.music.core.permission;

import android.content.Context;
import android.content.pm.PackageManager;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import lombok.experimental.SuperBuilder;

@SuperBuilder
public abstract class MusicPermissionManagerAndroid implements MusicPermissionManager {

    private final Context context;

    @Override
    public boolean hasPermission(@NonNull MusicPermission permission) {
        return ContextCompat.checkSelfPermission(context, getPermissionString(permission)) ==
                PackageManager.PERMISSION_GRANTED;
    }

}
