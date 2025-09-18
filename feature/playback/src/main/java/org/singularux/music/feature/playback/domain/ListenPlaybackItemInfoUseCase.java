package org.singularux.music.feature.playback.domain;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.media3.common.MediaItem;
import androidx.media3.common.Player;
import androidx.media3.session.MediaController;

import org.singularux.music.core.playback.MusicControllerFacade;
import org.singularux.music.feature.playback.model.PlaybackItemInfo;

import java.util.Optional;

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

@RequiredArgsConstructor(onConstructor_ = @Inject)
public class ListenPlaybackItemInfoUseCase {

    private static final String TAG = "ListenPlaybackInfoUseCase";

    private final MusicControllerFacade musicControllerFacade;

    public Flowable<Optional<PlaybackItemInfo>> get() {
        return Flowable.create(new PlaybackInfoSource(musicControllerFacade),
                        BackpressureStrategy.LATEST)
                .subscribeOn(Schedulers.computation());
    }

    @RequiredArgsConstructor
    private static class PlaybackInfoSource
            implements FlowableOnSubscribe<Optional<PlaybackItemInfo>> {

        private final MusicControllerFacade musicControllerFacade;

        @Override
        public void subscribe(@NonNull FlowableEmitter<Optional<PlaybackItemInfo>> emitter) {
            // Only start emitting when MediaController is ready
            musicControllerFacade.getMediaControllerSingle()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new MediaControllerObserver(emitter));
        }

    }

    @RequiredArgsConstructor
    private static class MediaControllerObserver implements SingleObserver<MediaController> {

        private final FlowableEmitter<Optional<PlaybackItemInfo>> emitter;

        @Override
        public void onSubscribe(@NonNull Disposable d) {}

        @Override
        public void onSuccess(@NonNull MediaController mediaController) {
            Log.i(TAG, "Adding MediaController listener");
            PlaybackInfoListener listener = new PlaybackInfoListener(mediaController, emitter);
            // Add listener, force first update and remove when flowable is cancelled
            mediaController.addListener(listener);
            listener.update();
            emitter.setCancellable(new RemovePlayerListenerCancellable(
                    TAG, mediaController, listener));
        }

        @Override
        public void onError(@NonNull Throwable e) {
            Log.e(TAG, "Cannot retrieve MediaController", e);
            emitter.onError(e);
            emitter.onComplete();
        }

    }

    @RequiredArgsConstructor
    private static class PlaybackInfoListener implements Player.Listener {

        private final MediaController mediaController;
        private final FlowableEmitter<Optional<PlaybackItemInfo>> emitter;
        private final MediaItemToPlaybackItemInfoMapper mapper =
                new MediaItemToPlaybackItemInfoMapper();

        @Override
        public void onMediaItemTransition(@Nullable MediaItem mediaItem, int reason) {
            update();
        }

        public void update() {
            MediaItem mediaItem = mediaController.getCurrentMediaItem();
            if (mediaItem != null) {
                Log.d(TAG, "Current MediaItem is not null, updating accordingly " + mediaItem);
                emitter.onNext(Optional.of(mapper.apply(mediaItem)));
            } else {
                Log.d(TAG, "Current MediaItem is null");
                emitter.onNext(Optional.empty());
            }
        }

    }

}
