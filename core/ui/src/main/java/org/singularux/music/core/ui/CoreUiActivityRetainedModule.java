package org.singularux.music.core.ui;

import android.content.Context;

import com.squareup.picasso.LruCache;
import com.squareup.picasso.Picasso;

import org.singularux.music.core.threading.IOExecutorService;

import java.util.concurrent.ExecutorService;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.components.ActivityRetainedComponent;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.android.scopes.ActivityRetainedScoped;

@Module
@InstallIn(ActivityRetainedComponent.class)
public class CoreUiActivityRetainedModule {

    private static final int THUMBNAIL_CACHE_SIZE_BYTES = 8 * 1024 * 1024;

    @Provides
    @ActivityRetainedScoped
    public Picasso providesPicasso(@ApplicationContext Context context,
                                   @IOExecutorService ExecutorService ioExecutorService) {
        return new Picasso.Builder(context)
                .downloader(new PicassoNoOpDownloader())
                .executor(ioExecutorService)
                .memoryCache(new LruCache(THUMBNAIL_CACHE_SIZE_BYTES))
                .build();
    }

}
