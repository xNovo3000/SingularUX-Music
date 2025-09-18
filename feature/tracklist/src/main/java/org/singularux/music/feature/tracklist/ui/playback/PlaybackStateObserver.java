package org.singularux.music.feature.tracklist.ui.playback;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;

import org.singularux.music.feature.playback.model.PlaybackState;
import org.singularux.music.feature.tracklist.R;
import org.singularux.music.feature.tracklist.databinding.RouteTrackListBinding;
import org.singularux.music.feature.tracklist.ui.TrackListViewModel;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PlaybackStateObserver implements Observer<PlaybackState> {

    private final Context context;
    private final RouteTrackListBinding binding;
    private final TrackListViewModel viewModel;

    @Override
    public void onChanged(@NonNull PlaybackState playbackState) {
        boolean isPlayPauseEnabled;
        Drawable icon;
        String contentDescription;
        View.OnClickListener onClickListener;
        // Extract
        isPlayPauseEnabled = playbackState.isReady();
        if (playbackState.isPlaying()) {
            icon = ContextCompat.getDrawable(context, R.drawable.round_pause_24);
            contentDescription = context.getString(R.string.playback_bar_pause);
            onClickListener = v -> viewModel.pause();
        } else {
            icon = ContextCompat.getDrawable(context, R.drawable.round_play_arrow_24);
            contentDescription = context.getString(R.string.playback_bar_play);
            onClickListener = v -> viewModel.play();
        }
        // Update UI
        binding.playbackBar.playbackBarPlayPause.setEnabled(isPlayPauseEnabled);
        binding.playbackBar.playbackBarPlayPause.setIcon(icon);
        binding.playbackBar.playbackBarPlayPause.setContentDescription(contentDescription);
        binding.playbackBar.playbackBarPlayPause.setOnClickListener(onClickListener);
    }

}
