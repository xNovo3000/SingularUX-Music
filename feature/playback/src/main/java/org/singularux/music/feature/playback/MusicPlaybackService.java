package org.singularux.music.feature.playback;

import android.app.PendingIntent;
import android.content.Intent;

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
        getPackageManager().getLaunchIntentForPackage("org.singularux.music.MusicActivity");
        // Set the activity to open when clicking on the notification
        Intent intent = getPackageManager()
                .getLaunchIntentForPackage("org.singularux.music");
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
