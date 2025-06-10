package org.singularux.music.feature.playback;

import android.content.ComponentName;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.media3.session.MediaController;
import androidx.media3.session.SessionToken;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

import io.reactivex.rxjava3.core.Single;
import lombok.Getter;

public class MusicControllerFacade {

    private static final String TAG = "MediaControllerFacade";

    private final Single<MediaController> mediaControllerSingle;
    private @Getter boolean isReady = false;

    public MusicControllerFacade(@NonNull Context context) {
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
                    isReady = true;
                }
                @Override
                public void onFailure(@NonNull Throwable t) {
                    emitter.onError(t);
                    Log.e(TAG, "Error loading MediaController", t);
                }
            }, ContextCompat.getMainExecutor(context));
        });
    }

}
