package org.singularux.music.feature.playback.domain;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.media3.common.MediaItem;
import androidx.media3.session.MediaController;

import org.singularux.music.core.playback.MusicControllerFacade;
import org.singularux.music.feature.playback.data.TimelineAction2;
import org.singularux.music.feature.playback.data.TrackDto;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;
import lombok.Value;

public class OnTimelineActionUseCase2 {

    private static final String TAG = "OnTimelineActionUseCase2";

    private final MusicControllerFacade musicControllerFacade;

    @Inject
    public OnTimelineActionUseCase2(MusicControllerFacade musicControllerFacade) {
        this.musicControllerFacade = musicControllerFacade;
    }

    public @NonNull Completable run(@NonNull TimelineAction2 action) {
        Log.d(TAG, "Running action " + action);
        if (action instanceof TimelineAction2.ReplaceMediaItems) {
            return run((TimelineAction2.ReplaceMediaItems) action);
        } else if (action instanceof TimelineAction2.AddToCustomQueue) {
            return run((TimelineAction2.AddToCustomQueue) action);
        }
        throw new IllegalArgumentException("Invalid action " + action);
    }

    private @NonNull Completable run(@NonNull TimelineAction2.ReplaceMediaItems action) {
        return musicControllerFacade.getMediaControllerSingle()
                // Extract data to inject into payload off-thread
                .observeOn(Schedulers.computation())
                .map(mediaController -> {
                    List<MediaItem> mediaItemList = action.getTrackDtoList().stream()
                            .map(new TrackDto.ToMediaItemMapper(false))
                            .collect(Collectors.toList());
                    int index = action.getIndex();
                    if (action.isShuffled()) {
                        MediaItem first = mediaItemList.remove(action.getIndex());
                        Collections.shuffle(mediaItemList);
                        mediaItemList.add(0, first);
                        index = 0;
                    }
                    return new ReplaceMediaItemsPayload(mediaController, mediaItemList, index);
                })
                // Inject into mediaController in the main thread and return
                .observeOn(AndroidSchedulers.mainThread())
                .flatMapCompletable(replaceMediaItemsPayload -> {
                    replaceMediaItemsPayload.getMediaController().setMediaItems(
                            replaceMediaItemsPayload.getMediaItemList(),
                            replaceMediaItemsPayload.getIndex(), 0);
                    replaceMediaItemsPayload.getMediaController().prepare();
                    return Completable.complete();
                });
    }

    private @NonNull Completable run(@NonNull TimelineAction2.AddToCustomQueue action) {
        // Extract index in which we will add the track
        Single<Integer> indexSingle = musicControllerFacade.getMediaControllerSingle()
                .observeOn(AndroidSchedulers.mainThread())
                .map(mediaController -> {
                    int currentIndex = mediaController.getCurrentMediaItemIndex() + 1;
                    int count = mediaController.getMediaItemCount();
                    while (currentIndex < count) {
                        MediaItem mediaItem = mediaController.getMediaItemAt(currentIndex);
                        if (mediaItem.mediaMetadata.extras != null &&
                                !mediaItem.mediaMetadata.extras.containsKey("custom_queue")) {
                            break;
                        }
                        currentIndex++;
                    }
                    return currentIndex;
                });
        // Extract MediaItem from TrackDto
        TrackDto.ToMediaItemMapper mapper = new TrackDto.ToMediaItemMapper(true);
        Single<MediaItem> mediaItemSingle = Single.just(action.getTrackDto())
                .observeOn(Schedulers.computation())
                .map(mapper::apply);
        // Combine them and apply into mediaController
        return Single.zip(musicControllerFacade.getMediaControllerSingle(),
                indexSingle, mediaItemSingle, (mediaController, index, mediaItem) -> {
                    return new AddToCustomQueuePayload(mediaController, mediaItem, index);
                })
                .flatMapCompletable(addToCustomQueuePayload -> {
                    addToCustomQueuePayload.getMediaController().addMediaItem(
                            addToCustomQueuePayload.getIndex(), addToCustomQueuePayload.getMediaItem());
                    return Completable.complete();
                });
    }

    @Value
    private static class ReplaceMediaItemsPayload {
        @NonNull MediaController mediaController;
        @NonNull List<MediaItem> mediaItemList;
        int index;
    }

    @Value
    private static class AddToCustomQueuePayload {
        @NonNull MediaController mediaController;
        @NonNull MediaItem mediaItem;
        int index;
    }

}
