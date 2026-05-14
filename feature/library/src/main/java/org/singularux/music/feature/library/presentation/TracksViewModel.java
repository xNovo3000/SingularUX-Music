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

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.rxjava3.core.FlowableEmitter;
import io.reactivex.rxjava3.core.FlowableOnSubscribe;
import lombok.Getter;

@HiltViewModel
public class TracksViewModel extends ViewModel {

    private final @Getter String readMusicPermission;
    private final @Getter LiveData<List<TrackItemData>> trackItemDataList;

    private final SearchQueryEmitter searchQueryEmitter;
    private final @Getter LiveData<List<SearchItemData>> searchItemDataList;

    @Inject
    public TracksViewModel(GetReadMusicPermissionUseCase getReadMusicPermissionUseCase,
                           ListenTrackListUseCase listenTrackListUseCase,
                           GetSearchResultUseCase getSearchResultUseCase) {
        this.readMusicPermission = getReadMusicPermissionUseCase.get();
        this.trackItemDataList = LiveDataReactiveStreams
                .fromPublisher(listenTrackListUseCase.get());
        // Search
        this.searchQueryEmitter = new SearchQueryEmitter();
        this.searchItemDataList = LiveDataReactiveStreams
                .fromPublisher(getSearchResultUseCase.get(this.searchQueryEmitter));
        addCloseable(this.searchQueryEmitter);
    }

    public void playFromTrackList(int index) {

    }

    public void playFromSearchList(int index) {

    }

    public void addToQueueFromTrackList(int index) {

    }

    public void addToQueueFromSearchList(int index) {

    }

    public void updateSearchQuery(@NonNull String query) {
        searchQueryEmitter.update(query);
    }

    public static final class SearchQueryEmitter
            implements FlowableOnSubscribe<String>, AutoCloseable {

        private @Nullable FlowableEmitter<String> emitter = null;

        @Override
        public void subscribe(@NonNull FlowableEmitter<String> emitter) {
            this.emitter = emitter;
        }

        public void update(@NonNull String query) {
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
