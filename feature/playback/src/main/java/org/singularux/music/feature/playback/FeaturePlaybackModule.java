package org.singularux.music.feature.playback;

import android.content.Context;

import org.singularux.music.feature.playback.foreground.MusicControllerFacade;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.components.ActivityRetainedComponent;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.android.scopes.ActivityRetainedScoped;

@Module
@InstallIn(ActivityRetainedComponent.class)
public class FeaturePlaybackModule {

    @Provides
    @ActivityRetainedScoped
    public MusicControllerFacade getMusicControllerFacade(@ApplicationContext Context context) {
        return new MusicControllerFacade(context);
    }

}
