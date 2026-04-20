package org.singularux.music.feature.library.presentation;

import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.Insets;
import androidx.core.splashscreen.SplashScreen;
import androidx.core.view.OnApplyWindowInsetsListener;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.squareup.picasso.Picasso;

import org.singularux.music.core.playback.MusicControllerFacade;
import org.singularux.music.feature.library.R;
import org.singularux.music.feature.library.databinding.ActivityLibraryBinding;
import org.singularux.music.feature.playback.data.PlaybackInfo;
import org.singularux.music.feature.playback.data.PlaybackPosition;
import org.singularux.music.feature.playback.data.PlaybackState;

import java.util.Optional;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class LibraryActivity extends FragmentActivity {

    public @Inject MusicControllerFacade musicControllerFacade;
    public @Inject Picasso picasso;

    private ActivityLibraryBinding binding;
    private LibraryViewModel viewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        // Start activity with splash screen and edge-to-edge enabled
        // Keep splash screen until controller is ready
        SplashScreen.installSplashScreen(this)
                .setKeepOnScreenCondition(() -> !musicControllerFacade.isReady());
        EdgeToEdge.enable(this);
        super.onCreate(savedInstanceState);
        // Populate view
        binding = ActivityLibraryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        // Apply inset listeners
        ViewCompat.setOnApplyWindowInsetsListener(binding.playbackBar.getRoot(),
                new PlaybackBarComponentInsetListener());
        // Get ViewModel
        viewModel = new ViewModelProvider(this).get(LibraryViewModel.class);
        // Observe changes
        viewModel.getPlaybackStateLiveData()
                .observe(this, new PlaybackStateObserver());
        viewModel.getPlaybackPositionLiveData()
                .observe(this, new PlaybackPositionObserver());
        viewModel.getMaybePlaybackInfoLiveData()
                .observe(this, new MaybePlaybackInfoObserver());
    }

    @Override
    protected void onDestroy() {
        // Release current session before destroying
        musicControllerFacade.release();
        super.onDestroy();
    }

    private class PlaybackStateObserver implements Observer<PlaybackState> {

        @Override
        public void onChanged(@NonNull PlaybackState playbackState) {
            // Enable play/pause button logic
            binding.playbackBar.playPause.setEnabled(playbackState.isReady());
            // Show play/pause logic
            if (playbackState.isPlaying()) {
                binding.playbackBar.playPause.setIconResource(R.drawable.pause_24);
                String contentDescription = getString(R.string.playback_bar_action_pause);
                binding.playbackBar.playPause.setContentDescription(contentDescription);
                binding.playbackBar.playPause.setOnClickListener(v -> viewModel.pause());
            } else {
                binding.playbackBar.playPause.setIconResource(R.drawable.play_24);
                String contentDescription = getString(R.string.playback_bar_action_play);
                binding.playbackBar.playPause.setContentDescription(contentDescription);
                binding.playbackBar.playPause.setOnClickListener(v -> viewModel.play());
            }
        }

    }

    private class PlaybackPositionObserver implements Observer<PlaybackPosition> {

        @Override
        public void onChanged(@NonNull PlaybackPosition playbackPosition) {
            // Update progress bar
            float max = binding.playbackBar.progress.getMax();
            int progress = (int) (playbackPosition.getProgress() * max);
            binding.playbackBar.progress.setProgressCompat(progress, false);
        }

    }

    private class MaybePlaybackInfoObserver implements Observer<Optional<PlaybackInfo>> {

        @Override
        public void onChanged(@NonNull Optional<PlaybackInfo> maybePlaybackInfo) {
            if (maybePlaybackInfo.isPresent()) {
                PlaybackInfo playbackInfo = maybePlaybackInfo.get();
                // Set track and artist data
                String artist = playbackInfo.getArtistName();
                if (artist == null) {
                    artist = getString(R.string.playback_bar_no_artist);
                }
                binding.playbackBar.title.setText(playbackInfo.getTitle());
                binding.playbackBar.artist.setText(artist);
                // Load thumbnail
                if (playbackInfo.getArtworkUri() != null) {
                    picasso.load(playbackInfo.getArtworkUri())
                            .resizeDimen(R.dimen.playback_bar_artwork, R.dimen.playback_bar_artwork)
                            .into(binding.playbackBar.artwork);
                } else {
                    picasso.cancelRequest(binding.playbackBar.artwork);
                    binding.playbackBar.artwork.setImageDrawable(null);
                }
            } else {
                // Set empty data
                String track = getString(R.string.playback_bar_no_track);
                String artist = getString(R.string.playback_bar_no_artist);
                binding.playbackBar.title.setText(track);
                binding.playbackBar.artist.setText(artist);
                // Set empty thumbnail
                picasso.cancelRequest(binding.playbackBar.artwork);
                binding.playbackBar.artwork.setImageDrawable(null);
            }
        }

    }

    private static class PlaybackBarComponentInsetListener implements OnApplyWindowInsetsListener {

        @Override
        public @NonNull WindowInsetsCompat onApplyWindowInsets(
                @NonNull View view, @NonNull WindowInsetsCompat windowInsets) {
            // Retrieve required insets
            Insets insets = windowInsets.getInsets(WindowInsetsCompat.Type.navigationBars());
            // Apply padding
            view.setPadding(insets.left, 0, insets.right, insets.bottom);
            // Last element of the view
            return WindowInsetsCompat.CONSUMED;
        }

    }


}
