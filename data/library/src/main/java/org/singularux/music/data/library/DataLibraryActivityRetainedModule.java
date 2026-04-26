package org.singularux.music.data.library;

import android.content.Context;

import org.singularux.music.core.permission.MusicPermissionManager;
import org.singularux.music.data.library.repository.TrackRepository;
import org.singularux.music.data.library.repository.TrackRepositoryAndroid;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.components.ActivityRetainedComponent;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.android.scopes.ActivityRetainedScoped;

@Module
@InstallIn(ActivityRetainedComponent.class)
public abstract class DataLibraryActivityRetainedModule {

    @Provides
    @ActivityRetainedScoped
    public TrackRepository providesTrackRepository(@ApplicationContext Context context,
                                                   MusicPermissionManager musicPermissionManager) {
        return new TrackRepositoryAndroid(context, musicPermissionManager);
    }

}
