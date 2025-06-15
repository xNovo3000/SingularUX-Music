package org.singularux.music.feature.playback.viewmodel;

import androidx.lifecycle.ViewModel;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class NowPlayingViewModel extends ViewModel {

    @Inject
    public NowPlayingViewModel() {
    }

}
