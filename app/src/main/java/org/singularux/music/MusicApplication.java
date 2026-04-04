package org.singularux.music;

import android.app.Application;

import org.singularux.music.core.ComputationExecutorService;
import org.singularux.music.core.IOExecutorService;

import java.util.concurrent.ExecutorService;

import javax.inject.Inject;

import dagger.hilt.android.HiltAndroidApp;
import io.reactivex.rxjava3.plugins.RxJavaPlugins;
import io.reactivex.rxjava3.schedulers.Schedulers;

@HiltAndroidApp
public class MusicApplication extends Application {

    public @Inject @ComputationExecutorService ExecutorService computationExecutorService;
    public @Inject @IOExecutorService ExecutorService ioExecutorService;

    @Override
    public void onCreate() {
        super.onCreate();
        // Set RxJava custom schedulers
        RxJavaPlugins.setInitComputationSchedulerHandler(
                schedulerSupplier -> Schedulers.from(computationExecutorService));
        RxJavaPlugins.setIoSchedulerHandler(
                schedulerSupplier -> Schedulers.from(ioExecutorService));
    }

}
