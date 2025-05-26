package com.ingen.usbapp.utils;

import static android.content.Context.ACTIVITY_SERVICE;

import android.app.ActivityManager;
import android.content.Context;

public class MemoryUtil {

    public static boolean isLowMemory(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        activityManager.getMemoryInfo(memoryInfo);

        return memoryInfo.lowMemory;
    }

    public static double availableMemoryPercentage() {
        Runtime runtime;
        long maxMemory;
        long usedMemory;
        double availableMemoryPercentage = 1.0;
        final double MIN_AVAILABLE_MEMORY_PERCENTAGE = 0.1;
        final int DELAY_TIME = 5 * 1000;

        runtime = Runtime.getRuntime();
        maxMemory = runtime.maxMemory();
        usedMemory = runtime.totalMemory() - runtime.freeMemory();
        availableMemoryPercentage = 1 - (double) usedMemory / maxMemory;

        return availableMemoryPercentage;
    }
}
