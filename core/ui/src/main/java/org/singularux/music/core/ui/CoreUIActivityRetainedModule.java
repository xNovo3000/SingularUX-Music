package org.singularux.music.core.ui;

import android.content.Context;

import com.squareup.picasso.LruCache;
import com.squareup.picasso.Picasso;

import org.singularux.music.core.threading.ComputationExecutorService;
import org.singularux.music.core.ui.picasso.NoOpDownloader;

import java.util.concurrent.ExecutorService;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.components.ActivityRetainedComponent;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.android.scopes.ActivityRetainedScoped;

@Module
@InstallIn(ActivityRetainedComponent.class)
public class CoreUIActivityRetainedModule {

    private static final int PICASSO_CACHE_SIZE_BYTES = 8 * 1024 * 1024;

    @Provides
    @ActivityRetainedScoped
    public Picasso providesPicasso(
            @ApplicationContext Context context,
            @ComputationExecutorService ExecutorService computationExecutorService) {
        return new Picasso.Builder(context)
                .downloader(new NoOpDownloader())
                .executor(computationExecutorService)
                .memoryCache(new LruCache(PICASSO_CACHE_SIZE_BYTES))
                .build();
    }

}
