package org.singularux.music.presentation;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.telephony.TelephonyManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.media3.session.MediaSession;

import java.util.Objects;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PauseWhenCallingBroadcastReceiver extends BroadcastReceiver {

    private static final String TAG = "PauseWhenCallingBroadcastReceiver";

    public static final IntentFilter INTENT_FILTER =
            new IntentFilter(TelephonyManager.ACTION_PHONE_STATE_CHANGED);

    private final MediaSession mediaSession;

    private boolean wasPlayingBeforeCallStarted = false;

    @Override
    public void onReceive(@NonNull Context context, @NonNull Intent intent) {
        // Check if action is ACTION_PHONE_STATE_CHANGED
        if (!Objects.equals(intent.getAction(), TelephonyManager.ACTION_PHONE_STATE_CHANGED)) {
            Log.w(TAG, "Received non-filtered action: " + intent.getAction());
            return;
        }
        // Check if contains state
        String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
        if (state == null) {
            Log.w(TAG, "Received empty TelephonyManager.EXTRA_STATE");
            return;
        }
        // Save when ringing and restore when idle
        if (state.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
            Log.d(TAG, "Phone is ringing, saving current playback state");
            wasPlayingBeforeCallStarted = mediaSession.getPlayer().isPlaying();
            if (wasPlayingBeforeCallStarted) {
                mediaSession.getPlayer().pause();
            }
        } else if (state.equals(TelephonyManager.EXTRA_STATE_IDLE)) {
            Log.d(TAG, "Phone is idle, restoring playback state");
            mediaSession.getPlayer().setPlayWhenReady(wasPlayingBeforeCallStarted);
        }
    }

}
