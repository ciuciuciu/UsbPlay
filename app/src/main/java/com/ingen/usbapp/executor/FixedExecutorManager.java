package com.ingen.usbapp.executor;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FixedExecutorManager {

    static final int DEFAULT_THREAD_POOL_SIZE = 5;

    private static ExecutorService executorService = Executors.newFixedThreadPool(DEFAULT_THREAD_POOL_SIZE);

    private static ExecutorService mUsbExecutorService = Executors.newSingleThreadExecutor();

    public static Executor getExecutor() {
        return executorService;
    }

    public static ExecutorService getUsbExecutorService() {
        return mUsbExecutorService;
    }
}
