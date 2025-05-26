package com.ingen.usbapp.executor;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ExecutorManager {

    static final int DEFAULT_THREAD_POOL_SIZE = 3;

    private static ExecutorService executorService = Executors.newFixedThreadPool(DEFAULT_THREAD_POOL_SIZE);

    private static ExecutorService executorWebService = Executors.newFixedThreadPool(3);

    public static Executor getExecutor() {
        return executorService;
    }

    public static Executor getAPIExecutor() {
        return executorWebService;
    }
}
