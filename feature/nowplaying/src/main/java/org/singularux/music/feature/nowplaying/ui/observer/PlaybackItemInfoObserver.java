package org.singularux.music.feature.nowplaying.ui.observer;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.Observer;

import com.squareup.picasso.Picasso;

import org.singularux.music.feature.nowplaying.R;
import org.singularux.music.feature.nowplaying.databinding.RouteNowPlayingBinding;
import org.singularux.music.feature.nowplaying.ui.utils.SliderDurationLabelFormatter;
import org.singularux.music.feature.playback.model.PlaybackItemInfo;

import java.time.Duration;
import java.util.Optional;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PlaybackItemInfoObserver implements Observer<Optional<PlaybackItemInfo>> {

    private static final String TAG = "PlaybackItemInfoObserver";

    private final Context context;
    private final RouteNowPlayingBinding binding;
    private final Picasso picasso;

    private final ProgressSliderListener progressSliderListener;
    private final SliderDurationLabelFormatter sliderDurationLabelFormatter;

    @Override
    public void onChanged(@NonNull Optional<PlaybackItemInfo> maybePlaybackItemInfo) {
        String title;
        String artists;
        Duration duration;
        String durationString;
        Uri artwork;
        // Extract
        if (maybePlaybackItemInfo.isPresent()) {
            PlaybackItemInfo playbackItemInfo = maybePlaybackItemInfo.get();
            Log.d(TAG, "Received PlaybackItemInfo: " + playbackItemInfo);
            // Title
            title = playbackItemInfo.getTitle();
            // Artists
            if (playbackItemInfo.getArtistName() != null) {
                artists = playbackItemInfo.getArtistName();
            } else {
                artists = context.getString(R.string.now_playing_artist_placeholder);
            }
            // Duration
            duration = playbackItemInfo.getDuration();
            durationString = context.getString(R.string.now_playing_duration,
                    duration.getSeconds() / 60, duration.getSeconds() % 60);
            // Artwork
            artwork = playbackItemInfo.getArtworkPath();
        } else {
            title = context.getString(R.string.now_playing_title_placeholder);
            artists = context.getString(R.string.now_playing_artist_placeholder);
            duration = null;
            durationString = context.getString(R.string.now_playing_duration_placeholder);
            artwork = null;
        }
        // Apply
        binding.nowPlayingTitle.setText(title);
        binding.nowPlayingArtist.setText(artists);
        progressSliderListener.setDuration(duration);
        sliderDurationLabelFormatter.setDuration(duration);
        binding.nowPlayingTotalTime.setText(durationString);
        if (artwork != null) {
            picasso.load(artwork)
                    .into(binding.nowPlayingArtwork);
        } else {
            binding.nowPlayingArtwork.setImageDrawable(null);
        }
    }

}
