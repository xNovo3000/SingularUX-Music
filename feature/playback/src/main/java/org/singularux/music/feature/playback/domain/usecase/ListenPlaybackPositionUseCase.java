package org.singularux.music.feature.playback.domain.usecase;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.media3.session.MediaController;

import org.singularux.music.feature.playback.MusicControllerFacade;
import org.singularux.music.feature.playback.domain.model.PlaybackPosition;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.functions.Function;
import io.reactivex.rxjava3.schedulers.Schedulers;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(onConstructor_ = {@Inject})
public class ListenPlaybackPositionUseCase {

    private static final String TAG = "ListenPlaybackPositionUseCase";

    private final MusicControllerFacade musicControllerFacade;

    public Flowable<PlaybackPosition> get() {
        // Must be watched on main thread because MediaController can be queried only there
        return Flowable.interval(0, 250, TimeUnit.MILLISECONDS,
                        Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .map(new PlaybackPositionMapper(musicControllerFacade));
    }

    @RequiredArgsConstructor
    private static class PlaybackPositionMapper implements Function<Long, PlaybackPosition> {

        private final MusicControllerFacade musicControllerFacade;

        @Override
        public @NonNull PlaybackPosition apply(@NonNull Long value) {
            MediaController maybeMediaController = musicControllerFacade.getMediaController();
            if (maybeMediaController != null) {
                Log.v(TAG, "MediaController is ok, reading position");
                long currentPositionMs = maybeMediaController.getCurrentPosition();
                long totalDurationMs = maybeMediaController.getContentDuration();
                float position = (float) (((double) currentPositionMs) / ((double) totalDurationMs));
                Duration current = Duration.ofMillis(currentPositionMs);
                return new PlaybackPosition(Math.clamp(position, 0.0F, 1.0F), current);
            } else {
                Log.v(TAG, "MediaController is null, setting progress to zero");
                return new PlaybackPosition(0.0F, Duration.ZERO);
            }
        }

    }

}
