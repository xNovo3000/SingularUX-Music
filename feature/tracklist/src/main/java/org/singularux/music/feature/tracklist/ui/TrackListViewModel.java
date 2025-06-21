package org.singularux.music.feature.tracklist.ui;

import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.LiveDataReactiveStreams;
import androidx.lifecycle.ViewModel;
import androidx.media3.common.MediaItem;
import androidx.media3.common.MediaMetadata;
import androidx.media3.session.MediaController;

import org.singularux.music.feature.playback.domain.model.PlaybackState;
import org.singularux.music.feature.playback.domain.usecase.ListenPlaybackStateUseCase;
import org.singularux.music.feature.playback.foreground.MusicControllerFacade;
import org.singularux.music.feature.playback.domain.usecase.ListenPlaybackItemInfoUseCase;
import org.singularux.music.feature.playback.domain.usecase.ListenPlaybackPositionUseCase;
import org.singularux.music.feature.playback.domain.model.PlaybackItemInfo;
import org.singularux.music.feature.playback.domain.model.PlaybackPosition;
import org.singularux.music.feature.tracklist.domain.usecase.ListenTrackListUseCase;
import org.singularux.music.feature.tracklist.domain.model.TrackItem;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import lombok.Getter;

@HiltViewModel
public class TrackListViewModel extends ViewModel {

    private final @Getter LiveData<List<TrackItem>> trackList;

    private final @Getter LiveData<PlaybackPosition> playbackPosition;
    private final @Getter LiveData<Optional<PlaybackItemInfo>> playbackItemInfo;
    private final @Getter LiveData<PlaybackState> playbackState;

    private final MusicControllerFacade musicControllerFacade;

    @Inject
    public TrackListViewModel(
            @NonNull ListenTrackListUseCase listenTrackListUseCase,
            @NonNull ListenPlaybackPositionUseCase listenPlaybackPositionUseCase,
            @NonNull ListenPlaybackItemInfoUseCase listenPlaybackItemInfoUseCase,
            @NonNull ListenPlaybackStateUseCase listenPlaybackStateUseCase,
            @NonNull MusicControllerFacade musicControllerFacade
    ) {
        this.trackList = LiveDataReactiveStreams.fromPublisher(listenTrackListUseCase.get());
        this.playbackPosition = LiveDataReactiveStreams
                .fromPublisher(listenPlaybackPositionUseCase.get());
        this.playbackItemInfo = LiveDataReactiveStreams
                .fromPublisher(listenPlaybackItemInfoUseCase.get());
        this.playbackState = LiveDataReactiveStreams
                .fromPublisher(listenPlaybackStateUseCase.get());
        this.musicControllerFacade = musicControllerFacade;
    }

    public void playFromSpecificTrackListIndex(int index) {
        // Get list of current tracks, and take from that index onwards,
        // map it to MediaItems and feed them in the MediaController
        List<TrackItem> currentList = trackList.getValue();
        if (currentList == null) {
            return;
        }
        List<MediaItem> mediaItems = currentList.stream()
                .skip(index)
                .map(new TrackItemToMediaItemMapper())
                .collect(Collectors.toList());
        MediaController mediaController = musicControllerFacade.requireMediaController();
        mediaController.clearMediaItems();
        mediaController.addMediaItems(mediaItems);
        mediaController.play();
    }

    public void play() {
        musicControllerFacade.requireMediaController().play();
    }

    public void pause() {
        musicControllerFacade.requireMediaController().pause();
    }

    public static class TrackItemToMediaItemMapper implements Function<TrackItem, MediaItem> {

        @Override
        public MediaItem apply(@NonNull TrackItem trackItem) {
            Uri uri = Uri.withAppendedPath(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    String.valueOf(trackItem.getId()));
            // Extras that does not fit the mediaMetadata
            Bundle extras = new Bundle();
            extras.putString("playback_id", "track_list/" + trackItem.getId());
            if (trackItem.getArtistId() != null) {
                extras.putLong("artist_id", trackItem.getArtistId());
            }
            if (trackItem.getAlbumId() != null) {
                extras.putLong("album_id", trackItem.getAlbumId());
            }
            // Basic mediaMetadata
            MediaMetadata mediaMetadata = new MediaMetadata.Builder()
                    .setTitle(trackItem.getTitle())
                    .setArtist(trackItem.getArtistName())
                    .setDurationMs(trackItem.getDuration().toMillis())
                    .setAlbumTitle(trackItem.getAlbumTitle())
                    .setArtworkUri(trackItem.getArtworkUri())
                    .setExtras(extras)
                    .build();
            // Full MediaItem
            return new MediaItem.Builder()
                    .setMediaId(String.valueOf(trackItem.getId()))
                    .setUri(uri)
                    .setMediaMetadata(mediaMetadata)
                    .build();
        }

    }

}
