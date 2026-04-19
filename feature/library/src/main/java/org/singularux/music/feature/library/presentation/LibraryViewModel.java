package org.singularux.music.feature.library.presentation;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.LiveDataReactiveStreams;
import androidx.lifecycle.ViewModel;

import org.singularux.music.feature.playback.data.PlaybackInfo;
import org.singularux.music.feature.playback.data.PlaybackPosition;
import org.singularux.music.feature.playback.data.PlaybackState;
import org.singularux.music.feature.playback.data.PlayerAction;
import org.singularux.music.feature.playback.domain.ListenPlaybackInfoUseCase;
import org.singularux.music.feature.playback.domain.ListenPlaybackPositionUseCase;
import org.singularux.music.feature.playback.domain.ListenPlaybackStateUseCase;
import org.singularux.music.feature.playback.domain.OnPlayerActionUseCase;

import java.util.Optional;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import lombok.Getter;

@HiltViewModel
public class LibraryViewModel extends ViewModel {

    private final @Getter LiveData<PlaybackPosition> playbackPositionLiveData;
    private final @Getter LiveData<Optional<PlaybackInfo>> maybePlaybackInfoLiveData;
    private final @Getter LiveData<PlaybackState> playbackStateLiveData;

    private final OnPlayerActionUseCase onPlayerActionUseCase;

    @Inject
    public LibraryViewModel(ListenPlaybackPositionUseCase listenPlaybackPositionUseCase,
                            ListenPlaybackInfoUseCase listenPlaybackInfoUseCase,
                            ListenPlaybackStateUseCase listenPlaybackStateUseCase,
                            OnPlayerActionUseCase onPlayerActionUseCase) {
        this.playbackPositionLiveData = LiveDataReactiveStreams
                .fromPublisher(listenPlaybackPositionUseCase.get());
        this.maybePlaybackInfoLiveData = LiveDataReactiveStreams
                .fromPublisher(listenPlaybackInfoUseCase.get());
        this.playbackStateLiveData = LiveDataReactiveStreams
                .fromPublisher(listenPlaybackStateUseCase.get());
        this.onPlayerActionUseCase = onPlayerActionUseCase;
    }

    public void play() {
        onPlayerActionUseCase.run(new PlayerAction.Play());
    }

    public void pause() {
        onPlayerActionUseCase.run(new PlayerAction.Pause());
    }

}
