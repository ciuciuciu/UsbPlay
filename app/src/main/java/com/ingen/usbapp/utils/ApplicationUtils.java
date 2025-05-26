package com.ingen.usbapp.utils;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Process;

import java.util.List;

public class ApplicationUtils {

    public static String getApplicationName(Context ctx) {
        String result = "";
        try {
            result = ctx.getPackageManager().getPackageInfo(ctx.getPackageName(), 0).packageName;
        } catch (PackageManager.NameNotFoundException e) {
        }
        return result;
    }

    public static String getApplicationVersionName(Context ctx) {
        String result = "";
        try {
            result = ctx.getPackageManager().getPackageInfo(ctx.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
        }
        return result;
    }

    public static int getApplicationVersionCode(Context ctx) {
        int result = 1;
        try {
            result = ctx.getPackageManager().getPackageInfo(ctx.getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
        }
        return result;
    }

    public static void restartApplication(Context context, Class<?> activityClass) {
        Intent intent = new Intent(context, activityClass);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
        // restart Activity
        context.startActivity(intent);
        //for restarting the Activity
        Process.killProcess(Process.myPid());
        System.exit(2);
    }

    /**
     * https://github.com/ciuciuciu/android_restart_app_by_service
     */
    public static void restartApplicationFromService(Service service, int delayedTime) {
        ActivityManager am = (ActivityManager) service.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningAppProcessInfo = am.getRunningAppProcesses();
        for (int i = 0; i < runningAppProcessInfo.size(); i++) {
            if (runningAppProcessInfo.get(i).processName.equals(ApplicationUtils.getApplicationName(service))) {
                Process.killProcess(runningAppProcessInfo.get(i).pid);
                break;
            }
        }

        // delay 3 seconds
        try {
            Thread.sleep(delayedTime);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        launchApp(service, ApplicationUtils.getApplicationName(service));
        service.stopSelf();
        Process.killProcess(Process.myPid());
    }

    private static void launchApp(Context context, String packageName) {
        PackageManager manager = context.getPackageManager();
        Intent i = manager.getLaunchIntentForPackage(packageName);
        i.addCategory(Intent.CATEGORY_LAUNCHER);
        context.startActivity(i);
    }
}
