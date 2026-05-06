package org.singularux.music.feature.nowplaying.presentation;

import androidx.lifecycle.ViewModel;

import org.singularux.music.feature.playback.data.PlayerAction;
import org.singularux.music.feature.playback.domain.OnPlayerActionUseCase;

import java.time.Duration;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class NowPlaying2ViewModel extends ViewModel {

    private final OnPlayerActionUseCase onPlayerActionUseCase;

    @Inject
    public NowPlaying2ViewModel(OnPlayerActionUseCase onPlayerActionUseCase) {
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
