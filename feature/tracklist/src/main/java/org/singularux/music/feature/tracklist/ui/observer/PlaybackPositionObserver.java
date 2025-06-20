package org.singularux.music.feature.tracklist.ui.observer;

import androidx.lifecycle.Observer;

import org.singularux.music.feature.playback.domain.model.PlaybackPosition;
import org.singularux.music.feature.tracklist.databinding.RouteTrackListBinding;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PlaybackPositionObserver implements Observer<PlaybackPosition> {

    private final RouteTrackListBinding binding;

    @Override
    public void onChanged(PlaybackPosition playbackPosition) {
        int position = (int) (playbackPosition.getPosition() * 1000.0F);
        binding.playbackBar.playbackBarProgress.setProgressCompat(position, true);
    }

}
