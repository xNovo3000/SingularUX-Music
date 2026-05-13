package org.singularux.music.feature.library.presentation;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.LiveDataReactiveStreams;
import androidx.lifecycle.ViewModel;

import org.singularux.music.feature.library.data.TrackItemData;
import org.singularux.music.feature.library.domain.GetReadMusicPermissionUseCase;
import org.singularux.music.feature.library.domain.ListenTrackListUseCase;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import lombok.Getter;

@HiltViewModel
public class TracksViewModel extends ViewModel {

    private static final String PLAYING_FROM = "tracks";

    private final @Getter String readMusicPermission;
    private final @Getter LiveData<List<TrackItemData>> trackItemDataList;

    @Inject
    public TracksViewModel(GetReadMusicPermissionUseCase getReadMusicPermissionUseCase,
                           ListenTrackListUseCase listenTrackListUseCase) {
        this.readMusicPermission = getReadMusicPermissionUseCase.get();
        this.trackItemDataList = LiveDataReactiveStreams
                .fromPublisher(listenTrackListUseCase.get(PLAYING_FROM));
    }

    public void play(int index) {

    }

    public void addToQueue(int index) {

    }

}
