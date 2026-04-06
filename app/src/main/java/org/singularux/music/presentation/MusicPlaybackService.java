package org.singularux.music.presentation;

import android.content.Intent;
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

}
