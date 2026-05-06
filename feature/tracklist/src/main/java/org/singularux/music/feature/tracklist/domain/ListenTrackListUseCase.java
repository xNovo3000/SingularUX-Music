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
import org.singularux.music.feature.playback.domain.ListenPlaybackItemInfoUseCase;
import org.singularux.music.feature.playback.data.PlaybackItemInfo;
import org.singularux.music.feature.tracklist.ui.list.item.TrackListItem;
import org.singularux.music.feature.tracklist.util.TrackEntityToTrackListItemMapper;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
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

    private final ListenPlaybackItemInfoUseCase listenPlaybackItemInfoUseCase;

    @Inject
    public ListenTrackListUseCase(
            @ApplicationContext Context context,
            TrackRepository trackRepository,
            MusicPermissionManager musicPermissionManager,
            ListenPlaybackItemInfoUseCase listenPlaybackItemInfoUseCase
    ) {
        this.context = context;
        this.trackRepository = trackRepository;
        this.musicPermissionManager = musicPermissionManager;
        this.listenPlaybackItemInfoUseCase = listenPlaybackItemInfoUseCase;
    }

    public @NonNull Flowable<List<TrackListItem>> get(@NonNull String nowPlayingToken) {
        // Get list of EntityTrack
        Flowable<List<TrackEntity>> tracksEntityFlowable = Flowable
                .create(new TrackEntityEmitter(context, musicPermissionManager),
                        BackpressureStrategy.LATEST)
                .subscribeOn(Schedulers.computation())  // Execute flowable on computation thread
                .observeOn(Schedulers.io())  // Read on IO thread
                .map(o -> trackRepository.getAll());
        // Merge with playbackInfo
        return Flowable.combineLatest(tracksEntityFlowable, listenPlaybackItemInfoUseCase.get(),
                        new TrackEntityWithPlaybackInfoToTrackListItemMapper(nowPlayingToken))
                .subscribeOn(Schedulers.computation());
    }

    @RequiredArgsConstructor
    private static final class TrackEntityEmitter implements FlowableOnSubscribe<Object> {

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
    private static final class RemoveListenerCancellable implements Cancellable {

        private final Context context;
        private final ContentObserver observer;

        @Override
        public void cancel() {
            Log.d(TAG, "Removing track list observer");
            context.getContentResolver().unregisterContentObserver(observer);
        }

    }

    private static final class TrackListObserver extends ContentObserver {

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

    @RequiredArgsConstructor
    private static final class TrackEntityWithPlaybackInfoToTrackListItemMapper
            implements BiFunction<List<TrackEntity>, Optional<PlaybackItemInfo>, List<TrackListItem>> {

        private final String nowPlayingToken;

        @Override
        public @NonNull List<TrackListItem> apply(
                @NonNull List<TrackEntity> trackEntityList,
                @NotNull Optional<PlaybackItemInfo> playbackInfo
        ) {
            // Get current id playing only if token matches
            final long currentIdPlaying = playbackInfo
                    .filter(i -> Objects.equals(i.getPlayingFrom(), nowPlayingToken))
                    .map(PlaybackItemInfo::getId)
                    .orElse(-1L);
            return trackEntityList.stream()
                    .map(new TrackEntityToTrackListItemMapper(currentIdPlaying))
                    .collect(Collectors.toList());
        }

    }

}
