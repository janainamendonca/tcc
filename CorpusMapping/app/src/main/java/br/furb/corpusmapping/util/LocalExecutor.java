package br.furb.corpusmapping.util;

import android.os.*;
import android.os.Process;
import android.support.annotation.NonNull;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by Janaina on 11/10/2015.
 */
public class LocalExecutor extends ThreadPoolExecutor {

    private static LocalExecutor instance;

    public static ExecutorService getExecutor() {
        if (instance == null) {
            final int numberCores = Runtime.getRuntime().availableProcessors();
            instance = new LocalExecutor(numberCores * 2 + 1);
        }
        return instance;
    }

    public LocalExecutor(int maxThreads) {
        super(0, maxThreads, 30, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(), new LocalThreadFactory());
    }

    private static class LocalThreadFactory implements ThreadFactory {
        @Override
        public Thread newThread(@NonNull Runnable runnable) {
            return new LocalThread(runnable);
        }
    }

    private static class LocalThread extends Thread {
        private LocalThread(Runnable runnable) {
            super(runnable);
        }

        @Override
        public void run() {
            android.os.Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
            super.run();
        }
    }
}
