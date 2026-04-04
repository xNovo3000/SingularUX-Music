package org.singularux.music.core;

import androidx.annotation.NonNull;

import java.util.Arrays;

public interface MusicPermissionManager {

    boolean hasPermission(@NonNull MusicPermission permission);
    @NonNull String getPermissionName(@NonNull MusicPermission permission);

    default boolean hasPermissions(@NonNull MusicPermission... permissions) {
        return Arrays.stream(permissions)
                .allMatch(this::hasPermission);
    }

    default @NonNull String[] getPermissionsName(@NonNull MusicPermission... permissions) {
        return Arrays.stream(permissions)
                .map(this::getPermissionName)
                .toArray(String[]::new);
    }

}
