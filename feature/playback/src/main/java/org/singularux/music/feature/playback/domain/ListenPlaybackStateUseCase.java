package org.singularux.music.feature.playback.domain;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.media3.common.MediaItem;
import androidx.media3.common.Player;
import androidx.media3.session.MediaController;

import org.singularux.music.feature.playback.foreground.MusicControllerFacade;
import org.singularux.music.feature.playback.model.PlaybackState;

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
public class ListenPlaybackStateUseCase {

    private static final String TAG = "ListenPlaybackStateUseCase";

    private final MusicControllerFacade musicControllerFacade;

    public Flowable<PlaybackState> get() {
        return Flowable.create(new PlaybackStateSource(musicControllerFacade),
                        BackpressureStrategy.LATEST)
                .subscribeOn(Schedulers.computation());
    }

    @RequiredArgsConstructor
    private static class PlaybackStateSource implements FlowableOnSubscribe<PlaybackState> {

        private final MusicControllerFacade musicControllerFacade;

        @Override
        public void subscribe(@NonNull FlowableEmitter<PlaybackState> emitter) {
            // Only start emitting when MediaController is ready
            musicControllerFacade.getMediaControllerSingle()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new MediaControllerObserver(emitter));
        }

    }

    @RequiredArgsConstructor
    private static class MediaControllerObserver implements SingleObserver<MediaController> {

        private final FlowableEmitter<PlaybackState> emitter;

        @Override
        public void onSubscribe(@NonNull Disposable d) {}

        @Override
        public void onSuccess(@NonNull MediaController mediaController) {
            Log.i(TAG, "Adding MediaController listener");
            PlaybackStateListener listener = new PlaybackStateListener(mediaController, emitter);
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
    private static class PlaybackStateListener implements Player.Listener {

        private final MediaController mediaController;
        private final FlowableEmitter<PlaybackState> emitter;

        @Override
        public void onPlaybackStateChanged(int playbackState) {
            update();
        }

        @Override
        public void onIsPlayingChanged(boolean isPlaying) {
            update();
        }

        @Override
        public void onMediaItemTransition(@Nullable MediaItem mediaItem, int reason) {
            update();
        }

        public void update() {
            boolean isReady = mediaController.getCurrentMediaItem() != null;
            boolean isPlaying = mediaController.isPlaying();
            boolean hasNextItem = mediaController.hasNextMediaItem();
            PlaybackState playbackState = new PlaybackState(isReady, isPlaying, hasNextItem);
            Log.d(TAG, "Updating with values " + playbackState);
            emitter.onNext(playbackState);
        }

    }

}
