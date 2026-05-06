package org.singularux.music.feature.playback.domain;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.media3.common.MediaItem;
import androidx.media3.common.Player;
import androidx.media3.session.MediaController;

import org.singularux.music.core.playback.MusicControllerFacade;
import org.singularux.music.feature.playback.data.PlaybackItemInfo;

import java.time.Duration;
import java.util.Optional;
import java.util.function.Function;

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

    public @NonNull Flowable<Optional<PlaybackItemInfo>> get() {
        return Flowable.create(new OptionalPlaybackItemInfoSource(musicControllerFacade),
                        BackpressureStrategy.LATEST)
                .subscribeOn(Schedulers.computation(), false);
    }

    @RequiredArgsConstructor
    private static class OptionalPlaybackItemInfoSource
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
    private static final class MediaControllerObserver implements SingleObserver<MediaController> {

        private final FlowableEmitter<Optional<PlaybackItemInfo>> emitter;

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
        private final FlowableEmitter<Optional<PlaybackItemInfo>> emitter;

        @Override
        public void onMediaItemTransition(@Nullable MediaItem mediaItem, int reason) {
            update();
        }

        public void update() {
            MediaItem mediaItem = mediaController.getCurrentMediaItem();
            if (mediaItem != null) {
                Log.d(TAG, "Current MediaItem is not null, updating accordingly " + mediaItem);
                PlaybackItemInfoExtractor extractor = new PlaybackItemInfoExtractor();
                emitter.onNext(Optional.of(extractor.apply(mediaItem)));
            } else {
                Log.d(TAG, "Current MediaItem is null");
                emitter.onNext(Optional.empty());
            }
        }

    }

    private static final class PlaybackItemInfoExtractor
            implements Function<MediaItem, PlaybackItemInfo> {

        @Override
        public @NonNull PlaybackItemInfo apply(@NonNull MediaItem mediaItem) {
            // ID
            long id;
            try {
                id = Long.parseLong(mediaItem.mediaId);
            } catch (NumberFormatException e) {
                id = -1;
            }
            // Title
            String title = "";
            if (mediaItem.mediaMetadata.title != null) {
                title = mediaItem.mediaMetadata.title.toString();
            }
            // Artist ID
            Long artistId = null;
            if (mediaItem.mediaMetadata.extras != null &&
                    mediaItem.mediaMetadata.extras.containsKey("artist_id")) {
                artistId = mediaItem.mediaMetadata.extras.getLong("artist_id");
            }
            // Artist name
            String artistName = null;
            if (mediaItem.mediaMetadata.artist != null) {
                artistName = mediaItem.mediaMetadata.artist.toString();
            }
            // Album ID
            Long albumId = null;
            if (mediaItem.mediaMetadata.extras != null &&
                    mediaItem.mediaMetadata.extras.containsKey("album_id")) {
                albumId = mediaItem.mediaMetadata.extras.getLong("album_id");
            }
            // Album title
            String albumTitle = null;
            if (mediaItem.mediaMetadata.albumTitle != null) {
                albumTitle = mediaItem.mediaMetadata.albumTitle.toString();
            }
            // Duration
            Duration duration = Duration.ZERO;
            if (mediaItem.mediaMetadata.durationMs != null) {
                duration = Duration.ofMillis(mediaItem.mediaMetadata.durationMs);
            }
            // Playback token
            String playingFrom = null;
            if (mediaItem.mediaMetadata.extras != null &&
                    mediaItem.mediaMetadata.extras.containsKey("playing_from")) {
                playingFrom = mediaItem.mediaMetadata.extras.getString("playing_from");
            }
            // Artwork
            Uri artworkUri = mediaItem.mediaMetadata.artworkUri;
            // Create PlaybackItemInfo
            return new PlaybackItemInfo(id, title, artistId, artistName,
                    albumId, albumTitle, artworkUri, duration, playingFrom);
        }

    }

}
