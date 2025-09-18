package org.singularux.music.feature.playback.domain;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.media3.session.MediaController;

import org.singularux.music.core.playback.MusicControllerFacade;
import org.singularux.music.feature.playback.model.PlaybackPosition;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.functions.Function;
import io.reactivex.rxjava3.functions.Predicate;
import io.reactivex.rxjava3.schedulers.Schedulers;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(onConstructor_ = @Inject)
public class ListenPlaybackPositionUseCase {

    private static final String TAG = "ListenPlaybackPositionUseCase";

    private static final int INITIAL_DELAY_MS = 0;
    private static final int UPDATE_PERIOD_MS = 250;
    private static final PlaybackPosition EMPTY_PLAYBACK_POSITION =
            new PlaybackPosition(0.0F, Duration.ofMillis(0), Duration.ofMillis(1));

    private final MusicControllerFacade musicControllerFacade;

    public Flowable<PlaybackPosition> get() {
        // Must be watched on main thread because MediaController can be queried only there
        return Flowable.interval(INITIAL_DELAY_MS, UPDATE_PERIOD_MS, TimeUnit.MILLISECONDS,
                        Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .map(new PlaybackPositionMapper(musicControllerFacade))
                .filter(new PlaybackPositionFilter());
    }

    @RequiredArgsConstructor
    private static final class PlaybackPositionMapper implements Function<Long, PlaybackPosition> {

        private final MusicControllerFacade musicControllerFacade;

        @Override
        public @NonNull PlaybackPosition apply(@NonNull Long value) {
            // Send the current position when all this conditions are met:
            // 1. MediaController is present
            // 2. There is a current MediaItem
            // Send an empty playback position when one of this conditions are met:
            // 1. MediaController is not present
            // 2. There is not a current MediaItem
            MediaController mediaController = musicControllerFacade.getMediaController();
            if (mediaController != null && mediaController.getCurrentMediaItem() != null) {
                long currentPositionMs = mediaController.getCurrentPosition();
                long contentDurationMs = Math.max(1L, mediaController.getContentDuration());
                float progress = (float) ((double) currentPositionMs / (double) contentDurationMs);
                Duration currentPosition = Duration.ofMillis(currentPositionMs);
                Duration contentDuration = Duration.ofMillis(contentDurationMs);
                PlaybackPosition playbackPosition = new PlaybackPosition(progress, currentPosition, contentDuration);
                Log.v(TAG, "Updating PlaybackPosition with value " + playbackPosition);
                return playbackPosition;
            } else {
                Log.v(TAG, "Updating PlaybackPosition with empty one since there is no MediaItem");
                return EMPTY_PLAYBACK_POSITION;
            }
        }

    }

    @RequiredArgsConstructor
    private static final class PlaybackPositionFilter implements Predicate<PlaybackPosition> {

        @Override
        public boolean test(@NonNull PlaybackPosition playbackPosition) {
            float progress = playbackPosition.getProgress();
            return progress >= 0.0F && progress <= 1.0F;
        }

    }

}
