package org.singularux.music.feature.playback.domain;

import android.util.Log;

import androidx.media3.session.MediaController;

import org.singularux.music.feature.playback.MusicControllerFacade;
import org.singularux.music.feature.playback.model.PlaybackPosition;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Scheduler;
import io.reactivex.rxjava3.functions.Function;
import io.reactivex.rxjava3.schedulers.Schedulers;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(onConstructor_ = {@Inject})
public class ListenPlaybackPositionUseCase {

    private static final String TAG = "ListenPlaybackPositionUseCase";

    private final MusicControllerFacade musicControllerFacade;

    public Flowable<PlaybackPosition> get() {
        // Must be watched on main thread because MediaController can be queried only there
        return Flowable.interval(250, TimeUnit.MILLISECONDS, Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .map(new PlaybackPositionMapper(musicControllerFacade));
    }

    @RequiredArgsConstructor
    private static class PlaybackPositionMapper implements Function<Long, PlaybackPosition> {

        private final MusicControllerFacade musicControllerFacade;

        @Override
        public PlaybackPosition apply(Long value) {
            MediaController maybeMediaController = musicControllerFacade.getMediaController();
            if (maybeMediaController != null) {
                Log.v(TAG, "MediaController is ok, reading position");
                double currentPositionMs = (double) Math
                        .clamp(maybeMediaController.getCurrentPosition(), 0, Long.MAX_VALUE);
                double totalDurationMs = (double) Math
                        .clamp(maybeMediaController.getContentDuration(), 0, Long.MAX_VALUE);
                return new PlaybackPosition((float) (currentPositionMs / totalDurationMs));
            } else {
                Log.v(TAG, "MediaController is null, setting progress to zero");
                return new PlaybackPosition(0.0F);
            }
        }

    }

}
