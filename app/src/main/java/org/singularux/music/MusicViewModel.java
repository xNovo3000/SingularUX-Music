package org.singularux.music;

import androidx.lifecycle.ViewModel;

import org.singularux.music.core.playback.MusicControllerFacade;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class MusicViewModel extends ViewModel {

    private final MusicControllerFacade musicControllerFacade;

    @Inject
    public MusicViewModel(MusicControllerFacade musicControllerFacade) {
        this.musicControllerFacade = musicControllerFacade;
    }

    @Override
    protected void onCleared() {
        musicControllerFacade.release();
    }

}