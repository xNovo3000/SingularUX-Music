package org.singularux.music.feature.playback.domain;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.media3.common.MediaItem;
import androidx.media3.common.MediaMetadata;
import androidx.media3.common.Player;
import androidx.media3.session.MediaController;

import org.singularux.music.feature.playback.MusicControllerFacade;
import org.singularux.music.feature.playback.model.PlaybackInfo;

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
import io.reactivex.rxjava3.functions.Cancellable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(onConstructor_ = {@Inject})
public class ListenPlaybackInfoUseCase {

    private static final String TAG = "ListenPlaybackInfoUseCase";

    private final MusicControllerFacade musicControllerFacade;

    public Flowable<Optional<PlaybackInfo>> get() {
        return Flowable.create(new PlaybackInfoSource(musicControllerFacade), BackpressureStrategy.LATEST)
                .subscribeOn(Schedulers.computation());
    }

    @RequiredArgsConstructor
    private static class PlaybackInfoSource implements FlowableOnSubscribe<Optional<PlaybackInfo>> {

        private final MusicControllerFacade musicControllerFacade;

        @Override
        public void subscribe(@NonNull FlowableEmitter<Optional<PlaybackInfo>> emitter) {
            // Only start emitting when MediaController is ready
            musicControllerFacade.getMediaControllerSingle()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new MediaControllerObserver(emitter));
        }

    }

    @RequiredArgsConstructor
    private static class MediaControllerObserver implements SingleObserver<MediaController> {

        private final FlowableEmitter<Optional<PlaybackInfo>> emitter;

        @Override
        public void onSubscribe(@NonNull Disposable d) {}

        @Override
        public void onSuccess(@NonNull MediaController mediaController) {
            Log.i(TAG, "Adding MediaController listener");
            PlaybackInfoListener listener = new PlaybackInfoListener(mediaController, emitter);
            // Add listener, force first update and remove when flowable is cancelled
            mediaController.addListener(listener);
            listener.update();
            emitter.setCancellable(new RemoveListenerCancellable(mediaController, listener));
        }

        @Override
        public void onError(@NonNull Throwable e) {
            Log.e(TAG, "Cannot retrieve MediaController", e);
            emitter.onError(e);
            emitter.onComplete();
        }

    }

    @RequiredArgsConstructor
    private static class RemoveListenerCancellable implements Cancellable {

        private final MediaController mediaController;
        private final PlaybackInfoListener listener;

        @Override
        public void cancel() {
            Log.i(TAG, "Removing MediaController listener");
            mediaController.removeListener(listener);
        }

    }

    @RequiredArgsConstructor
    private static class PlaybackInfoListener implements Player.Listener {

        private final MediaController mediaController;
        private final FlowableEmitter<Optional<PlaybackInfo>> emitter;

        @Override
        public void onIsPlayingChanged(boolean isPlaying) {
            update();
        }

        @Override
        public void onMediaMetadataChanged(@NonNull MediaMetadata mediaMetadata) {
            update();
        }

        public void update() {
            MediaItem mediaItem = mediaController.getCurrentMediaItem();
            if (mediaItem != null) {
                Log.d(TAG, "Updating PlaybackInfo with mediaItem " + mediaItem);
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
                // Artist ID and name
                Long artistId = null;
                if (mediaItem.mediaMetadata.extras != null &&
                        mediaItem.mediaMetadata.extras.containsKey("artist_id")) {
                    artistId = mediaItem.mediaMetadata.extras.getLong("artist_id");
                }
                String artistName = null;
                if (mediaItem.mediaMetadata.artist != null) {
                    artistName = mediaItem.mediaMetadata.artist.toString();
                }
                // Album ID and name
                Long albumId = null;
                if (mediaItem.mediaMetadata.extras != null &&
                        mediaItem.mediaMetadata.extras.containsKey("album_id")) {
                    albumId = mediaItem.mediaMetadata.extras.getLong("album_id");
                }
                String albumName = null;
                if (mediaItem.mediaMetadata.albumTitle != null) {
                    albumName = mediaItem.mediaMetadata.albumTitle.toString();
                }
                // Artwork
                Uri artworkUri = mediaItem.mediaMetadata.artworkUri;
                // State
                boolean isPlaying = mediaController.isPlaying();
                boolean hasPrevious = mediaController.hasPreviousMediaItem();
                boolean hasNext = mediaController.hasNextMediaItem();
                // Create PlaybackInfo object
                PlaybackInfo playbackInfo = PlaybackInfo.builder()
                        .id(id)
                        .title(title)
                        .artistId(artistId)
                        .artistName(artistName)
                        .albumId(albumId)
                        .albumName(albumName)
                        .artworkUri(artworkUri)
                        .isPlaying(isPlaying)
                        .hasPrevious(hasPrevious)
                        .hasNext(hasNext)
                        .build();
                // Push to the subscribers
                emitter.onNext(Optional.of(playbackInfo));
            } else {
                // Send empty value
                Log.d(TAG, "No media item present, returning empty optional");
                emitter.onNext(Optional.empty());
            }
        }

    }

}
