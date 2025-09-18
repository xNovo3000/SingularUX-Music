package org.singularux.music.feature.playback.foreground;

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
import io.reactivex.rxjava3.core.SingleEmitter;
import io.reactivex.rxjava3.core.SingleObserver;
import io.reactivex.rxjava3.core.SingleOnSubscribe;
import io.reactivex.rxjava3.disposables.Disposable;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
public class MusicControllerFacade {

    private static final String TAG = "MusicControllerFacade";

    private final Single<MediaController> mediaControllerSingle;
    private @Nullable MediaController mediaController = null;

    public MusicControllerFacade(@NonNull Context context) {
        // Try to load MediaController
        ComponentName componentName = new ComponentName(context, MusicPlaybackService.class);
        SessionToken sessionToken = new SessionToken(context, componentName);
        ListenableFuture<MediaController> mediaControllerFuture = new MediaController
                .Builder(context, sessionToken)
                .buildAsync();
        // Create MediaController single
        this.mediaControllerSingle = Single
                .create(new MediaControllerEmitter(context, mediaControllerFuture));
        // Listen for MediaController instance
        this.mediaControllerSingle.observeOn(AndroidSchedulers.mainThread())
                .subscribe(new MediaControllerObserver());
    }

    public void release() {
        this.mediaControllerSingle.observeOn(AndroidSchedulers.mainThread())
                .subscribe(new MediaControllerReleaser());
    }

    public @NonNull MediaController requireMediaController() {
        return Objects.requireNonNull(mediaController);
    }

    public boolean isReady() {
        return mediaController != null && mediaController.isConnected();
    }

    @RequiredArgsConstructor
    private static final class MediaControllerEmitter
            implements SingleOnSubscribe<MediaController> {

        private final Context context;
        private final ListenableFuture<MediaController> mediaControllerFuture;

        @Override
        public void subscribe(@NonNull SingleEmitter<MediaController> emitter) {
            Futures.addCallback(mediaControllerFuture, new FutureCallback<>() {
                @Override
                public void onSuccess(MediaController result) {
                    emitter.onSuccess(result);
                }
                @Override
                public void onFailure(@NonNull Throwable t) {
                    emitter.onError(t);
                }
            }, ContextCompat.getMainExecutor(context));
        }

    }

    private final class MediaControllerObserver implements SingleObserver<MediaController> {

        @Override
        public void onSubscribe(@NonNull Disposable d) {}

        @Override
        public void onSuccess(@NonNull MediaController result) {
            Log.i(TAG, "Loaded MediaController");
            mediaController = result;
        }

        @Override
        public void onError(@NonNull Throwable e) {
            Log.e(TAG, "Failed to load MediaController", e);
        }

    }

    private static final class MediaControllerReleaser
            implements SingleObserver<MediaController> {

        @Override
        public void onSubscribe(@NonNull Disposable d) {}

        @Override
        public void onSuccess(@NonNull MediaController result) {
            Log.i(TAG, "Releasing MediaController");
            result.release();
        }

        @Override
        public void onError(@NonNull Throwable e) {
            Log.e(TAG, "Failed to load MediaController", e);
        }

    }

}