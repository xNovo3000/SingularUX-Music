package org.singularux.music.core.ui.picasso;

import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class PicassoThreadPoolExecutor extends ThreadPoolExecutor {

    private static final int MAX_POOL_SIZE = Math.max(1, Runtime.getRuntime().availableProcessors() / 2);
    private static final int KEEP_ALIVE_SECONDS = 10;

    public PicassoThreadPoolExecutor() {
        super(1, MAX_POOL_SIZE, KEEP_ALIVE_SECONDS, TimeUnit.SECONDS,
                new PriorityBlockingQueue<>(), new PicassoThreadFactory());
    }

}
