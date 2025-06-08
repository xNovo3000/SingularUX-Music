package org.singularux.music.core.permission;

import androidx.annotation.NonNull;

import java.util.List;
import java.util.stream.Collectors;

public interface MusicPermissionManager {

    boolean hasPermission(@NonNull MusicPermission permission);

    @NonNull String getPermissionString(@NonNull MusicPermission permission);

    default @NonNull List<String> getPermissionStrings(@NonNull List<MusicPermission> permissions) {
        return permissions.stream()
                .map(this::getPermissionString)
                .collect(Collectors.toList());
    }

}
