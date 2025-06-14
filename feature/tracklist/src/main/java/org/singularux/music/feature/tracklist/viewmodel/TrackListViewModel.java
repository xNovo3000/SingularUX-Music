package org.singularux.music.feature.tracklist.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.LiveDataReactiveStreams;
import androidx.lifecycle.ViewModel;
import androidx.media3.common.MediaItem;
import androidx.media3.session.MediaController;

import org.singularux.music.feature.playback.MusicControllerFacade;
import org.singularux.music.feature.playback.domain.ListenPlaybackInfoUseCase;
import org.singularux.music.feature.playback.domain.ListenPlaybackPositionUseCase;
import org.singularux.music.feature.playback.model.PlaybackInfo;
import org.singularux.music.feature.playback.model.PlaybackPosition;
import org.singularux.music.feature.tracklist.domain.ListenTrackListUseCase;
import org.singularux.music.feature.tracklist.model.TrackItem;
import org.singularux.music.feature.tracklist.model.TrackItemToMediaItemMapper;

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

    private final TrackItemToMediaItemMapper trackItemToMediaItemMapper;

    @Inject
    public TrackListViewModel(
            @NonNull ListenTrackListUseCase listenTrackListUseCase,
            @NonNull ListenPlaybackPositionUseCase listenPlaybackPositionUseCase,
            @NonNull ListenPlaybackInfoUseCase listenPlaybackInfoUseCase,
            @NonNull MusicControllerFacade musicControllerFacade,
            @NonNull TrackItemToMediaItemMapper trackItemToMediaItemMapper
    ) {
        this.trackList = LiveDataReactiveStreams.fromPublisher(listenTrackListUseCase.get());
        this.playbackPosition = LiveDataReactiveStreams
                .fromPublisher(listenPlaybackPositionUseCase.get());
        this.playbackInfo = LiveDataReactiveStreams.fromPublisher(listenPlaybackInfoUseCase.get());
        this.musicControllerFacade = musicControllerFacade;
        this.trackItemToMediaItemMapper = trackItemToMediaItemMapper;
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
                .map(trackItemToMediaItemMapper)
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

}
