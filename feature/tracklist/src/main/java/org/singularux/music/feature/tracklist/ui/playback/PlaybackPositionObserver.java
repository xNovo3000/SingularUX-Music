package org.singularux.music.feature.tracklist.ui.playback;

import androidx.annotation.NonNull;
import androidx.lifecycle.Observer;

import org.singularux.music.feature.playback.model.PlaybackPosition;
import org.singularux.music.feature.tracklist.databinding.RouteTrackListBinding;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PlaybackPositionObserver implements Observer<PlaybackPosition> {

    private final RouteTrackListBinding binding;

    @Override
    public void onChanged(@NonNull PlaybackPosition playbackPosition) {
        int position = (int) (playbackPosition.getProgress() * 1000.0F);
        binding.playbackBar.playbackBarProgress.setProgressCompat(position, true);
    }

}
