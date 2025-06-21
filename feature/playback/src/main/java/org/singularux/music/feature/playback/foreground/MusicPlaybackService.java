package org.singularux.music.feature.playback.foreground;

import android.app.PendingIntent;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.session.MediaSession;
import androidx.media3.session.MediaSessionService;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MusicPlaybackService extends MediaSessionService {

    private @Nullable MediaSession mediaSession = null;

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
    }

    @Override
    public void onTaskRemoved(@Nullable Intent rootIntent) {
        if (mediaSession != null) {
            mediaSession.getPlayer().setPlayWhenReady(false);
            mediaSession.getPlayer().release();
            mediaSession.release();
            mediaSession = null;
        }
        stopSelf();
    }

    @Override
    public void onDestroy() {
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
