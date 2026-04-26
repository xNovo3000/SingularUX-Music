package org.singularux.music.core.threading;

import android.util.Log;

import java.util.concurrent.ThreadFactory;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class BackgroundThreadFactory implements ThreadFactory {

    private static final String TAG = "BackgroundThreadFactory";

    private final String prefix;

    private int sequence = -1;

    @Override
    public Thread newThread(Runnable r) {
        sequence += 1;
        Log.v(TAG, "Requested creation of thread: " + prefix + '-' + sequence);
        return new BackgroundThread(r, prefix + '-' + sequence);
    }

}
