package org.singularux.music.core.thumbnail;

import androidx.annotation.NonNull;

import com.squareup.picasso.Downloader;

import java.io.IOException;

import okhttp3.Request;
import okhttp3.Response;

public class PicassoNoOpDownloader implements Downloader {

    @Override
    public @NonNull Response load(@NonNull Request request) throws IOException {
        throw new IOException("Cannot process internet request");
    }

    @Override
    public void shutdown() {}

}
