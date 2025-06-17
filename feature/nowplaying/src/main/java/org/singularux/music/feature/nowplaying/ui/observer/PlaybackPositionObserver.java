package org.singularux.music.feature.nowplaying.ui.observer;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.Observer;

import org.singularux.music.feature.nowplaying.R;
import org.singularux.music.feature.nowplaying.databinding.RouteNowPlayingBinding;
import org.singularux.music.feature.playback.domain.model.PlaybackPosition;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PlaybackPositionObserver implements Observer<PlaybackPosition> {

    private final Context context;
    private final RouteNowPlayingBinding binding;

    @Override
    public void onChanged(@NonNull PlaybackPosition playbackPosition) {
        // Update slider value
        binding.nowPlayingProgress.setValue(playbackPosition.getPosition());
        // Update current time label
        long currentMinutes = playbackPosition.getCurrent().getSeconds() / 60;
        long currentSeconds = playbackPosition.getCurrent().getSeconds() % 60;
        String currentString = context.getString(R.string.now_playing_duration,
                currentMinutes, currentSeconds);
        binding.nowPlayingCurrentTime.setText(currentString);
    }

}
