package org.singularux.music.feature.playback.domain;

import android.util.Log;

import androidx.annotation.Nullable;
import androidx.media3.session.MediaController;

import org.singularux.music.core.playback.MusicControllerFacade;
import org.singularux.music.feature.playback.data.PlaybackPosition;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.functions.Function;
import io.reactivex.rxjava3.schedulers.Schedulers;
import lombok.RequiredArgsConstructor;

public class ListenPlaybackPositionUseCase {

    private static final String TAG = "ListenPlaybackPositionUseCase";

    private static final int INITIAL_DELAY_MS = 0;
    private static final int PERIOD_MS = 250;

    private final MusicControllerFacade musicControllerFacade;

    @Inject
    public ListenPlaybackPositionUseCase(MusicControllerFacade musicControllerFacade) {
        this.musicControllerFacade = musicControllerFacade;
    }

    public Flowable<PlaybackPosition> get() {
        return Flowable.interval(INITIAL_DELAY_MS, PERIOD_MS, TimeUnit.MILLISECONDS,
                        Schedulers.computation())
                .distinctUntilChanged()
                .observeOn(AndroidSchedulers.mainThread())
                .map(new PlaybackPositionRetriever(musicControllerFacade));
    }

    @RequiredArgsConstructor
    private static class PlaybackPositionRetriever implements Function<Long, PlaybackPosition> {

        private final MusicControllerFacade musicControllerFacade;

        @Override
        public @Nullable PlaybackPosition apply(Long index) {
            MediaController mediaController = musicControllerFacade.getMediaController();
            // Check if controller and a media item are present at the moment
            if (mediaController == null || mediaController.getCurrentMediaItem() == null) {
                Log.v(TAG, "MediaController not ready or no media item present");
                return new PlaybackPosition(0.0F, Duration.ZERO);
            }
            // Extract position and total duration
            long positionMs = mediaController.getCurrentPosition();
            long durationMs = mediaController.getContentDuration();
            // Sanitize
            positionMs = Math.max(positionMs, 0L);
            durationMs = Math.max(durationMs, 1L);
            if (positionMs > durationMs) {
                Log.v(TAG, "positionMs is greater than durationMs, sending zero value");
                return new PlaybackPosition(0.0F, Duration.ZERO);
            }
            // Calculate final values
            float progress = (float) ((double) positionMs / (double) durationMs);
            Duration position = Duration.ofMillis(positionMs);
            PlaybackPosition playbackPosition = new PlaybackPosition(progress, position);
            Log.v(TAG, "Sending " + playbackPosition);
            return playbackPosition;
        }

    }

}
