package org.singularux.music.feature.playback.domain;

import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.media3.common.MediaItem;
import androidx.media3.common.MediaMetadata;
import androidx.media3.session.MediaController;

import org.singularux.music.core.playback.MusicControllerFacade;
import org.singularux.music.feature.playback.data.TimelineAction;

import java.util.Collections;
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
            if (replaceMediaItemsAction.getIndex() < 0 || replaceMediaItemsAction.getIndex() >
                    replaceMediaItemsAction.getMediaItemList().size()) {
                Log.e(TAG, "Index out of bounds, index: " + replaceMediaItemsAction.getIndex());
                return;
            }
            List<MediaItem> mediaItems = replaceMediaItemsAction.getMediaItemList().stream()
                    .map(new MediaItemExtractor())
                    .collect(Collectors.toList());
            if (replaceMediaItemsAction.isShuffled()) {
                MediaItem first = mediaItems.remove(replaceMediaItemsAction.getIndex());
                Collections.shuffle(mediaItems);
                mediaItems.add(0, first);
                mediaController.setMediaItems(mediaItems, 0, 0);
            } else {
                mediaController.setMediaItems(mediaItems,
                        replaceMediaItemsAction.getIndex(), 0);
            }
            mediaController.prepare();
            mediaController.play();
        }
    }

    private static final class MediaItemExtractor
            implements Function<TimelineAction.MediaItem, MediaItem> {

        @Override
        public @NonNull MediaItem apply(@NonNull TimelineAction.MediaItem mediaItem) {
            Bundle extras = new Bundle();
            if (mediaItem.getArtistId() != null) {
                extras.putLong("artist_id", mediaItem.getArtistId());
            }
            if (mediaItem.getAlbumId() != null) {
                extras.putLong("album_id", mediaItem.getAlbumId());
            }
            if (mediaItem.getPlayingFrom() != null) {
                extras.putString("playing_from", mediaItem.getPlayingFrom());
            }
            MediaMetadata mediaMetadata = new MediaMetadata.Builder()
                    .setTitle(mediaItem.getTitle())
                    .setAlbumArtist(mediaItem.getAlbumTitle())
                    .setArtist(mediaItem.getArtistName())
                    .setArtworkUri(mediaItem.getArtworkPath())
                    .setDurationMs(mediaItem.getDuration().toMillis())
                    .setExtras(extras)
                    .build();
            return new MediaItem.Builder()
                    .setMediaId(String.valueOf(mediaItem.getId()))
                    .setUri(getTrackUriFromId(mediaItem.getId()))
                    .setMediaMetadata(mediaMetadata)
                    .build();
        }

        private static Uri getTrackUriFromId(long trackId) {
            return Uri.withAppendedPath(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    String.valueOf(trackId));
        }

    }

}
