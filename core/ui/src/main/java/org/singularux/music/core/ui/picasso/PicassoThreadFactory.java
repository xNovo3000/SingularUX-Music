package org.singularux.music.core.ui.picasso;

import static android.os.Process.THREAD_PRIORITY_BACKGROUND;

import android.os.Process;

import java.util.concurrent.ThreadFactory;

public final class PicassoThreadFactory implements ThreadFactory {

    private int sequence = -1;

    @Override
    public Thread newThread(Runnable r) {
        sequence += 1;
        return new PicassoThread(r, sequence);
    }

    private static final class PicassoThread extends Thread {

        public PicassoThread(Runnable r, int sequence) {
            super(r, "Picasso-" + sequence);
        }

        @Override
        public void run() {
            Process.setThreadPriority(THREAD_PRIORITY_BACKGROUND);
            super.run();
        }

    }

}
