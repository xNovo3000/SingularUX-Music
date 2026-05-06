package org.singularux.music.feature.playback.domain;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.media3.session.MediaController;

import org.singularux.music.core.playback.MusicControllerFacade;
import org.singularux.music.feature.playback.data.PlayerAction;

import javax.inject.Inject;

public class OnPlayerActionUseCase {

    private static final String TAG = "OnPlaybackActionUseCase";

    private final MusicControllerFacade musicControllerFacade;

    @Inject
    public OnPlayerActionUseCase(MusicControllerFacade musicControllerFacade) {
        this.musicControllerFacade = musicControllerFacade;
    }

    public void run(@NonNull PlayerAction action) {
        Log.d(TAG, "Running action " + action);
        // Check if MediaController is present first
        if (musicControllerFacade.getMediaController() == null) {
            Log.d(TAG, "MediaController is not ready at the moment");
            return;
        }
        MediaController mediaController = musicControllerFacade.requireMediaController();
        // Run actions based on class type
        if (action instanceof PlayerAction.Play) {
            mediaController.play();
        } else if (action instanceof PlayerAction.Pause) {
            mediaController.pause();
        } else if (action instanceof PlayerAction.SkipPrev) {
            mediaController.seekToPrevious();
        } else if (action instanceof PlayerAction.SkipNext) {
            mediaController.seekToNext();
        } else if (action instanceof PlayerAction.SeekTo) {
            PlayerAction.SeekTo seekToAction = (PlayerAction.SeekTo) action;
            mediaController.seekTo(seekToAction.getDuration().toMillis());
        }
    }

}
