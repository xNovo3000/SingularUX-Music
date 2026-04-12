package org.singularux.music.core.permission;

import android.content.Context;
import android.os.Build;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.components.ActivityRetainedComponent;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.android.scopes.ActivityRetainedScoped;

@Module
@InstallIn(ActivityRetainedComponent.class)
public class CorePermissionActivityRetainedModule {

    @Provides
    @ActivityRetainedScoped
    public MusicPermissionManager providesMusicPermissionManager(
            @ApplicationContext Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return MusicPermissionManagerAndroid33.builder()
                    .context(context)
                    .build();
        } else {
            return MusicPermissionManagerAndroid26.builder()
                    .context(context)
                    .build();
        }
    }

}
