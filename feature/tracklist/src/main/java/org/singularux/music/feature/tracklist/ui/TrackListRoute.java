package org.singularux.music.feature.tracklist.ui;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.squareup.picasso.Picasso;

import org.singularux.music.core.permission.MusicPermission;
import org.singularux.music.core.permission.MusicPermissionManager;
import org.singularux.music.feature.playback.model.PlaybackInfo;
import org.singularux.music.feature.playback.model.PlaybackPosition;
import org.singularux.music.feature.tracklist.R;
import org.singularux.music.feature.tracklist.databinding.RouteTrackListBinding;
import org.singularux.music.feature.tracklist.ui.inset.PlaybackBarInsetListener;
import org.singularux.music.feature.tracklist.ui.inset.TrackListInsetListener;
import org.singularux.music.feature.tracklist.ui.inset.TrackListSearchBarInsetListener;
import org.singularux.music.feature.tracklist.viewmodel.TrackListViewModel;

import java.util.Optional;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class TrackListRoute extends Fragment {

    private static final String TAG = "TrackListRoute";

    @Inject public MusicPermissionManager musicPermissionManager;
    @Inject public PlaybackBarInsetListener playbackBarInsetListener;
    @Inject public TrackListSearchBarInsetListener trackListSearchBarInsetListener;
    @Inject public TrackListInsetListener trackListInsetListener;
    @Inject public TrackListAdapter trackListAdapter;
    @Inject public Picasso picasso;

    private RouteTrackListBinding binding;
    private TrackListViewModel viewModel;

    public TrackListRoute() {
        super(R.layout.route_track_list);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        binding = RouteTrackListBinding.bind(view);
        viewModel = new ViewModelProvider(this).get(TrackListViewModel.class);
        // Add InsetListeners
        ViewCompat.setOnApplyWindowInsetsListener(
                binding.trackListSearchBar, trackListSearchBarInsetListener);
        ViewCompat.setOnApplyWindowInsetsListener(
                binding.trackListRecyclerview, trackListInsetListener);
        ViewCompat.setOnApplyWindowInsetsListener(
                binding.playbackBar.playbackBarContainer, playbackBarInsetListener);
        // Request permission to read music
        ActivityResultLauncher<String> readMusicPermissionRequest = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                new RequestMusicPermissionResultCallback());
        // Add click listeners
        trackListAdapter.setOnItemClickListener(viewModel::playFromSpecificTrackListIndex);
        binding.playbackBar.playbackBarContainer.setOnClickListener(v -> {});
        // Set binders, listeners and adapters
        binding.trackListRecyclerview.setAdapter(trackListAdapter);
        readMusicPermissionRequest.launch(musicPermissionManager.getPermissionString(MusicPermission.READ_MUSIC));
        viewModel.getPlaybackPosition().observe(getViewLifecycleOwner(),
                new PlaybackPositionObserver());
        viewModel.getPlaybackInfo().observe(getViewLifecycleOwner(),
                new PlaybackInfoObserver());
    }

    private class RequestMusicPermissionResultCallback implements ActivityResultCallback<Boolean> {

        @Override
        public void onActivityResult(Boolean result) {
            if (result) {
                Log.i(TAG, "Permission READ_MUSIC granted");
                // Observe track list
                viewModel.getTrackList().observe(getViewLifecycleOwner(),
                        trackItems -> trackListAdapter.submitList(trackItems));
            } else {
                Log.i(TAG, "Permission DENIED granted");
            }
        }

    }

    private class PlaybackPositionObserver implements Observer<PlaybackPosition> {

        @Override
        public void onChanged(PlaybackPosition playbackPosition) {
            int position = (int) (playbackPosition.getPosition() * 1000.0F);
            binding.playbackBar.playbackBarProgress.setProgressCompat(position, true);
        }

    }

    private class PlaybackInfoObserver implements Observer<Optional<PlaybackInfo>> {

        @Override
        public void onChanged(Optional<PlaybackInfo> maybePlaybackInfo) {
            Context context = ContextCompat.getContextForLanguage(requireContext());
            // Extract data
            String title;
            String artist;
            boolean isPlaying = false;
            Drawable icon;
            boolean enabled;
            Uri artworkUri = null;
            // Apply data
            if (maybePlaybackInfo.isPresent()) {
                PlaybackInfo playbackInfo = maybePlaybackInfo.get();
                if (playbackInfo.getTitle() != null) {
                    title = playbackInfo.getTitle();
                } else {
                    title = context.getString(R.string.track_item_unknown_track);
                }
                if (playbackInfo.getArtistsName() != null) {
                    artist = playbackInfo.getArtistsName();
                } else {
                    artist = context.getString(R.string.track_item_unknown_artist);
                }
                isPlaying = playbackInfo.isPlaying();
                if (playbackInfo.isPlaying()) {
                    icon = ContextCompat.getDrawable(context, R.drawable.round_pause_24);
                } else {
                    icon = ContextCompat.getDrawable(context, R.drawable.round_play_arrow_24);
                }
                enabled = true;
                artworkUri = playbackInfo.getArtworkUri();
            } else {
                title = context.getString(R.string.track_item_unknown_track);
                artist = context.getString(R.string.track_item_unknown_artist);
                icon = ContextCompat.getDrawable(context, R.drawable.round_play_arrow_24);
                enabled = false;
            }
            // Apply UI
            binding.playbackBar.playbackBarTitle.setText(title);
            binding.playbackBar.playbackBarArtist.setText(artist);
            binding.playbackBar.playbackBarPlayPause.setIcon(icon);
            binding.playbackBar.playbackBarPlayPause.setEnabled(enabled);
            picasso.load(artworkUri)
                    .resizeDimen(R.dimen.playback_bar_artwork_size, R.dimen.playback_bar_artwork_size)
                    .into(binding.playbackBar.playbackBarArtwork);
            if (isPlaying) {
                binding.playbackBar.playbackBarPlayPause.setOnClickListener(
                        v -> viewModel.pause());
            } else {
                binding.playbackBar.playbackBarPlayPause.setOnClickListener(
                        v -> viewModel.play());
            }
        }

    }

}
