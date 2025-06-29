package org.singularux.music.feature.playback.foreground;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.telephony.TelephonyManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.session.MediaSession;
import androidx.media3.session.MediaSessionService;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MusicPlaybackService extends MediaSessionService {

    private @Nullable MediaSession mediaSession = null;
    private @Nullable PhoneCallBroadcastReceiver phoneCallBroadcastReceiver = null;

    @Override
    public void onCreate() {
        super.onCreate();
        // Set the activity to open when clicking on the notification
        Intent intent = getPackageManager().getLaunchIntentForPackage(getPackageName());
        assert intent != null;  // Will never be null
        intent.putExtra("origin", "system_ui_notification");
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
        // Create the player
        ExoPlayer player = new ExoPlayer.Builder(this).build();
        mediaSession = new MediaSession.Builder(this, player)
                .setSessionActivity(pendingIntent)
                .build();
        // Listen for calls, pause playback if a call is received and resume after that
        IntentFilter phoneCallIntentFilter =
                new IntentFilter(TelephonyManager.ACTION_PHONE_STATE_CHANGED);
        phoneCallBroadcastReceiver = new PhoneCallBroadcastReceiver(mediaSession);
        ContextCompat.registerReceiver(this, phoneCallBroadcastReceiver,
                phoneCallIntentFilter, ContextCompat.RECEIVER_EXPORTED);
        // TODO: Restore current timeline
    }

    @Override
    public void onTaskRemoved(@Nullable Intent rootIntent) {
        // TODO: Save current timeline
        // Stop playing when user dismisses application
        if (mediaSession != null) {
            mediaSession.getPlayer().setPlayWhenReady(false);
        }
        stopSelf();
    }

    @Override
    public void onDestroy() {
        // Stop listening for calls
        if (phoneCallBroadcastReceiver != null) {
            unregisterReceiver(phoneCallBroadcastReceiver);
        }
        // Destroy session
        if (mediaSession != null) {
            mediaSession.getPlayer().release();
            mediaSession.release();
            mediaSession = null;
        }
        super.onDestroy();
    }

    @Nullable
    @Override
    public MediaSession onGetSession(@NonNull MediaSession.ControllerInfo controllerInfo) {
        return mediaSession;
    }

}
