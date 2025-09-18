package org.singularux.music.feature.nowplaying.ui;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.LiveDataReactiveStreams;
import androidx.lifecycle.ViewModel;
import androidx.media3.session.MediaController;

import org.singularux.music.feature.playback.foreground.MusicControllerFacade;
import org.singularux.music.feature.playback.model.PlaybackState;
import org.singularux.music.feature.playback.domain.ListenPlaybackItemInfoUseCase;
import org.singularux.music.feature.playback.domain.ListenPlaybackPositionUseCase;
import org.singularux.music.feature.playback.domain.ListenPlaybackStateUseCase;
import org.singularux.music.feature.playback.model.PlaybackItemInfo;
import org.singularux.music.feature.playback.model.PlaybackPosition;

import java.util.Optional;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import lombok.Getter;

@HiltViewModel
public class NowPlayingViewModel extends ViewModel {

    private final @Getter LiveData<Optional<PlaybackItemInfo>> playbackItemInfo;
    private final @Getter LiveData<PlaybackPosition> playbackPosition;
    private final @Getter LiveData<PlaybackState> playbackState;

    private final MusicControllerFacade musicControllerFacade;

    @Inject
    public NowPlayingViewModel(
            @NonNull ListenPlaybackItemInfoUseCase listenPlaybackItemInfoUseCase,
            @NonNull ListenPlaybackPositionUseCase listenPlaybackPositionUseCase,
            @NonNull ListenPlaybackStateUseCase listenPlaybackStateUseCase,
            @NonNull MusicControllerFacade musicControllerFacade
    ) {
        this.playbackItemInfo = LiveDataReactiveStreams
                .fromPublisher(listenPlaybackItemInfoUseCase.get());
        this.playbackPosition = LiveDataReactiveStreams
                .fromPublisher(listenPlaybackPositionUseCase.get());
        this.playbackState = LiveDataReactiveStreams
                .fromPublisher(listenPlaybackStateUseCase.get());
        this.musicControllerFacade = musicControllerFacade;
    }

    public void play() {
        musicControllerFacade.requireMediaController().play();
    }

    public void pause() {
        musicControllerFacade.requireMediaController().pause();
    }

    public void skipPrev() {
        MediaController mediaController = musicControllerFacade.requireMediaController();
        mediaController.seekToPrevious();
        mediaController.play();
    }

    public void skipNext() {
        MediaController mediaController = musicControllerFacade.requireMediaController();
        mediaController.seekToNext();
        mediaController.play();
    }

    public void seekTo(long positionMs) {
        musicControllerFacade.requireMediaController().seekTo(positionMs);
    }

}
