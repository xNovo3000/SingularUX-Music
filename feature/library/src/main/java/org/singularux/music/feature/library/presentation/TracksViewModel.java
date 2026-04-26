package org.singularux.music.feature.library.presentation;

import androidx.lifecycle.ViewModel;

import org.singularux.music.core.permission.MusicPermission;
import org.singularux.music.feature.library.domain.GetStringPermissionUseCase;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import lombok.Getter;

@HiltViewModel
public class TracksViewModel extends ViewModel {

    private final @Getter String readMusicPermission;

    @Inject
    public TracksViewModel(GetStringPermissionUseCase getStringPermissionUseCase) {
        this.readMusicPermission = getStringPermissionUseCase.get(MusicPermission.READ_MEDIA_AUDIO);
    }

    public void playShuffled() {

    }

}
