package org.singularux.music.feature.tracklist.domain;

import androidx.annotation.NonNull;

import org.singularux.music.data.library.repository.TrackRepository;
import org.singularux.music.feature.tracklist.ui.search.item.SearchListItem;
import org.singularux.music.feature.tracklist.util.TrackEntityToSearchListItemMapper;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.inject.Inject;

import io.reactivex.rxjava3.core.BackpressureStrategy;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.FlowableOnSubscribe;
import io.reactivex.rxjava3.schedulers.Schedulers;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(onConstructor_ = @Inject)
public class GetTrackListFilteredByNameUseCase {

    private final TrackRepository trackRepository;

    public Flowable<List<SearchListItem>> get(@NonNull FlowableOnSubscribe<String> queryEmitter) {
        // TODO: In the future the query must give results about artists and albums
        // For now, we are giving only tracks
        return Flowable.create(queryEmitter, BackpressureStrategy.LATEST)
                .subscribeOn(Schedulers.computation())
                .debounce(250, TimeUnit.MILLISECONDS)
                .observeOn(Schedulers.io())
                .map(trackRepository::getAllByTitleLike)
                .observeOn(Schedulers.computation())
                .map(trackEntities -> trackEntities.stream()
                        .map(new TrackEntityToSearchListItemMapper())
                        .collect(Collectors.toList()));
    }

}
