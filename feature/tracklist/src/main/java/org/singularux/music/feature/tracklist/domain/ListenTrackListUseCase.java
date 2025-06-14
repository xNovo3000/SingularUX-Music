package org.singularux.music.feature.tracklist.domain;

import android.content.Context;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;

import org.jetbrains.annotations.NotNull;
import org.singularux.music.core.permission.MusicPermission;
import org.singularux.music.core.permission.MusicPermissionManager;
import org.singularux.music.data.library.entity.TrackEntity;
import org.singularux.music.data.library.repository.TrackRepository;
import org.singularux.music.data.library.repository.TrackRepositoryAndroid;
import org.singularux.music.feature.playback.domain.ListenPlaybackInfoUseCase;
import org.singularux.music.feature.playback.model.PlaybackInfo;
import org.singularux.music.feature.tracklist.model.TrackItem;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;

import dagger.hilt.android.qualifiers.ApplicationContext;
import io.reactivex.rxjava3.core.BackpressureStrategy;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.FlowableEmitter;
import io.reactivex.rxjava3.core.FlowableOnSubscribe;
import io.reactivex.rxjava3.functions.BiFunction;
import io.reactivex.rxjava3.functions.Cancellable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import lombok.RequiredArgsConstructor;

public class ListenTrackListUseCase {

    private static final String TAG = "ListenTrackListUseCase";

    private final Context context;
    private final TrackRepository trackRepository;
    private final MusicPermissionManager musicPermissionManager;

    private final ListenPlaybackInfoUseCase listenPlaybackInfoUseCase;

    @Inject
    public ListenTrackListUseCase(
            @ApplicationContext Context context,
            TrackRepository trackRepository,
            MusicPermissionManager musicPermissionManager,
            ListenPlaybackInfoUseCase listenPlaybackInfoUseCase
    ) {
        this.context = context;
        this.trackRepository = trackRepository;
        this.musicPermissionManager = musicPermissionManager;
        this.listenPlaybackInfoUseCase = listenPlaybackInfoUseCase;
    }

    public @NonNull Flowable<List<TrackItem>> get() {
        // Get list of EntityTrack
        Flowable<List<TrackEntity>> tracksEntityFlowable = Flowable
                .create(new TrackItemListOnSubscribe(context, musicPermissionManager),
                        BackpressureStrategy.LATEST)
                .subscribeOn(Schedulers.computation())  // Execute flowable on computation thread
                .observeOn(Schedulers.io())  // Read on IO thread
                .map(o -> trackRepository.getAll());
        // Merge with playbackInfo
        return Flowable.combineLatest(tracksEntityFlowable, listenPlaybackInfoUseCase.get(),
                        new TrackEntityWithPlaybackInfoToTrackItemMapper())
                .subscribeOn(Schedulers.computation());
    }

    @RequiredArgsConstructor
    private static class TrackItemListOnSubscribe implements FlowableOnSubscribe<Object> {

        private final Context context;
        private final MusicPermissionManager musicPermissionManager;

        @Override
        public void subscribe(@NonNull FlowableEmitter<Object> emitter) {
            // Only if has permission
            if (musicPermissionManager.hasPermission(MusicPermission.READ_MUSIC)) {
                Log.d(TAG, "Adding track list observer");
                TrackListObserver observer = new TrackListObserver(emitter);
                // Add observer, force first update and remove when flowable is cancelled
                context.getContentResolver().registerContentObserver(TrackRepositoryAndroid.URI,
                        false, observer);
                observer.onChange(false);
                emitter.setCancellable(new RemoveListenerCancellable(context, observer));
            } else {
                Log.i(TAG, "Missing READ_MUSIC permission");
            }
        }

    }

    @RequiredArgsConstructor
    private static class RemoveListenerCancellable implements Cancellable {

        private final Context context;
        private final TrackListObserver observer;

        @Override
        public void cancel() {
            Log.d(TAG, "Removing track list observer");
            context.getContentResolver().unregisterContentObserver(observer);
        }

    }

    private static class TrackListObserver extends ContentObserver {

        private final FlowableEmitter<Object> emitter;
        private final Object dummy = new Object();

        public TrackListObserver(FlowableEmitter<Object> emitter) {
            super(new Handler(Looper.getMainLooper()));
            this.emitter = emitter;
        }

        @Override
        public void onChange(boolean selfChange) {
            Log.d(TAG, "Change requested");
            emitter.onNext(dummy);
        }

        @Override
        public void onChange(boolean selfChange, @NonNull Collection<Uri> uris, int flags) {
            // Call only once
            this.onChange(selfChange);
        }

    }

    private static class TrackEntityWithPlaybackInfoToTrackItemMapper
            implements BiFunction<List<TrackEntity>, Optional<PlaybackInfo>, List<TrackItem>> {


        @Override
        public @NonNull List<TrackItem> apply(
                @NonNull List<TrackEntity> trackEntityList,
                @NotNull Optional<PlaybackInfo> playbackInfo
        ) {
            // Get current id playing
            final long currentIdPlaying = playbackInfo.map(PlaybackInfo::getId).orElse(-1L);
            return trackEntityList.stream()
                    .map(trackEntity -> TrackItem.builder()
                            .id(trackEntity.getId())
                            .title(trackEntity.getTitle())
                            .artistId(trackEntity.getArtistId())
                            .artistName(trackEntity.getArtistName())
                            .albumId(trackEntity.getAlbumId())
                            .albumName(trackEntity.getAlbumTitle())
                            .artworkUri(trackEntity.getArtworkUri())
                            .duration(trackEntity.getDuration())
                            .isCurrentlyPlaying(currentIdPlaying == trackEntity.getId())
                            .build())
                    .collect(Collectors.toList());
        }

    }

}
