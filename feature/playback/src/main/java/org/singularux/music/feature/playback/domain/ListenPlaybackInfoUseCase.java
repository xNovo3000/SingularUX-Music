package org.singularux.music.feature.playback.domain;

import androidx.annotation.Nullable;

import org.singularux.music.feature.playback.MusicControllerFacade;
import org.singularux.music.feature.playback.model.PlaybackInfo;

import javax.inject.Inject;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.BackpressureStrategy;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.FlowableEmitter;
import io.reactivex.rxjava3.core.FlowableOnSubscribe;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(onConstructor_ = {@Inject})
public class ListenPlaybackInfoUseCase {

    private final MusicControllerFacade musicControllerFacade;

    public Flowable<PlaybackInfo> get() {
        return Flowable.create(new PlaybackPositionSource(musicControllerFacade), BackpressureStrategy.LATEST)
                .subscribeOn(Schedulers.computation());
    }

    @RequiredArgsConstructor
    private static class PlaybackPositionSource implements FlowableOnSubscribe<PlaybackInfo> {

        private final MusicControllerFacade musicControllerFacade;

        @Override
        public void subscribe(@NonNull FlowableEmitter<PlaybackInfo> emitter) {

        }

    }

}
