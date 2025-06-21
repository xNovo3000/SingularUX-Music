package org.singularux.music.feature.nowplaying.ui;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.LiveDataReactiveStreams;
import androidx.lifecycle.ViewModel;
import androidx.media3.session.MediaController;

import org.singularux.music.feature.playback.domain.usecase.ListenPlaybackItemInfoUseCase;
import org.singularux.music.feature.playback.domain.usecase.ListenPlaybackPositionUseCase;
import org.singularux.music.feature.playback.foreground.MusicControllerFacade;
import org.singularux.music.feature.playback.domain.model.PlaybackItemInfo;
import org.singularux.music.feature.playback.domain.model.PlaybackPosition;

import java.util.Optional;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import lombok.Getter;

@HiltViewModel
public class NowPlayingViewModel extends ViewModel {

    private final @Getter LiveData<Optional<PlaybackItemInfo>> playbackInfo;
    private final @Getter LiveData<PlaybackPosition> playbackPosition;

    private final MusicControllerFacade musicControllerFacade;

    @Inject
    public NowPlayingViewModel(
            @NonNull ListenPlaybackItemInfoUseCase listenPlaybackItemInfoUseCase,
            @NonNull ListenPlaybackPositionUseCase listenPlaybackPositionUseCase,
            @NonNull MusicControllerFacade musicControllerFacade
    ) {
        this.playbackInfo = LiveDataReactiveStreams.fromPublisher(listenPlaybackItemInfoUseCase.get());
        this.playbackPosition = LiveDataReactiveStreams
                .fromPublisher(listenPlaybackPositionUseCase.get());
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

    public void seekTo(long newPosition) {
        musicControllerFacade.requireMediaController().seekTo(newPosition * 1000);
    }

}
