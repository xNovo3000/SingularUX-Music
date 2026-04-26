package org.singularux.music.core.threading;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;

@Module
@InstallIn(SingletonComponent.class)
public class CoreThreadingSingletonModule {

    private static final int NUMBER_OF_CPU_CORES = Runtime.getRuntime().availableProcessors();

    private static final int IO_KEEP_ALIVE_SECONDS = 60;
    private static final int IO_MAX_THREADS = 32;

    @Provides
    @Singleton
    @ComputationExecutorService
    public ExecutorService providesComputationExecutorService() {
        return Executors.newFixedThreadPool(NUMBER_OF_CPU_CORES,
                new BackgroundThreadFactory("Background"));
    }

    @Provides
    @Singleton
    @IOExecutorService
    public ExecutorService providesIOExecutorService() {
        return new ThreadPoolExecutor(0, Math.max(NUMBER_OF_CPU_CORES, IO_MAX_THREADS),
                IO_KEEP_ALIVE_SECONDS, TimeUnit.SECONDS,
                new SynchronousQueue<>(), new BackgroundThreadFactory("IO"));
    }

}