package org.singularux.music.feature.library.presentation;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.LiveDataReactiveStreams;
import androidx.lifecycle.ViewModel;

import org.singularux.music.feature.library.data.SearchItemData;
import org.singularux.music.feature.library.data.TrackItemData;
import org.singularux.music.feature.library.domain.GetReadMusicPermissionUseCase;
import org.singularux.music.feature.library.domain.GetSearchResultUseCase;
import org.singularux.music.feature.library.domain.ListenTrackListUseCase;
import org.singularux.music.feature.playback.data.PlayerAction;
import org.singularux.music.feature.playback.data.TimelineAction;
import org.singularux.music.feature.playback.domain.OnPlayerActionUseCase;
import org.singularux.music.feature.playback.domain.OnTimelineActionUseCase;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.rxjava3.core.FlowableEmitter;
import io.reactivex.rxjava3.core.FlowableOnSubscribe;
import lombok.Getter;

@HiltViewModel
public class TracksViewModel extends ViewModel {

    private final OnTimelineActionUseCase onTimelineActionUseCase;
    private final OnPlayerActionUseCase onPlayerActionUseCase;

    private final @Getter String readMusicPermission;
    private final @Getter LiveData<List<TrackItemData>> trackItemDataList;

    /* Search */
    private final SearchQueryEmitter searchQueryEmitter;
    private final @Getter LiveData<List<SearchItemData>> searchItemDataList;

    @Inject
    public TracksViewModel(GetReadMusicPermissionUseCase getReadMusicPermissionUseCase,
                           ListenTrackListUseCase listenTrackListUseCase,
                           GetSearchResultUseCase getSearchResultUseCase,
                           OnTimelineActionUseCase onTimelineActionUseCase,
                           OnPlayerActionUseCase onPlayerActionUseCase) {
        this.onTimelineActionUseCase = onTimelineActionUseCase;
        this.onPlayerActionUseCase = onPlayerActionUseCase;
        this.readMusicPermission = getReadMusicPermissionUseCase.get();
        this.trackItemDataList = LiveDataReactiveStreams
                .fromPublisher(listenTrackListUseCase.get());
        /* Search */
        this.searchQueryEmitter = new SearchQueryEmitter();
        this.searchItemDataList = LiveDataReactiveStreams
                .fromPublisher(getSearchResultUseCase.get(this.searchQueryEmitter));
        addCloseable(this.searchQueryEmitter);
    }

    /* Play */

    public void playFromTrackList(int index) {
        List<TrackItemData> current = trackItemDataList.getValue();
        if (current != null) {
            TimelineAction action = new TimelineAction.ReplaceMediaItems(
                    current.stream()
                            .map(new TrackItemData.ToTimelineMediaItemMapper("tracks", false))
                            .collect(Collectors.toList()),
                    index, false);
            onTimelineActionUseCase.run(action);
            onPlayerActionUseCase.run(new PlayerAction.Play());
        }
    }

    public void playFromSearchList(int index) {
        List<SearchItemData> current = searchItemDataList.getValue();
        if (current != null) {
            TimelineAction action = new TimelineAction.ReplaceMediaItems(
                    current.stream()
                            .filter(new SearchItemData.Track.Filter())
                            .map(searchItemData -> (SearchItemData.Track) searchItemData)
                            .map(new SearchItemData.Track.ToQueueItemMapper(false))
                            .collect(Collectors.toList()),
                    index, false);
            onTimelineActionUseCase.run(action);
            onPlayerActionUseCase.run(new PlayerAction.Play());
        }
    }

    public void addToQueueFromTrackList(int index) {
        List<TrackItemData> current = trackItemDataList.getValue();
        if (current != null) {
            TrackItemData itemData = current.get(index);
            TrackItemData.ToTimelineMediaItemMapper mapper =
                    new TrackItemData.ToTimelineMediaItemMapper("tracks", true);
            TimelineAction action = new TimelineAction.AddToCustomQueue(mapper.apply(itemData));
            onTimelineActionUseCase.run(action);
        }
    }

    public void addToQueueFromSearchList(int index) {
        List<SearchItemData> current = searchItemDataList.getValue();
        if (current != null) {
            SearchItemData itemData = current.get(index);
            SearchItemData.Track.Filter filter = new SearchItemData.Track.Filter();
            if (filter.test(itemData)) {
                SearchItemData.Track trackItemData = (SearchItemData.Track) itemData;
                SearchItemData.Track.ToQueueItemMapper mapper =
                        new SearchItemData.Track.ToQueueItemMapper(true);
                TimelineAction action = new TimelineAction
                        .AddToCustomQueue(mapper.apply(trackItemData));
                onTimelineActionUseCase.run(action);
            }
        }
    }

    /* Search */

    public void updateSearchQuery(@NonNull String query) {
        searchQueryEmitter.emit(query);
    }

    public static final class SearchQueryEmitter
            implements FlowableOnSubscribe<String>, AutoCloseable {

        private @Nullable FlowableEmitter<String> emitter = null;

        @Override
        public void subscribe(@NonNull FlowableEmitter<String> emitter) {
            this.emitter = emitter;
        }

        public void emit(@NonNull String query) {
            if (emitter != null)
                emitter.onNext(query);
        }

        @Override
        public void close() {
            if (emitter != null)
                emitter.onComplete();
        }

    }

}
