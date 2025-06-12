package org.singularux.music.data.library;

import android.content.Context;

import org.singularux.music.data.library.util.ArtworkUriGeneratorSupplier;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.components.ActivityRetainedComponent;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.android.scopes.ActivityRetainedScoped;

@Module
@InstallIn(ActivityRetainedComponent.class)
public class DataLibraryModule {

    @Provides
    @ActivityRetainedScoped
    public ArtworkUriGeneratorSupplier providesArtworkUriGeneratorSupplier(
            @ApplicationContext Context context
    ) {
        return new ArtworkUriGeneratorSupplier(context);
    }

}
