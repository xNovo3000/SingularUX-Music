package org.singularux.music.core.playback;

import android.content.Context;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.components.ActivityRetainedComponent;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.android.scopes.ActivityRetainedScoped;

@Module
@InstallIn(ActivityRetainedComponent.class)
public class CorePlaybackActivityRetainedModule {

    @Provides
    @ActivityRetainedScoped
    public MusicControllerFacade providesMusicControllerFacade(
            @ApplicationContext Context context) {
        return new MusicControllerFacade(context);
    }

}