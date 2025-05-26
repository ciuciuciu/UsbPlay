package com.ingen.usbapp.services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.Nullable;

import com.ingen.usbapp.utils.ApplicationUtils;
import com.ingen.usbapp.utils.Logger;

public class RestartApplicationService extends IntentService {

    public static void startService(Context context, int delayedRestart) {
        Intent restartServiceIntent = new Intent(context, RestartApplicationService.class);
        restartServiceIntent.putExtra("DelayedRestart", delayedRestart);
        context.startService(restartServiceIntent);
    }

    public RestartApplicationService() {
        super("RestartApplicationService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        int delayedRestart = intent.getIntExtra("DelayedRestart", 1000);
        Logger.d("delayedRestart " + delayedRestart);
        ApplicationUtils.restartApplicationFromService(this, delayedRestart);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Logger.d("onDestroy()");
    }
}
