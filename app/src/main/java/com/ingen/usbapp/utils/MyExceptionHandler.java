package com.ingen.usbapp.utils;

import android.content.Context;

import com.ingen.usbapp.services.RestartApplicationService;

public class MyExceptionHandler implements Thread.UncaughtExceptionHandler {
    private final Context myContext;
    private final Class<?> myActivityClass;

    public MyExceptionHandler(Context context, Class<?> c) {
        myContext = context;
        myActivityClass = c;
    }

    @Override
    public void uncaughtException(Thread thread, Throwable throwable) {
        Logger.d("uncaughtException");
        Logger.d(thread.getName() + " : " + throwable.getMessage());

        throwable.printStackTrace();

        RestartApplicationService.startService(myContext, 2000);
    }
}