package com.ingen.usbapp;

import android.content.Context;

import com.ingen.usbapp.utils.SharedPrefs;


public class Configuration {

    private static final String KEY_APP_RUNNING = "App_Running";

    public static void saveAppRunning(Context context, boolean isRunning) {
        SharedPrefs.put(context, KEY_APP_RUNNING, isRunning);
    }

    public static boolean getAppRunning(Context context) {
        return SharedPrefs.get(context, KEY_APP_RUNNING, Boolean.class);
    }
}
