package org.singularux.music.feature.nowplaying.presentation;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.LiveDataReactiveStreams;
import androidx.lifecycle.ViewModel;

import org.singularux.music.feature.playback.data.PlaybackItemInfo;
import org.singularux.music.feature.playback.data.PlaybackPosition;
import org.singularux.music.feature.playback.data.PlaybackState;
import org.singularux.music.feature.playback.data.PlayerAction;
import org.singularux.music.feature.playback.domain.ListenPlaybackItemInfoUseCase;
import org.singularux.music.feature.playback.domain.ListenPlaybackPositionUseCase;
import org.singularux.music.feature.playback.domain.ListenPlaybackStateUseCase;
import org.singularux.music.feature.playback.domain.OnPlayerActionUseCase;

import java.time.Duration;
import java.util.Optional;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import lombok.Getter;

@HiltViewModel
public class NowPlayingViewModel extends ViewModel {

    private final @Getter LiveData<Optional<PlaybackItemInfo>> playbackItemInfo;
    private final @Getter LiveData<PlaybackPosition> playbackPosition;
    private final @Getter LiveData<PlaybackState> playbackState;
    private final OnPlayerActionUseCase onPlayerActionUseCase;

    @Inject
    public NowPlayingViewModel(
            OnPlayerActionUseCase onPlayerActionUseCase,
            @NonNull ListenPlaybackItemInfoUseCase listenPlaybackItemInfoUseCase,
            @NonNull ListenPlaybackPositionUseCase listenPlaybackPositionUseCase,
            @NonNull ListenPlaybackStateUseCase listenPlaybackStateUseCase) {
        this.playbackItemInfo = LiveDataReactiveStreams
                .fromPublisher(listenPlaybackItemInfoUseCase.get());
        this.playbackPosition = LiveDataReactiveStreams
                .fromPublisher(listenPlaybackPositionUseCase.get());
        this.playbackState = LiveDataReactiveStreams
                .fromPublisher(listenPlaybackStateUseCase.get());
        this.onPlayerActionUseCase = onPlayerActionUseCase;
    }

    public void play() {
        onPlayerActionUseCase.run(new PlayerAction.Play());
    }

    public void pause() {
        onPlayerActionUseCase.run(new PlayerAction.Pause());
    }

    public void skipPrev() {
        onPlayerActionUseCase.run(new PlayerAction.SkipPrev());
    }

    public void skipNext() {
        onPlayerActionUseCase.run(new PlayerAction.SkipNext());
    }

    public void seekTo(long pointerMs) {
        onPlayerActionUseCase.run(new PlayerAction.SeekTo(Duration.ofMillis(pointerMs)));
    }

}
