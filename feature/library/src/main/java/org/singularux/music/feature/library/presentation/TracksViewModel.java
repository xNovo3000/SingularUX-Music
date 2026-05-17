package org.singularux.music.feature.library.presentation;

import android.util.Log;

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
import java.util.Random;
import java.util.stream.Collectors;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.rxjava3.core.FlowableEmitter;
import io.reactivex.rxjava3.core.FlowableOnSubscribe;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import lombok.Getter;

@HiltViewModel
public class TracksViewModel extends ViewModel {

    private static final String TAG = "TracksViewModel";

    private final OnTimelineActionUseCase onTimelineActionUseCase;
    private final OnPlayerActionUseCase onPlayerActionUseCase;

    private final @Getter String readMusicPermission;
    private final @Getter LiveData<List<TrackItemData>> trackItemDataList;

    /* Search */
    private final SearchQueryEmitter searchQueryEmitter;
    private final @Getter LiveData<List<SearchItemData>> searchItemDataList;

    private @Nullable Disposable currentAction = null;

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
        Log.d(TAG, "Executing playFromTrackList with index " + index);
        List<TrackItemData> current = trackItemDataList.getValue();
        if (current != null && index < current.size()) {
            if (currentAction != null && !currentAction.isDisposed()) {
                currentAction.dispose();
            }
            currentAction = Observable.just(current)
                    .observeOn(Schedulers.computation())
                    .map(list -> list.stream()
                            .map(new TrackItemData.ToTrackDtoMapper("tracks"))
                            .collect(Collectors.toList()))
                    .flatMapCompletable(list -> {
                        TimelineAction action = new TimelineAction.ReplaceMediaItems(
                                list, index, false);
                        return onTimelineActionUseCase.run(action);
                    })
                    .doOnComplete(() -> onPlayerActionUseCase.run(new PlayerAction.Play()))
                    .subscribe();
        }
    }

    public void playFromSearchList(int index) {
        Log.d(TAG, "Executing playFromSearchList with index " + index);
        List<SearchItemData> current = searchItemDataList.getValue();
        if (current != null && index < current.size()) {
            if (currentAction != null && !currentAction.isDisposed()) {
                currentAction.dispose();
            }
            currentAction = Observable.just(current)
                    .observeOn(Schedulers.computation())
                    .map(list -> list.stream()
                            .filter(new SearchItemData.Track.Filter())
                            .map(new SearchItemData.Track.MapAfterFilter())
                            .map(new SearchItemData.Track.ToTrackDtoMapper())
                            .collect(Collectors.toList()))
                    .flatMapCompletable(list -> {
                        TimelineAction action = new TimelineAction.ReplaceMediaItems(
                                list, index, false);
                        return onTimelineActionUseCase.run(action);
                    })
                    .doOnComplete(() -> onPlayerActionUseCase.run(new PlayerAction.Play()))
                    .subscribe();
        }
    }

    public void playShuffled() {
        Log.d(TAG, "Executing playShuffled");
        List<TrackItemData> current = trackItemDataList.getValue();
        if (current != null) {
            if (currentAction != null && !currentAction.isDisposed()) {
                currentAction.dispose();
            }
            int index = new Random().nextInt(current.size());
            currentAction = Observable.just(current)
                    .observeOn(Schedulers.computation())
                    .map(list -> list.stream()
                            .map(new TrackItemData.ToTrackDtoMapper("tracks"))
                            .collect(Collectors.toList()))
                    .flatMapCompletable(list -> {
                        TimelineAction action = new TimelineAction.ReplaceMediaItems(
                                list, index, true);
                        return onTimelineActionUseCase.run(action);
                    })
                    .doOnComplete(() -> onPlayerActionUseCase.run(new PlayerAction.Play()))
                    .subscribe();
        }
    }

    public void addToQueueFromTrackList(int index) {
        Log.d(TAG, "Executing addToQueueFromTrackList with index " + index);
        List<TrackItemData> current = trackItemDataList.getValue();
        if (current != null && index < current.size()) {
            if (currentAction != null && !currentAction.isDisposed()) {
                currentAction.dispose();
            }
            TrackItemData.ToTrackDtoMapper mapper =
                    new TrackItemData.ToTrackDtoMapper("tracks");
            currentAction = Observable.just(current.get(index))
                    .observeOn(Schedulers.computation())
                    .map(mapper::apply)
                    .flatMapCompletable(item -> {
                        TimelineAction action = new TimelineAction.AddToCustomQueue(item);
                        return onTimelineActionUseCase.run(action);
                    })
                    .subscribe();
        }
    }

    public void addToQueueFromSearchList(int index) {
        Log.d(TAG, "Executing addToQueueFromSearchList with index " + index);
        List<SearchItemData> current = searchItemDataList.getValue();
        if (current != null && index < current.size()) {
            if (currentAction != null && !currentAction.isDisposed()) {
                currentAction.dispose();
            }
            SearchItemData.Track.Filter filter =
                    new SearchItemData.Track.Filter();
            SearchItemData.Track.MapAfterFilter mapAfterFilter =
                    new SearchItemData.Track.MapAfterFilter();
            SearchItemData.Track.ToTrackDtoMapper mapper =
                    new SearchItemData.Track.ToTrackDtoMapper();
            currentAction = Observable.just(current.get(index))
                    .observeOn(Schedulers.computation())
                    .filter(filter::test)
                    .map(mapAfterFilter::apply)
                    .map(mapper::apply)
                    .flatMapCompletable(item -> {
                        TimelineAction action = new TimelineAction.AddToCustomQueue(item);
                        return onTimelineActionUseCase.run(action);
                    })
                    .subscribe();
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
