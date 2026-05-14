package org.singularux.music.feature.library.domain;

import androidx.annotation.NonNull;

import org.singularux.music.data.library.entity.TrackEntity;
import org.singularux.music.data.library.repository.TrackRepository;
import org.singularux.music.feature.library.data.SearchItemData;
import org.singularux.music.feature.library.data.TrackItemData;
import org.singularux.music.feature.playback.data.PlaybackItemInfo;
import org.singularux.music.feature.playback.domain.ListenPlaybackItemInfoUseCase;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.inject.Inject;

import io.reactivex.rxjava3.core.BackpressureStrategy;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.FlowableOnSubscribe;
import io.reactivex.rxjava3.functions.BiFunction;
import io.reactivex.rxjava3.schedulers.Schedulers;
import lombok.RequiredArgsConstructor;

public class GetSearchResultUseCase {

    private static final int QUERY_DEBOUNCE_MS = 100;

    private final TrackRepository trackRepository;
    private final ListenPlaybackItemInfoUseCase listenPlaybackItemInfoUseCase;

    @Inject
    public GetSearchResultUseCase(TrackRepository trackRepository,
                                  ListenPlaybackItemInfoUseCase listenPlaybackItemInfoUseCase) {
        this.trackRepository = trackRepository;
        this.listenPlaybackItemInfoUseCase = listenPlaybackItemInfoUseCase;
    }

    public @NonNull Flowable<List<SearchItemData>> get(
            @NonNull FlowableOnSubscribe<String> emitter) {
        Flowable<List<Object>> searchObjectListFlowable = Flowable
                .create(emitter, BackpressureStrategy.LATEST)
                .subscribeOn(Schedulers.computation(), false)
                .debounce(QUERY_DEBOUNCE_MS, TimeUnit.MILLISECONDS)
                .observeOn(Schedulers.io())
                .map(trackRepository::getAllByTitleLike)
                .observeOn(Schedulers.computation())
                // TODO: Just an optimization, in the future this will map
                //  TrackEntity, AlbumEntity, ArtistEntity and PlaylistEntity
                .map(trackEntityList -> trackEntityList.stream()
                        .map(trackEntity -> (Object) trackEntity)
                        .collect(Collectors.toList()));
        return Flowable.combineLatest(searchObjectListFlowable, listenPlaybackItemInfoUseCase.get(),
                        new SearchWithPlaybackInfoCombiner())
                .subscribeOn(Schedulers.computation());
    }

    private static final class SearchWithPlaybackInfoCombiner implements BiFunction<
            List<Object>, Optional<PlaybackItemInfo>, List<SearchItemData>> {

        private static final String PLAYING_FROM = "search";

        @Override
        public @NonNull List<SearchItemData> apply(
                @NonNull List<Object> objects,
                @NonNull Optional<PlaybackItemInfo> maybePlaybackItemInfo) {
            // Extract the playing track id only if the playingFrom token matches
            long currentPlayingTrackId = maybePlaybackItemInfo
                    .filter(playbackItemInfo -> Objects
                            .equals(playbackItemInfo.getPlayingFrom(), PLAYING_FROM))
                    .map(PlaybackItemInfo::getId)
                    .orElse(-1L);
            // Create the final list
            return objects.stream()
                    .map(new SearchObjectMapper(currentPlayingTrackId))
                    .collect(Collectors.toList());
        }
    }

    @RequiredArgsConstructor
    private static final class SearchObjectMapper implements Function<Object, SearchItemData> {

        private final long currentPlayingTrackId;

        @Override
        public @NonNull SearchItemData apply(@NonNull Object object) {
            if (object instanceof TrackEntity) {
                TrackEntity trackEntity = (TrackEntity) object;
                return new SearchItemData.Track(trackEntity.getId(), trackEntity.getTitle(),
                        trackEntity.getArtistId(), trackEntity.getArtistName(),
                        trackEntity.getAlbumId(), trackEntity.getAlbumTitle(),
                        trackEntity.getDuration(), trackEntity.getArtworkPath(),
                        trackEntity.getId() == currentPlayingTrackId);
            }
            throw new IllegalArgumentException("Found invalid object " + object);
        }

    }

}
