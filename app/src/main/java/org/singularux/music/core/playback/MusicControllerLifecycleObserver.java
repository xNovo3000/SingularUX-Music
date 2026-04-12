package org.singularux.music.core.playback;

import androidx.annotation.NonNull;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MusicControllerLifecycleObserver implements DefaultLifecycleObserver {

    private final @Getter MusicControllerFacade musicControllerFacade;

    @Override
    public void onDestroy(@NonNull LifecycleOwner owner) {
        // Always release the controller
        musicControllerFacade.release();
    }

}
