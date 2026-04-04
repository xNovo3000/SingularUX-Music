package org.singularux.music.core;

import android.os.Process;
import android.util.Log;

public class BackgroundThread extends Thread {

    private static final String TAG = "BackgroundThread";

    public BackgroundThread(Runnable r, String name) {
        super(r, name);
    }

    @Override
    public void run() {
        Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
        Log.d(TAG, "Starting " + getName());
        super.run();
        Log.v(TAG, "Stopping " + getName());
    }

}
