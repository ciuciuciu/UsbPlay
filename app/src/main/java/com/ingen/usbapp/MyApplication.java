package com.ingen.usbapp;

import android.app.Application;

import com.ingen.usbapp.utils.MyExceptionHandler;
import com.ingen.usbapp.utils.MyLifecycleHandler;


public class MyApplication extends Application {

    @Override
    public synchronized void onCreate() {
        super.onCreate();
        registerActivityLifecycleCallbacks(new MyLifecycleHandler());

        Thread.setDefaultUncaughtExceptionHandler(new MyExceptionHandler(this, StartupActivity.class));
    }
}
