package org.singularux.music.feature.nowplaying.ui.observer;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.Observer;

import org.singularux.music.feature.nowplaying.R;
import org.singularux.music.feature.nowplaying.databinding.RouteNowPlayingBinding;
import org.singularux.music.feature.playback.domain.model.PlaybackPosition;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PlaybackPositionObserver implements Observer<PlaybackPosition> {

    private static final String TAG = "PlaybackPositionObserver";

    private final Context context;
    private final RouteNowPlayingBinding binding;

    @Override
    public void onChanged(@NonNull PlaybackPosition playbackPosition) {
        Log.d(TAG, "Received PlaybackPosition: " + playbackPosition);
        // Update slider value
        binding.nowPlayingProgress.setValue(playbackPosition.getCurrentPosition());
        // Extract the duration for that percentage of the track
        long totalSeconds = (long) (playbackPosition.getContentDuration().getSeconds()
                * playbackPosition.getCurrentPosition());
        // Extract minutes and seconds part
        long currentMinutes = totalSeconds / 60;
        long currentSeconds = totalSeconds % 60;
        // Update current time label
        String currentString = context.getString(R.string.now_playing_duration,
                currentMinutes, currentSeconds);
        binding.nowPlayingCurrentTime.setText(currentString);
    }

}
