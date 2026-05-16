package org.singularux.music.feature.library.domain;

import android.content.Context;
import android.database.ContentObserver;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;

import org.singularux.music.data.library.DataLibraryUtils;
import org.singularux.music.data.library.entity.TrackEntity;
import org.singularux.music.data.library.repository.TrackRepository;
import org.singularux.music.feature.library.data.TrackItemData;
import org.singularux.music.feature.playback.data.PlaybackItemInfo;
import org.singularux.music.feature.playback.domain.ListenPlaybackItemInfoUseCase;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.inject.Inject;

import dagger.hilt.android.qualifiers.ApplicationContext;
import io.reactivex.rxjava3.core.BackpressureStrategy;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.FlowableEmitter;
import io.reactivex.rxjava3.core.FlowableOnSubscribe;
import io.reactivex.rxjava3.functions.BiFunction;
import io.reactivex.rxjava3.schedulers.Schedulers;
import lombok.RequiredArgsConstructor;

public class ListenTrackListUseCase {

    private final Context context;
    private final TrackRepository trackRepository;
    private final ListenPlaybackItemInfoUseCase listenPlaybackItemInfoUseCase;

    @Inject
    public ListenTrackListUseCase(@ApplicationContext Context context,
                                  TrackRepository trackRepository,
                                  ListenPlaybackItemInfoUseCase listenPlaybackItemInfoUseCase) {
        this.context = context;
        this.trackRepository = trackRepository;
        this.listenPlaybackItemInfoUseCase = listenPlaybackItemInfoUseCase;
    }

    public @NonNull Flowable<List<TrackItemData>> get() {
        Flowable<List<TrackEntity>> trackEntityListFlowable = Flowable
                .create(new TrackListUpdateEmitter(context), BackpressureStrategy.LATEST)
                .subscribeOn(Schedulers.computation(), false)
                .observeOn(Schedulers.io())
                .map(o -> trackRepository.getAll());
        return Flowable.combineLatest(trackEntityListFlowable, listenPlaybackItemInfoUseCase.get(),
                        new TrackListWithPlaybackInfoCombiner())
                .subscribeOn(Schedulers.computation());
    }

    @RequiredArgsConstructor
    private static final class TrackListUpdateEmitter implements FlowableOnSubscribe<Object> {

        private final Context context;

        @Override
        public void subscribe(@NonNull FlowableEmitter<Object> emitter) {
            // Register observer, force first update and unregister when finishing
            TrackListUpdateObserver observer = new TrackListUpdateObserver(emitter);
            context.getContentResolver().registerContentObserver(
                    DataLibraryUtils.TRACKS_URI, false, observer);
            observer.onChange(true);
            emitter.setCancellable(() -> context.getContentResolver()
                    .unregisterContentObserver(observer));
        }

    }

    private static final class TrackListUpdateObserver extends ContentObserver {

        private final FlowableEmitter<Object> emitter;

        public TrackListUpdateObserver(FlowableEmitter<Object> emitter) {
            super(new Handler(Looper.getMainLooper()));
            this.emitter = emitter;
        }

        @Override
        public void onChange(boolean selfChange) {
            emitter.onNext(new Object());
        }

    }

    private static final class TrackListWithPlaybackInfoCombiner implements BiFunction<
            List<TrackEntity>, Optional<PlaybackItemInfo>, List<TrackItemData>> {

        private static final String PLAYING_FROM = "tracks";

        @Override
        public @NonNull List<TrackItemData> apply(
                @NonNull List<TrackEntity> trackEntities,
                @NonNull Optional<PlaybackItemInfo> maybePlaybackItemInfo) {
            // Extract the playing track id only if the playingFrom token matches
            long currentPlayingTrackId = maybePlaybackItemInfo
                    .filter(playbackItemInfo -> Objects
                            .equals(playbackItemInfo.getPlayingFrom(), PLAYING_FROM))
                    .map(PlaybackItemInfo::getId)
                    .orElse(-1L);
            // Create the final list
            return trackEntities.stream()
                    .map(new TrackEntityMapper(currentPlayingTrackId))
                    .collect(Collectors.toList());
        }
    }

    @RequiredArgsConstructor
    private static final class TrackEntityMapper implements Function<TrackEntity, TrackItemData> {

        private final long currentPlayingTrackId;

        @Override
        public @NonNull TrackItemData apply(@NonNull TrackEntity trackEntity) {
            return new TrackItemData(trackEntity.getId(), trackEntity.getTitle(),
                    trackEntity.getArtistId(), trackEntity.getArtistName(),
                    trackEntity.getAlbumId(), trackEntity.getAlbumTitle(),
                    trackEntity.getDuration(), trackEntity.getArtworkPath(),
                    trackEntity.getId() == currentPlayingTrackId);
        }

    }

}
