package org.singularux.music.feature.tracklist.ui;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.LiveDataReactiveStreams;
import androidx.lifecycle.ViewModel;
import androidx.media3.common.MediaItem;
import androidx.media3.session.MediaController;

import org.singularux.music.feature.playback.domain.model.PlaybackState;
import org.singularux.music.feature.playback.domain.usecase.ListenPlaybackStateUseCase;
import org.singularux.music.core.playback.MusicControllerFacade;
import org.singularux.music.feature.playback.domain.usecase.ListenPlaybackItemInfoUseCase;
import org.singularux.music.feature.playback.domain.usecase.ListenPlaybackPositionUseCase;
import org.singularux.music.feature.playback.domain.model.PlaybackItemInfo;
import org.singularux.music.feature.playback.domain.model.PlaybackPosition;
import org.singularux.music.feature.tracklist.domain.GetTrackListFilteredByNameUseCase;
import org.singularux.music.feature.tracklist.domain.ListenTrackListUseCase;
import org.singularux.music.feature.tracklist.ui.list.item.TrackListItem;
import org.singularux.music.feature.tracklist.ui.search.item.SearchListItem;
import org.singularux.music.feature.tracklist.ui.search.item.SearchListItemTrack;
import org.singularux.music.feature.tracklist.util.SearchListItemTrackToMediaItemMapper;
import org.singularux.music.feature.tracklist.util.TrackListItemToMediaItemMapper;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.rxjava3.core.FlowableEmitter;
import io.reactivex.rxjava3.core.FlowableOnSubscribe;
import lombok.Getter;

@HiltViewModel
public class TrackListViewModel extends ViewModel {

    private final MusicControllerFacade musicControllerFacade;
    private final QueryEmitter queryEmitter;

    private final @Getter LiveData<List<TrackListItem>> trackList;
    private final @Getter LiveData<List<SearchListItem>> searchTrackList;

    private final @Getter LiveData<PlaybackPosition> playbackPosition;
    private final @Getter LiveData<Optional<PlaybackItemInfo>> playbackItemInfo;
    private final @Getter LiveData<PlaybackState> playbackState;

    @Inject
    public TrackListViewModel(
            @NonNull MusicControllerFacade musicControllerFacade,
            @NonNull ListenTrackListUseCase listenTrackListUseCase,
            @NonNull GetTrackListFilteredByNameUseCase getTrackListFilteredByNameUseCase,
            @NonNull ListenPlaybackPositionUseCase listenPlaybackPositionUseCase,
            @NonNull ListenPlaybackItemInfoUseCase listenPlaybackItemInfoUseCase,
            @NonNull ListenPlaybackStateUseCase listenPlaybackStateUseCase
    ) {
        this.musicControllerFacade = musicControllerFacade;
        this.queryEmitter = new QueryEmitter();
        // Lists
        this.trackList = LiveDataReactiveStreams
                .fromPublisher(listenTrackListUseCase.get("track_list"));
        this.searchTrackList = LiveDataReactiveStreams
                .fromPublisher(getTrackListFilteredByNameUseCase.get(queryEmitter));
        // Playback
        this.playbackPosition = LiveDataReactiveStreams
                .fromPublisher(listenPlaybackPositionUseCase.get());
        this.playbackItemInfo = LiveDataReactiveStreams
                .fromPublisher(listenPlaybackItemInfoUseCase.get());
        this.playbackState = LiveDataReactiveStreams
                .fromPublisher(listenPlaybackStateUseCase.get());
    }

    public void playFromSpecificTrackListIndex(int index) {
        List<TrackListItem> currentList = trackList.getValue();
        if (currentList != null && index < currentList.size()) {
            List<MediaItem> mediaItems = currentList.stream()
                    .map(new TrackListItemToMediaItemMapper("track_list"))
                    .collect(Collectors.toList());
            MediaController mediaController = musicControllerFacade.requireMediaController();
            mediaController.clearMediaItems();
            mediaController.addMediaItems(mediaItems);
            mediaController.seekTo(index, 0);
            mediaController.play();
        }
    }

    public void playSpecificSearchListItemTrack(SearchListItemTrack item) {
        SearchListItemTrackToMediaItemMapper mapper =
                new SearchListItemTrackToMediaItemMapper();
        MediaItem mediaItem = mapper.apply(item);
        MediaController mediaController = musicControllerFacade.requireMediaController();
        mediaController.clearMediaItems();
        mediaController.addMediaItem(mediaItem);
        mediaController.play();
    }

    public void playShuffled() {
        List<TrackListItem> currentList = trackList.getValue();
        if (currentList != null) {
            List<MediaItem> mediaItems = currentList.stream()
                    .map(new TrackListItemToMediaItemMapper("track_list"))
                    .collect(Collectors.toList());
            Collections.shuffle(mediaItems);
            MediaController mediaController = musicControllerFacade.requireMediaController();
            mediaController.clearMediaItems();
            mediaController.addMediaItems(mediaItems);
            mediaController.seekTo(0, 0);
            mediaController.play();
        }
    }

    public void play() {
        musicControllerFacade.requireMediaController().play();
    }

    public void pause() {
        musicControllerFacade.requireMediaController().pause();
    }

    public void onSearchQueryChanged(@NonNull String query) {
        if (queryEmitter.emitter != null) {
            queryEmitter.emitter.onNext(query);
        }
    }

    private static final class QueryEmitter implements FlowableOnSubscribe<String> {

        private @Nullable FlowableEmitter<String> emitter = null;

        @Override
        public void subscribe(@NonNull FlowableEmitter<String> emitter) {
            this.emitter = emitter;
        }

    }

}
