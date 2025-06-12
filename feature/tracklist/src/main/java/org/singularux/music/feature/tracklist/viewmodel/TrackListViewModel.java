package org.singularux.music.feature.tracklist.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.LiveDataReactiveStreams;
import androidx.lifecycle.ViewModel;

import org.singularux.music.feature.playback.domain.ListenPlaybackInfoUseCase;
import org.singularux.music.feature.playback.domain.ListenPlaybackPositionUseCase;
import org.singularux.music.feature.playback.model.PlaybackInfo;
import org.singularux.music.feature.playback.model.PlaybackPosition;
import org.singularux.music.feature.tracklist.domain.ListenTrackListUseCase;
import org.singularux.music.feature.tracklist.model.TrackItem;

import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import lombok.Getter;

@HiltViewModel
@Getter
public class TrackListViewModel extends ViewModel {

    private final LiveData<List<TrackItem>> tracks;
    private final LiveData<PlaybackPosition> playbackPosition;
    private final LiveData<Optional<PlaybackInfo>> playbackInfo;

    @Inject
    public TrackListViewModel(
            @NonNull ListenTrackListUseCase listenTrackListUseCase,
            @NonNull ListenPlaybackPositionUseCase listenPlaybackPositionUseCase,
            @NonNull ListenPlaybackInfoUseCase listenPlaybackInfoUseCase
    ) {
        this.tracks = LiveDataReactiveStreams.fromPublisher(listenTrackListUseCase.get());
        this.playbackPosition = LiveDataReactiveStreams
                .fromPublisher(listenPlaybackPositionUseCase.get());
        this.playbackInfo = LiveDataReactiveStreams.fromPublisher(listenPlaybackInfoUseCase.get());
    }

}
