package org.singularux.music.core.permission;

import androidx.annotation.NonNull;

import java.util.Arrays;

public interface MusicPermissionManager {

    boolean hasPermission(@NonNull MusicPermission permission);
    @NonNull String getStringPermission(@NonNull MusicPermission permission);

    default boolean hasPermissions(@NonNull MusicPermission... permissions) {
        return Arrays.stream(permissions)
                .allMatch(this::hasPermission);
    }

    default @NonNull String[] getStringPermissions(@NonNull MusicPermission... permissions) {
        return Arrays.stream(permissions)
                .map(this::getStringPermission)
                .toArray(String[]::new);
    }

}
