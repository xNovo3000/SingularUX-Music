package org.singularux.music.feature.playback.domain;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.media3.common.MediaItem;
import androidx.media3.session.MediaController;

import org.singularux.music.core.playback.MusicControllerFacade;
import org.singularux.music.feature.playback.data.TimelineAction;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.inject.Inject;

public class OnTimelineActionUseCase {

    private static final String TAG = "OnPlaybackActionUseCase";

    private final MusicControllerFacade musicControllerFacade;

    @Inject
    public OnTimelineActionUseCase(MusicControllerFacade musicControllerFacade) {
        this.musicControllerFacade = musicControllerFacade;
    }

    public void run(@NonNull TimelineAction action) {
        Log.d(TAG, "Running action " + action);
        // Check if MediaController is present first
        if (musicControllerFacade.getMediaController() == null) {
            Log.d(TAG, "MediaController is not ready at the moment");
            return;
        }
        MediaController mediaController = musicControllerFacade.requireMediaController();
        // Run actions based on class type
        if (action instanceof TimelineAction.ReplaceMediaItems) {
            // TODO: Make this inside rx
            TimelineAction.ReplaceMediaItems replaceMediaItemsAction =
                    (TimelineAction.ReplaceMediaItems) action;
            List<MediaItem> mediaItems = replaceMediaItemsAction.getMediaItemList().stream()
                    .map(new MediaItemExtractor())
                    .collect(Collectors.toList());
            mediaController.setMediaItems(mediaItems,
                    replaceMediaItemsAction.getIndex(), 0);
        }
    }

    private static final class MediaItemExtractor
            implements Function<TimelineAction.MediaItem, MediaItem> {

        @Override
        public @NonNull MediaItem apply(@NonNull TimelineAction.MediaItem mediaItem) {
            return null;
        }

    }

}
