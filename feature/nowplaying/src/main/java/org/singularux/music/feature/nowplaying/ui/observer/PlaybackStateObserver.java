package org.singularux.music.feature.nowplaying.ui.observer;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;

import org.singularux.music.feature.nowplaying.R;
import org.singularux.music.feature.nowplaying.databinding.RouteNowPlayingBinding;
import org.singularux.music.feature.nowplaying.ui.NowPlayingViewModel;
import org.singularux.music.feature.playback.domain.model.PlaybackState;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PlaybackStateObserver implements Observer<PlaybackState> {

    private static final String TAG = "PlaybackStateObserver";

    private final Context context;
    private final RouteNowPlayingBinding binding;
    private final NowPlayingViewModel viewModel;

    @Override
    public void onChanged(@NonNull PlaybackState playbackState) {
        Log.d(TAG, "Received PlaybackState: " + playbackState);
        boolean isPlayPauseEnabled;
        boolean isSkipNextEnabled;
        Drawable icon;
        String contentDescription;
        View.OnClickListener onPlayPauseClickListener;
        // Extract
        isPlayPauseEnabled = playbackState.isReady();
        isSkipNextEnabled = playbackState.hasNextItem();
        if (playbackState.isPlaying()) {
            icon = ContextCompat.getDrawable(context, R.drawable.round_pause_24);
            contentDescription = context.getString(R.string.now_playing_pause);
            onPlayPauseClickListener = v -> viewModel.pause();
        } else {
            icon = ContextCompat.getDrawable(context, R.drawable.round_play_arrow_24);
            contentDescription = context.getString(R.string.now_playing_play);
            onPlayPauseClickListener = v -> viewModel.play();
        }
        // Update UI
        binding.nowPlayingPlayPause.setEnabled(isPlayPauseEnabled);
        binding.nowPlayingPlayPause.setIcon(icon);
        binding.nowPlayingPlayPause.setContentDescription(contentDescription);
        binding.nowPlayingPlayPause.setOnClickListener(onPlayPauseClickListener);
        binding.nowPlayingPrev.setOnClickListener(v -> viewModel.skipPrev());
        binding.nowPlayingNext.setEnabled(isSkipNextEnabled);
        binding.nowPlayingNext.setOnClickListener(v -> viewModel.skipNext());
    }

}
