package org.singularux.music.feature.tracklist.domain.usecase;

import androidx.annotation.NonNull;

import org.singularux.music.data.library.repository.TrackRepository;
import org.singularux.music.feature.tracklist.domain.model.TrackItem;

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

    public Flowable<List<TrackItem>> get(@NonNull FlowableOnSubscribe<String> queryEmitter) {
        return Flowable.create(queryEmitter, BackpressureStrategy.LATEST)
                .subscribeOn(Schedulers.computation())
                .debounce(250, TimeUnit.MILLISECONDS)
                .observeOn(Schedulers.io())
                .map(trackRepository::getAllByTitleLike)
                .observeOn(Schedulers.computation())
                .map(trackEntities -> trackEntities.stream()
                        .map(new TrackEntityToTrackItemMapper(-1L))
                        .collect(Collectors.toList()));
    }

}
