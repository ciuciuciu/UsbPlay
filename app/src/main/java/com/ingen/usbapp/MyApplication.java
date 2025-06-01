package com.ingen.usbapp;

import android.app.Application;
import android.content.Intent;

import com.ingen.usbapp.services.RestartService;
import com.ingen.usbapp.utils.MyExceptionHandler;
import com.ingen.usbapp.utils.MyLifecycleHandler;


public class MyApplication extends Application {

    private static MyApplication instance;

    @Override
    public synchronized void onCreate() {
        super.onCreate();
        instance = this;

        registerActivityLifecycleCallbacks(new MyLifecycleHandler());

        Thread.setDefaultUncaughtExceptionHandler(new MyExceptionHandler(this, StartupActivity.class));
    }

    public static void restartApplication() {
        try {
            Intent mServiceIntent = new Intent(instance, RestartService.class);
            instance.startService(mServiceIntent);
        } catch (Error error) {
        }
    }
}
