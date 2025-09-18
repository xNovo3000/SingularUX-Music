package org.singularux.music.core.ui.picasso;

import androidx.annotation.NonNull;

import com.squareup.picasso.Downloader;

import java.io.IOException;

import okhttp3.Request;
import okhttp3.Response;

public class NoOpDownloader implements Downloader {

    @Override
    public @NonNull Response load(@NonNull Request request) throws IOException {
        throw new IOException("Downloading images from internet is disabled");
    }

    @Override
    public void shutdown() {}

}
