package org.singularux.music.feature.playback.domain.usecase;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.media3.session.MediaController;

import org.singularux.music.feature.playback.foreground.MusicControllerFacade;
import org.singularux.music.feature.playback.domain.model.PlaybackPosition;

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

    private static final int UPDATE_PERIOD_MS = 500;
    private static final PlaybackPosition INVALID_PLAYBACK_POSITION =
            new PlaybackPosition(-1.0F, Duration.ofMillis(0));
    private static final PlaybackPosition EMPTY_PLAYBACK_POSITION =
            new PlaybackPosition(0.0F, Duration.ofMillis(1));

    private final MusicControllerFacade musicControllerFacade;

    public Flowable<PlaybackPosition> get() {
        // Must be watched on main thread because MediaController can be queried only there
        return Flowable.interval(0, UPDATE_PERIOD_MS, TimeUnit.MILLISECONDS,
                        Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .map(new PlaybackPositionMapper(musicControllerFacade))
                .filter(new PlaybackPositionInvalidFilter());
    }

    @RequiredArgsConstructor
    private static final class PlaybackPositionMapper implements Function<Long, PlaybackPosition> {

        private final MusicControllerFacade musicControllerFacade;

        @Override
        public @NonNull PlaybackPosition apply(@NonNull Long value) {
            // Send the current position when all this conditions are met:
            // 1. MediaController is present
            // 2. There is a current MediaItem
            // 3. MediaController is READY
            // Send an invalid playback position when all this conditions are met:
            // 1. MediaController is present
            // 2. There is a current MediaItem
            // 3. MediaController is not READY
            // Send an empty playback position when one of this conditions are met:
            // 1. MediaController is not present
            // 2. There is not a current MediaItem
            MediaController mediaController = musicControllerFacade.getMediaController();
            if (mediaController != null && mediaController.getCurrentMediaItem() != null) {
                if (mediaController.getPlayWhenReady()) {
                    double currentPositionMs = mediaController.getCurrentPosition();
                    long contentDurationMs = Math.max(1L, mediaController.getContentDuration());
                    float progress = (float) (currentPositionMs / contentDurationMs);
                    Duration contentDuration = Duration.ofMillis(contentDurationMs);
                    return new PlaybackPosition(progress, contentDuration);
                } else {
                    Log.v(TAG, "Updating PlaybackPosition with invalid one since MediaController is null");
                    return INVALID_PLAYBACK_POSITION;
                }
            } else {
                Log.v(TAG, "Updating PlaybackPosition with empty one since MediaController is null");
                return EMPTY_PLAYBACK_POSITION;
            }
        }

    }

    @RequiredArgsConstructor
    private static final class PlaybackPositionInvalidFilter implements Predicate<PlaybackPosition> {

        @Override
        public boolean test(@NonNull PlaybackPosition playbackPosition) {
            float currentPosition = playbackPosition.getCurrentPosition();
            return currentPosition >= 0.0F && currentPosition <= 1.0F;
        }

    }

}
