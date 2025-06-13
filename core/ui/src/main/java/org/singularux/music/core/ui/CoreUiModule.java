package org.singularux.music.core.ui;

import android.content.Context;

import com.squareup.picasso.LruCache;
import com.squareup.picasso.Picasso;

import org.singularux.music.core.ui.picasso.PicassoThreadPoolExecutor;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.components.ActivityRetainedComponent;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.android.scopes.ActivityRetainedScoped;

@Module
@InstallIn(ActivityRetainedComponent.class)
public class CoreUiModule {

    private static final int MAX_BYTES = 4 * 1024 * 1024;  // 4 MB

    @Provides
    @ActivityRetainedScoped
    public Picasso providesPicasso(@ApplicationContext Context context) {
        return new Picasso.Builder(context)
                .executor(new PicassoThreadPoolExecutor())
                .memoryCache(new LruCache(MAX_BYTES))
                .build();
    }

}
