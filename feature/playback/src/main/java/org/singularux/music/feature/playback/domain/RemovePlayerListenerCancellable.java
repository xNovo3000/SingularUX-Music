package org.singularux.music.feature.playback.domain;

import android.util.Log;

import androidx.media3.common.Player;
import androidx.media3.session.MediaController;

import io.reactivex.rxjava3.functions.Cancellable;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
class RemovePlayerListenerCancellable implements Cancellable {

    private final String who;
    private final MediaController mediaController;
    private final Player.Listener listener;

    @Override
    public void cancel() {
        Log.d(who, "Removing listener " + listener);
        mediaController.removeListener(listener);
    }

}
