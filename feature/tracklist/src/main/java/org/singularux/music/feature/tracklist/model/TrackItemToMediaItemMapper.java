package org.singularux.music.feature.tracklist.model;

import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;

import androidx.annotation.NonNull;
import androidx.media3.common.MediaItem;
import androidx.media3.common.MediaMetadata;

import java.util.function.Function;

import javax.inject.Inject;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(onConstructor_ = {@Inject})
public class TrackItemToMediaItemMapper implements Function<TrackItem, MediaItem> {

    @Override
    public MediaItem apply(@NonNull TrackItem trackItem) {
        Bundle extras = new Bundle();
        extras.putInt("id", trackItem.getId());
        Uri uri = Uri.withAppendedPath(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                String.valueOf(trackItem.getId()));
        MediaMetadata mediaMetadata = new MediaMetadata.Builder()
                .setTitle(trackItem.getTitle())
                .setArtist(trackItem.getArtistsName())
                .setArtworkUri(trackItem.getArtworkUri())
                .setExtras(extras)
                .build();
        return new MediaItem.Builder()
                .setUri(uri)
                .setMediaMetadata(mediaMetadata)
                .build();
    }

}
