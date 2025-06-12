package org.singularux.music.data.library;

import org.singularux.music.data.library.repository.TrackRepository;
import org.singularux.music.data.library.repository.TrackRepositoryAndroid;

import dagger.Binds;
import dagger.Module;
import dagger.hilt.InstallIn;
import dagger.hilt.android.components.ActivityRetainedComponent;
import dagger.hilt.android.scopes.ActivityRetainedScoped;

@Module
@InstallIn(ActivityRetainedComponent.class)
public abstract class DataLibraryModuleBindings {

    @Binds
    @ActivityRetainedScoped
    public abstract TrackRepository bindsTrackRepository(TrackRepositoryAndroid trackRepository);

}
