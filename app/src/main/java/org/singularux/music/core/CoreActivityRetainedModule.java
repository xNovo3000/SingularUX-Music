package org.singularux.music.core;

import android.content.Context;
import android.os.Build;

import com.squareup.picasso.LruCache;
import com.squareup.picasso.Picasso;

import org.singularux.music.core.permission.MusicPermissionManager;
import org.singularux.music.core.permission.MusicPermissionManagerAndroid26;
import org.singularux.music.core.permission.MusicPermissionManagerAndroid33;
import org.singularux.music.core.playback.MusicControllerFacade;
import org.singularux.music.core.threading.IOExecutorService;
import org.singularux.music.core.thumbnail.PicassoNoOpDownloader;

import java.util.concurrent.ExecutorService;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.components.ActivityRetainedComponent;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.android.scopes.ActivityRetainedScoped;

@Module
@InstallIn(ActivityRetainedComponent.class)
public class CoreActivityRetainedModule {

    private static final int THUMBNAIL_CACHE_SIZE_BYTES = 8 * 1024 * 1024;

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

    @Provides
    @ActivityRetainedScoped
    public MusicControllerFacade providesMusicControllerFacade(
            @ApplicationContext Context context) {
        return new MusicControllerFacade(context);
    }

}
