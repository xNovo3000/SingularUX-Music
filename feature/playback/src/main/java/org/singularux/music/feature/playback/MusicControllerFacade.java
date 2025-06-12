package org.singularux.music.feature.playback;

import android.content.ComponentName;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.media3.common.Player;
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
import io.reactivex.rxjava3.schedulers.Schedulers;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

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
        this.mediaControllerSingle = Single.create(emitter -> {
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
        });
        // Get the controller when it's ready or the error if fails
        this.mediaControllerSingle.subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {}
                    @Override
                    public void onSuccess(@NonNull MediaController result) {
                        Log.d(TAG, "Loaded MediaController");
                        mediaController = result;
                    }
                    @Override
                    public void onError(@NonNull Throwable e) {
                        Log.e(TAG, "Failed to load MediaController", e);
                    }
                });
    }

    public @NonNull MediaController requireMediaController() {
        return Objects.requireNonNull(mediaController);
    }

    public boolean isReady() {
        return mediaController != null && mediaController.isConnected();
    }

}
