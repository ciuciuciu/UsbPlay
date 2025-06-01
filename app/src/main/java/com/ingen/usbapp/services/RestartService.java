package com.ingen.usbapp.services;

import android.app.ActivityManager;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import java.util.List;

public class RestartService extends IntentService {
    public RestartService() {
        super("RestartService");
    }

    @Override
    protected void onHandleIntent(Intent workIntent) {
        ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningAppProcessInfo = am.getRunningAppProcesses();
        for (int i = 0; i < runningAppProcessInfo.size(); i++) {
            if (runningAppProcessInfo.get(i).processName.equals("com.example.apprestart")) {
                android.os.Process.killProcess(runningAppProcessInfo.get(i).pid);
                break;
            }
        }

        //restart after 3 seconds
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        launchApp(this, "com.example.apprestart");
        stopSelf();
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    public void launchApp(Context context, String packageName) {
        PackageManager manager = context.getPackageManager();
        Intent i = manager.getLaunchIntentForPackage(packageName);
        i.addCategory(Intent.CATEGORY_LAUNCHER);
        context.startActivity(i);
    }
}