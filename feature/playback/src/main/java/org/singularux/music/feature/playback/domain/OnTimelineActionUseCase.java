package org.singularux.music.feature.playback.domain;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.media3.common.MediaItem;
import androidx.media3.common.MediaMetadata;
import androidx.media3.session.MediaController;

import org.singularux.music.core.playback.MusicControllerFacade;
import org.singularux.music.feature.playback.data.QueueItem;
import org.singularux.music.feature.playback.data.TimelineAction;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.inject.Inject;

import lombok.RequiredArgsConstructor;

public class OnTimelineActionUseCase {

    private static final String TAG = "OnTimelineActionUseCase";

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
        // TODO: Make this inside rx
        if (action instanceof TimelineAction.ReplaceMediaItems) {
            TimelineAction.ReplaceMediaItems replaceMediaItemsAction =
                    (TimelineAction.ReplaceMediaItems) action;
            if (replaceMediaItemsAction.getIndex() < 0 || replaceMediaItemsAction.getIndex() >
                    replaceMediaItemsAction.getQueueItemList().size()) {
                Log.e(TAG, "Index out of bounds, index: " + replaceMediaItemsAction.getIndex());
                return;
            }
            List<MediaItem> mediaItems = replaceMediaItemsAction.getQueueItemList().stream()
                    .map(new QueueItem.ToMediaItemMapper())
                    .collect(Collectors.toList());
            if (replaceMediaItemsAction.isShuffled()) {
                MediaItem first = mediaItems.remove(replaceMediaItemsAction.getIndex());
                Collections.shuffle(mediaItems);
                mediaItems.add(0, first);
                Log.d(TAG, "Adding new item list in shuffled fashion");
                mediaController.setMediaItems(mediaItems, 0, 0);
            } else {
                Log.d(TAG, "Adding new item list in unshuffled fashion");
                mediaController.setMediaItems(mediaItems,
                        replaceMediaItemsAction.getIndex(), 0);
            }
            mediaController.prepare();
        } else if (action instanceof TimelineAction.AddToCustomQueue) {
            TimelineAction.AddToCustomQueue addToCustomQueueAction =
                    (TimelineAction.AddToCustomQueue) action;
            // Get index of the first item without the extra "custom_queue" extra
            int index = mediaController.getCurrentMediaItemIndex() + 1;
            int count = mediaController.getMediaItemCount();
            while (index < count) {
                MediaItem mediaItem = mediaController.getMediaItemAt(index);
                if (mediaItem.mediaMetadata.extras != null &&
                        !mediaItem.mediaMetadata.extras.containsKey("custom_queue")) {
                    break;
                }
                index++;
            }
            // Extract MediaItem and add to the list
            QueueItem.ToMediaItemMapper mapper = new QueueItem.ToMediaItemMapper();
            MediaItem mediaItem = mapper.apply(addToCustomQueueAction.getMediaItem());
            Log.d(TAG, "Adding MediaItem to custom queue at index: " + index);
            mediaController.addMediaItem(index, mediaItem);
        }
    }

}
