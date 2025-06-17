package org.singularux.music.feature.nowplaying.ui.observer;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.Observer;

import com.squareup.picasso.Picasso;

import org.singularux.music.feature.nowplaying.R;
import org.singularux.music.feature.nowplaying.databinding.RouteNowPlayingBinding;
import org.singularux.music.feature.nowplaying.ui.NowPlayingViewModel;
import org.singularux.music.feature.nowplaying.ui.utils.SliderDurationLabelFormatter;
import org.singularux.music.feature.playback.domain.model.PlaybackInfo;

import java.time.Duration;
import java.util.Objects;
import java.util.Optional;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PlaybackInfoObserver implements Observer<Optional<PlaybackInfo>> {

    private final Context context;
    private final RouteNowPlayingBinding binding;
    private final NowPlayingViewModel viewModel;

    private final Picasso picasso;

    private final ProgressSliderListener progressSliderListener;
    private final SliderDurationLabelFormatter sliderDurationLabelFormatter;

    @Override
    public void onChanged(@NonNull Optional<PlaybackInfo> maybePlaybackInfo) {
        if (maybePlaybackInfo.isPresent()) {
            PlaybackInfo playbackInfo = maybePlaybackInfo.get();
            // Title
            binding.nowPlayingTitle.setText(playbackInfo.getTitle());
            // Artist
            String artist = Objects.requireNonNullElseGet(playbackInfo.getArtistName(),
                    () -> context.getText(R.string.now_playing_artist_placeholder).toString());
            binding.nowPlayingArtist.setText(artist);
            // Duration
            Duration duration = playbackInfo.getDuration();
            long durationMinutesPart = duration.getSeconds() / 60;
            long durationSecondsPart = duration.getSeconds() % 60;
            String durationString = context.getString(R.string.now_playing_duration,
                    durationMinutesPart, durationSecondsPart);
            binding.nowPlayingTotalTime.setText(durationString);
            progressSliderListener.setContentDuration(duration);
            sliderDurationLabelFormatter.setContentDuration(duration);
            // Skip previous
            binding.nowPlayingPrev.setEnabled(true);
            binding.nowPlayingPrev.setOnClickListener(v -> viewModel.skipPrev());
            // Play/Pause
            binding.nowPlayingPlayPause.setEnabled(true);
            if (playbackInfo.isPlaying()) {
                binding.nowPlayingPlayPause.setContentDescription(context.getText(R.string.now_playing_pause));
                binding.nowPlayingPlayPause.setIconResource(R.drawable.round_pause_24);
                binding.nowPlayingPlayPause.setOnClickListener(v -> viewModel.pause());
            } else {
                binding.nowPlayingPlayPause.setContentDescription(context.getText(R.string.now_playing_play));
                binding.nowPlayingPlayPause.setIconResource(R.drawable.round_play_arrow_24);
                binding.nowPlayingPlayPause.setOnClickListener(v -> viewModel.play());
            }
            // Skip next
            binding.nowPlayingNext.setEnabled(playbackInfo.isHasNext());
            binding.nowPlayingNext.setOnClickListener(v -> viewModel.skipNext());
            // Artwork
            if (playbackInfo.getArtworkUri() != null) {
                picasso.load(playbackInfo.getArtworkUri())
                        .into(binding.nowPlayingArtwork);
            } else {
                binding.nowPlayingArtwork.setImageDrawable(null);
            }
        } else {
            binding.nowPlayingTitle.setText(context.getText(R.string.now_playing_title_placeholder));
            binding.nowPlayingArtist.setText(context.getText(R.string.now_playing_artist_placeholder));
            binding.nowPlayingTotalTime.setText(context.getText(R.string.now_playing_duration_placeholder));
            progressSliderListener.setContentDuration(null);
            sliderDurationLabelFormatter.setContentDuration(null);
            binding.nowPlayingPrev.setEnabled(false);
            binding.nowPlayingPlayPause.setEnabled(false);
            binding.nowPlayingNext.setEnabled(false);
            binding.nowPlayingArtwork.setImageDrawable(null);
        }
    }

}
