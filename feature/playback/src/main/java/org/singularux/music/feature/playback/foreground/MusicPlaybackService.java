package org.singularux.music.feature.playback.foreground;

import android.app.PendingIntent;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.OptIn;
import androidx.core.content.ContextCompat;
import androidx.media3.common.Player;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.session.MediaSession;
import androidx.media3.session.MediaSessionService;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MusicPlaybackService extends MediaSessionService {

    public static final String INTENT_ORIGIN = "system_ui_notification";

    private @Nullable MediaSession mediaSession = null;
    private @Nullable PauseWhenCallingBroadcastReceiver pauseWhenCallingBroadcastReceiver = null;

    @Override
    public void onCreate() {
        super.onCreate();
        // Create pending intent that opens the application when clicking on the notification
        // Additionally, put an origin tag to tell the system that the intent came from
        // the notification and should go to the "Now Playing" page if not already there
        // TODO: We need to launch the player activity, not the library activity
        // TODO: because we are switching to basic multi-activity architecture
        Intent intent = getPackageManager().getLaunchIntentForPackage(getPackageName());
        assert intent != null;
        intent.putExtra("origin", INTENT_ORIGIN);
        PendingIntent openApplicationPendingIntent = PendingIntent.getActivity(this, 0,
                intent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
        // Create playback session
        Player player = new ExoPlayer.Builder(this).build();
        mediaSession = new MediaSession.Builder(this, player)
                .setSessionActivity(openApplicationPendingIntent)
                .build();
        // Listen for phone calls
        pauseWhenCallingBroadcastReceiver = new PauseWhenCallingBroadcastReceiver(mediaSession);
        ContextCompat.registerReceiver(this, pauseWhenCallingBroadcastReceiver,
                PauseWhenCallingBroadcastReceiver.INTENT_FILTER, ContextCompat.RECEIVER_EXPORTED);
    }

    @OptIn(markerClass = UnstableApi.class)
    @Override
    public void onTaskRemoved(@Nullable Intent rootIntent) {
        // Stop playback when user closes the app from task manager
        // Do not destroy the session, the activity will release the service
        pauseAllPlayersAndStopSelf();
    }

    @Override
    public void onDestroy() {
        // Stop listening for phone calls
        if (pauseWhenCallingBroadcastReceiver != null) {
            unregisterReceiver(pauseWhenCallingBroadcastReceiver);
            pauseWhenCallingBroadcastReceiver = null;
        }
        // User released the session, can destroy
        if (mediaSession != null) {
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
