package org.singularux.music.feature.playback.domain;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.media3.common.MediaItem;
import androidx.media3.common.Player;
import androidx.media3.session.MediaController;

import org.singularux.music.core.playback.MusicControllerFacade;
import org.singularux.music.feature.playback.data.PlaybackItemInfo;
import org.singularux.music.feature.playback.data.QueueItem;

import javax.inject.Inject;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.BackpressureStrategy;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.FlowableEmitter;
import io.reactivex.rxjava3.core.FlowableOnSubscribe;
import io.reactivex.rxjava3.core.SingleObserver;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import lombok.RequiredArgsConstructor;

public class ListenPlaybackItemInfoUseCase {

    private static final String TAG = "ListenPlaybackInfoUseCase";

    private final MusicControllerFacade musicControllerFacade;

    @Inject
    public ListenPlaybackItemInfoUseCase(MusicControllerFacade musicControllerFacade) {
        this.musicControllerFacade = musicControllerFacade;
    }

    public @NonNull Flowable<PlaybackItemInfo> get() {
        return Flowable.create(new PlaybackItemInfoSource(musicControllerFacade),
                        BackpressureStrategy.LATEST)
                .subscribeOn(Schedulers.computation(), false);
    }

    @RequiredArgsConstructor
    private static class PlaybackItemInfoSource
            implements FlowableOnSubscribe<PlaybackItemInfo> {

        private final MusicControllerFacade musicControllerFacade;

        @Override
        public void subscribe(@NonNull FlowableEmitter<PlaybackItemInfo> emitter) {
            // Only start emitting when MediaController is ready
            musicControllerFacade.getMediaControllerSingle()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new MediaControllerObserver(emitter));
        }

    }

    @RequiredArgsConstructor
    private static final class MediaControllerObserver implements SingleObserver<MediaController> {

        private final FlowableEmitter<PlaybackItemInfo> emitter;

        @Override
        public void onSubscribe(@NonNull Disposable d) {}

        @Override
        public void onSuccess(@NonNull MediaController mediaController) {
            PlaybackInfoListener listener = new PlaybackInfoListener(mediaController, emitter);
            // Add listener, force first update and remove when flow is canceled
            mediaController.addListener(listener);
            listener.update();
            emitter.setCancellable(() -> mediaController.removeListener(listener));
        }

        @Override
        public void onError(@NonNull Throwable e) {
            Log.e(TAG, "Cannot retrieve MediaController", e);
            emitter.onError(e);
            emitter.onComplete();
        }

    }

    @RequiredArgsConstructor
    private static final class PlaybackInfoListener implements Player.Listener {

        private final MediaController mediaController;
        private final FlowableEmitter<PlaybackItemInfo> emitter;
        private final QueueItem.FromMediaItem fromMediaItem = new QueueItem.FromMediaItem();

        @Override
        public void onMediaItemTransition(@Nullable MediaItem mediaItem, int reason) {
            update();
        }

        public void update() {
            MediaItem mediaItem = mediaController.getCurrentMediaItem();
            if (mediaItem != null) {
                QueueItem queueItem = fromMediaItem.apply(mediaItem);
                Log.d(TAG, "Current MediaItem is not null, updating accordingly " + queueItem);
                emitter.onNext(new PlaybackItemInfo(queueItem));
            } else {
                Log.d(TAG, "Current MediaItem is null");
                emitter.onNext(new PlaybackItemInfo(null));
            }
        }

    }

}
