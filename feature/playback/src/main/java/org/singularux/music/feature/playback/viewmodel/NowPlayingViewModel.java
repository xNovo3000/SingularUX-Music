package org.singularux.music.feature.playback.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.LiveDataReactiveStreams;
import androidx.lifecycle.ViewModel;

import org.singularux.music.feature.playback.domain.ListenPlaybackInfoUseCase;
import org.singularux.music.feature.playback.domain.ListenPlaybackPositionUseCase;
import org.singularux.music.feature.playback.foreground.MusicControllerFacade;
import org.singularux.music.feature.playback.model.PlaybackInfo;
import org.singularux.music.feature.playback.model.PlaybackPosition;

import java.util.Optional;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import lombok.Getter;

@HiltViewModel
public class NowPlayingViewModel extends ViewModel {

    private final @Getter LiveData<Optional<PlaybackInfo>> playbackInfo;
    private final @Getter LiveData<PlaybackPosition> playbackPosition;

    private final MusicControllerFacade musicControllerFacade;

    @Inject
    public NowPlayingViewModel(
            @NonNull ListenPlaybackInfoUseCase listenPlaybackInfoUseCase,
            @NonNull ListenPlaybackPositionUseCase listenPlaybackPositionUseCase,
            @NonNull MusicControllerFacade musicControllerFacade
    ) {
        this.playbackInfo = LiveDataReactiveStreams.fromPublisher(listenPlaybackInfoUseCase.get());
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
        musicControllerFacade.requireMediaController().seekToPreviousMediaItem();
    }

    public void skipNext() {
        musicControllerFacade.requireMediaController().seekToNextMediaItem();
    }

}
