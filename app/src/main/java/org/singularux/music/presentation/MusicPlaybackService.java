package org.singularux.music.presentation;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.telephony.TelephonyManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.OptIn;
import androidx.core.content.ContextCompat;
import androidx.media3.common.Player;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.session.MediaSession;
import androidx.media3.session.MediaSessionService;

import java.util.Objects;

import dagger.hilt.android.AndroidEntryPoint;
import lombok.RequiredArgsConstructor;

@AndroidEntryPoint
public class MusicPlaybackService extends MediaSessionService {

    private static final String TAG = "MusicPlaybackService";

    private @Nullable MediaSession mediaSession = null;
    private @Nullable PauseWhenCallingBroadcastReceiver pauseWhenCallingBroadcastReceiver = null;

    @Override
    public void onCreate() {
        super.onCreate();
        // Create session
        Log.d(TAG, "Creating session");
        Player player = new ExoPlayer.Builder(this).build();
        mediaSession = new MediaSession.Builder(this, player).build();
        // Register a receiver that pauses when calling and restores playback when finishes
        pauseWhenCallingBroadcastReceiver = new PauseWhenCallingBroadcastReceiver(mediaSession);
        ContextCompat.registerReceiver(this, pauseWhenCallingBroadcastReceiver,
                PauseWhenCallingBroadcastReceiver.INTENT_FILTER, ContextCompat.RECEIVER_EXPORTED);
    }

    @OptIn(markerClass = UnstableApi.class)
    @Override
    public void onTaskRemoved(@Nullable Intent rootIntent) {
        pauseAllPlayersAndStopSelf();
    }

    @Override
    public void onDestroy() {
        // Unregister the phone call listener
        if (pauseWhenCallingBroadcastReceiver != null) {
            unregisterReceiver(pauseWhenCallingBroadcastReceiver);
            pauseWhenCallingBroadcastReceiver = null;
        }
        // Destroy session
        if (mediaSession != null) {
            Log.d(TAG, "Destroying session");
            mediaSession.getPlayer().release();
            mediaSession.release();
            mediaSession = null;
        }
        super.onDestroy();
    }

    @Override
    public @Nullable MediaSession onGetSession(
            @NonNull MediaSession.ControllerInfo controllerInfo) {
        return mediaSession;
    }

    @RequiredArgsConstructor
    private static class PauseWhenCallingBroadcastReceiver extends BroadcastReceiver {

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

}
