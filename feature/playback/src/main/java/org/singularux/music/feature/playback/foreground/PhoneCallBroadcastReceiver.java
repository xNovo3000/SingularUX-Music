package org.singularux.music.feature.playback.foreground;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.media3.session.MediaSession;

import java.util.Objects;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PhoneCallBroadcastReceiver extends BroadcastReceiver {

    private static final String TAG = "PhoneCallBroadcastReceiver";

    private final MediaSession mediaSession;

    private boolean wasPlayingBeforeCallStarted = false;

    @Override
    public void onReceive(@NonNull Context context, @NonNull Intent intent) {
        // Check if action is actually PHONE_STATE
        if (!Objects.equals(intent.getAction(), TelephonyManager.ACTION_PHONE_STATE_CHANGED)) {
            Log.w(TAG, "Received non-filtered action: " + intent.getAction());
            return;
        }
        // Check if TelephonyManager.EXTRA_STATE is not empty
        String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
        if (state == null) {
            Log.w(TAG, "Received empty TelephonyManager.EXTRA_STATE");
            return;
        }
        // Act accordingly
        if (Objects.equals(state, TelephonyManager.EXTRA_STATE_RINGING) ||
                Objects.equals(state, TelephonyManager.EXTRA_STATE_OFFHOOK)) {
            // Phone is ringing, call is answered or outgoing call started
            // Register last state
            wasPlayingBeforeCallStarted = mediaSession.getPlayer().isPlaying();
            Log.d(TAG, "Phone calling or ringing, saving playback state: " + wasPlayingBeforeCallStarted);
            if (wasPlayingBeforeCallStarted) {
                mediaSession.getPlayer().pause();
            }
        } else if (Objects.equals(state, TelephonyManager.EXTRA_STATE_IDLE)) {
            // Call ended or idle
            // Restore last state
            Log.d(TAG, "Phone idle, restoring playback state: " + wasPlayingBeforeCallStarted);
            mediaSession.getPlayer().setPlayWhenReady(wasPlayingBeforeCallStarted);
        }
    }

}
