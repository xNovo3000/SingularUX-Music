package org.singularux.music.core.playback;

import android.content.ComponentName;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.media3.session.MediaController;
import androidx.media3.session.SessionToken;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

import java.util.Objects;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.core.SingleObserver;
import io.reactivex.rxjava3.disposables.Disposable;
import lombok.Getter;

public class MusicControllerFacade {

    private static final String TAG = "MusicControllerFacade";

    private static final String PLAYBACK_SERVICE_CLASS =
            "org.singularux.music.feature.playback.presentation.MusicPlaybackService";

    private final Single<MediaController> mediaControllerSingle;
    private @Nullable @Getter MediaController maybeMediaController = null;

    public MusicControllerFacade(Context context) {
        // Create MediaController future
        ComponentName componentName = new ComponentName(context, PLAYBACK_SERVICE_CLASS);
        SessionToken sessionToken = new SessionToken(context, componentName);
        ListenableFuture<MediaController> mediaControllerFuture = new MediaController
                .Builder(context, sessionToken)
                .buildAsync();
        // Switch from future to rx flow
        this.mediaControllerSingle = Single.create(emitter -> {
            Futures.addCallback(mediaControllerFuture, new FutureCallback<>() {
                @Override
                public void onSuccess(@NonNull MediaController result) {
                    emitter.onSuccess(result);
                }
                @Override
                public void onFailure(@NonNull Throwable t) {
                    emitter.onError(t);
                }
            }, ContextCompat.getMainExecutor(context));
        });
        // Listen for instance
        this.mediaControllerSingle.observeOn(AndroidSchedulers.mainThread())
                .subscribe(new MediaControllerObserver());
    }

    public void release() {
        if (maybeMediaController != null) {
            maybeMediaController.release();
        } else {
            mediaControllerSingle.observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new MediaControllerReleaser());
        }
    }

    public @NonNull MediaController requireMediaController() {
        return Objects.requireNonNull(maybeMediaController);
    }

    public boolean isReady() {
        return maybeMediaController != null && maybeMediaController.isConnected();
    }

    private class MediaControllerObserver implements SingleObserver<MediaController> {

        @Override
        public void onSubscribe(@NonNull Disposable d) {}

        @Override
        public void onSuccess(@NonNull MediaController mediaController) {
            Log.d(TAG, "Loading MediaController");
            maybeMediaController = mediaController;
        }

        @Override
        public void onError(@NonNull Throwable e) {
            Log.e(TAG, "Cannot load MediaController", e);
        }

    }

    private static class MediaControllerReleaser implements SingleObserver<MediaController> {

        @Override
        public void onSubscribe(@NonNull Disposable d) {}

        @Override
        public void onSuccess(@NonNull MediaController mediaController) {
            Log.d(TAG, "Releasing MediaController");
            mediaController.release();
        }

        @Override
        public void onError(@NonNull Throwable e) {
            Log.e(TAG, "Cannot release MediaController", e);
        }

    }

}
