package org.singularux.music.feature.tracklist.viewmodel;

import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.LiveDataReactiveStreams;
import androidx.lifecycle.ViewModel;
import androidx.media3.common.MediaItem;
import androidx.media3.common.MediaMetadata;
import androidx.media3.session.MediaController;

import org.singularux.music.feature.playback.MusicControllerFacade;
import org.singularux.music.feature.playback.domain.ListenPlaybackInfoUseCase;
import org.singularux.music.feature.playback.domain.ListenPlaybackPositionUseCase;
import org.singularux.music.feature.playback.model.PlaybackInfo;
import org.singularux.music.feature.playback.model.PlaybackPosition;
import org.singularux.music.feature.tracklist.domain.ListenTrackListUseCase;
import org.singularux.music.feature.tracklist.model.TrackItem;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import lombok.Getter;

@HiltViewModel
@Getter
public class TrackListViewModel extends ViewModel {

    private static final String TAG = "TrackListViewModel";

    private final LiveData<List<TrackItem>> trackList;
    private final LiveData<PlaybackPosition> playbackPosition;
    private final LiveData<Optional<PlaybackInfo>> playbackInfo;

    private final MusicControllerFacade musicControllerFacade;

    @Inject
    public TrackListViewModel(
            @NonNull ListenTrackListUseCase listenTrackListUseCase,
            @NonNull ListenPlaybackPositionUseCase listenPlaybackPositionUseCase,
            @NonNull ListenPlaybackInfoUseCase listenPlaybackInfoUseCase,
            @NonNull MusicControllerFacade musicControllerFacade
    ) {
        this.trackList = LiveDataReactiveStreams.fromPublisher(listenTrackListUseCase.get());
        this.playbackPosition = LiveDataReactiveStreams
                .fromPublisher(listenPlaybackPositionUseCase.get());
        this.playbackInfo = LiveDataReactiveStreams.fromPublisher(listenPlaybackInfoUseCase.get());
        this.musicControllerFacade = musicControllerFacade;
    }

    public void playFromSpecificTrack(TrackItem trackItem) {
        Log.d(TAG, "Playing trackId " + trackItem.getId());
        // Create URI from item and feed into player
        Uri uri = Uri.withAppendedPath(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                String.valueOf(trackItem.getId()));
        MediaMetadata mediaMetadata = new MediaMetadata.Builder()
                .setTitle(trackItem.getTitle())
                .setArtist(trackItem.getArtistsName())
                .setArtworkUri(trackItem.getArtworkUri())
                .build();
        MediaItem mediaItem = new MediaItem.Builder()
                .setUri(uri)
                .setMediaMetadata(mediaMetadata)
                .build();
        MediaController mediaController = musicControllerFacade.getMediaController();
        if (mediaController != null) {
            mediaController.clearMediaItems();
            mediaController.addMediaItem(mediaItem);
            mediaController.play();
        }
    }

    public void playFromSpecificTrackListIndex(int index) {
        List<TrackItem> currentList = trackList.getValue();
        if (currentList == null) {
            return;
        }
        List<MediaItem> mediaItems = currentList.stream()
                .skip(index)
                .map(trackItem -> {
                    Uri uri = Uri.withAppendedPath(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                            String.valueOf(trackItem.getId()));
                    MediaMetadata mediaMetadata = new MediaMetadata.Builder()
                            .setTitle(trackItem.getTitle())
                            .setArtist(trackItem.getArtistsName())
                            .setArtworkUri(trackItem.getArtworkUri())
                            .build();
                    return new MediaItem.Builder()
                            .setUri(uri)
                            .setMediaMetadata(mediaMetadata)
                            .build();
                })
                .collect(Collectors.toList());
        MediaController mediaController = musicControllerFacade.getMediaController();
        if (mediaController != null) {
            mediaController.clearMediaItems();
            mediaController.addMediaItems(mediaItems);
            mediaController.play();
        }
    }

    public void play() {
        MediaController mediaController = musicControllerFacade.getMediaController();
        if (mediaController != null) {
            mediaController.play();
        }
    }

    public void pause() {
        MediaController mediaController = musicControllerFacade.getMediaController();
        if (mediaController != null) {
            mediaController.pause();
        }
    }

}
