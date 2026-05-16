package org.singularux.music.feature.library.presentation;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
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
import org.singularux.music.feature.playback.data.PlaybackItemInfo;
import org.singularux.music.feature.playback.data.PlaybackPosition;
import org.singularux.music.feature.playback.data.PlaybackState;

import java.util.Optional;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class LibraryActivity extends FragmentActivity {

    private static final String TAG = "LibraryActivity";

    public @Inject MusicControllerFacade musicControllerFacade;
    public @Inject Picasso picasso;

    private ActivityLibraryBinding binding;
    private LibraryViewModel viewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        // Create activity with edge-to-edge support and splash screen until controller is ready
        SplashScreen.installSplashScreen(this)
                .setKeepOnScreenCondition(() -> !musicControllerFacade.isReady());
        EdgeToEdge.enable(this);
        super.onCreate(savedInstanceState);
        // Extract XML elements
        binding = ActivityLibraryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        // Add inset listeners
        ViewCompat.setOnApplyWindowInsetsListener(binding.nowPlayingBar.getRoot(),
                new NowPlayingBarContainerInsetListener());
        // Extract ViewModel
        viewModel = new ViewModelProvider(this).get(LibraryViewModel.class);
        // Add static action listeners
        binding.nowPlayingBar.getRoot()
                .setOnClickListener(new NowPlayingBarContainerOnClickListener());
        // Listen data
        viewModel.getPlaybackState().observe(this, new PlaybackStateObserver());
        viewModel.getPlaybackItemInfo().observe(this, new PlaybackItemInfoObserver());
        viewModel.getPlaybackPosition().observe(this, new PlaybackPositionObserver());
    }

    @Override
    protected void onDestroy() {
        // Release controller before finishing this activity
        if (isFinishing())
            musicControllerFacade.release();
        super.onDestroy();
    }

    private final class PlaybackStateObserver implements Observer<PlaybackState> {

        @Override
        public void onChanged(@NonNull PlaybackState playbackState) {
            // Set enabled buttons
            boolean playPauseEnabled = playbackState.isReady();
            binding.nowPlayingBar.playPause.setEnabled(playPauseEnabled);
            // Set play/pause drawable and action
            if (playbackState.isPlaying()) {
                // Set pause action
                binding.nowPlayingBar.playPause.setIconResource(R.drawable.pause_24);
                binding.nowPlayingBar.playPause.setOnClickListener(v -> viewModel.pause());
            } else {
                // Set play action
                binding.nowPlayingBar.playPause.setIconResource(R.drawable.play_24);
                binding.nowPlayingBar.playPause.setOnClickListener(v -> viewModel.play());
            }
        }

    }

    private final class PlaybackItemInfoObserver implements Observer<Optional<PlaybackItemInfo>> {

        @Override
        public void onChanged(@NonNull Optional<PlaybackItemInfo> maybePlaybackItemInfo) {
            // Get title, artist (if present) and artwork (if present)
            String title, artist;
            Uri artworkPath;
            if (maybePlaybackItemInfo.isPresent()) {
                PlaybackItemInfo playbackItemInfo = maybePlaybackItemInfo.get();
                title = playbackItemInfo.getTitle();
                if (playbackItemInfo.getArtistName() != null) {
                    artist = playbackItemInfo.getArtistName();
                } else {
                    artist = getString(R.string.now_playing_bar_unknown_artist);
                }
                if (playbackItemInfo.getArtworkPath() != null) {
                    artworkPath = playbackItemInfo.getArtworkPath();
                } else {
                    artworkPath = null;
                }
            } else {
                title = getString(R.string.now_playing_bar_no_track);
                artist = getString(R.string.now_playing_bar_no_artist);
                artworkPath = null;
            }
            // Apply them
            binding.nowPlayingBar.title.setText(title);
            binding.nowPlayingBar.artist.setText(artist);
            if (artworkPath != null) {
                picasso.load(artworkPath)
                        .into(binding.nowPlayingBar.artwork);
            } else {
                picasso.cancelRequest(binding.nowPlayingBar.artwork);
                binding.nowPlayingBar.artwork.setImageDrawable(null);
            }
        }

    }

    private final class PlaybackPositionObserver implements Observer<PlaybackPosition> {

        @Override
        public void onChanged(@NonNull PlaybackPosition playbackPosition) {
            // Extract position and duration in milliseconds
            long sliderMaxValue = binding.nowPlayingBar.progressBar.getMax();
            long positionMs = playbackPosition.getCurrentPosition().toMillis();
            long durationMs = playbackPosition.getContentDuration().toMillis();
            // Calculate progress and update slider value accordingly
            int progress = (int) (positionMs * sliderMaxValue / durationMs);
            binding.nowPlayingBar.progressBar.setProgress(progress);
        }

    }

    private final class NowPlayingBarContainerOnClickListener implements View.OnClickListener {

        private static final String NOW_PLAYING_ACTIVITY_CLASS_NAME =
                "org.singularux.music.feature.nowplaying.presentation.NowPlaying2Activity";

        @Override
        public void onClick(View v) {
            // Get class
            Class<?> nowPlayingActivityClass;
            try {
                nowPlayingActivityClass = Class.forName(NOW_PLAYING_ACTIVITY_CLASS_NAME);
            } catch (ClassNotFoundException e) {
                Log.e(TAG, "Now playing activity not found: "
                        + NOW_PLAYING_ACTIVITY_CLASS_NAME);
                return;
            }
            // Create intent and start activity
            Intent intent = new Intent(getApplicationContext(), nowPlayingActivityClass);
            startActivity(intent);
        }

    }

    public static final class NowPlayingBarContainerInsetListener
            implements OnApplyWindowInsetsListener {

        @Override
        public @NonNull WindowInsetsCompat onApplyWindowInsets(
                @NonNull View view, @NonNull WindowInsetsCompat windowInsets) {
            Insets insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
            view.setPadding(insets.left, 0, insets.right, insets.bottom);
            return WindowInsetsCompat.CONSUMED;
        }

    }

}
