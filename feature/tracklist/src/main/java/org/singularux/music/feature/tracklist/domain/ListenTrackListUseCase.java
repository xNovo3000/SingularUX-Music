package org.singularux.music.feature.tracklist.domain;

import androidx.annotation.NonNull;

import org.singularux.music.feature.tracklist.model.TrackItem;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.rxjava3.core.BackpressureStrategy;
import io.reactivex.rxjava3.core.Flowable;

public class ListenTrackListUseCase {

    @Inject
    public ListenTrackListUseCase() {
    }

    public @NonNull Flowable<List<TrackItem>> get() {
        List<TrackItem> items = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            TrackItem item = new TrackItem(i, "Title " + i, "Artist " + i % 10,
                    null, Duration.ofSeconds(i), i == 4);
            items.add(item);
        }
        return Flowable.create(emitter -> emitter.onNext(items),
                BackpressureStrategy.LATEST);
    }

}
