package com.ingen.usbapp.services;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.ArrayMap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.ingen.usbapp.Configuration;
import com.ingen.usbapp.R;
import com.ingen.usbapp.ui.common.BaseActivity;
import com.ingen.usbapp.utils.ApplicationUtils;
import com.ingen.usbapp.utils.Logger;
import com.ingen.usbapp.utils.MyLifecycleHandler;

import java.lang.reflect.Field;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class RunningService extends Service implements Runnable {

    private long timerTime = TimeUnit.SECONDS.toMillis(10);
    private Handler timerHandler = new Handler();

    private boolean mLastCheck;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mLastCheck = true;
        Logger.d("onCreate");
        startForeground(62319, builtNotification());
        timerHandler.postDelayed(this::run, 1000);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        ForegroundServiceLauncher.getInstance().startService(this);
    }

    @Override
    public void run() {
        try {
            boolean isAppRunning = Configuration.getAppRunning(this);
            if (isAppRunning) {
                ActivityManager activityManager = (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);
                boolean isForeground = isApplicationForeground(activityManager);
                Logger.d("isForeground " + isForeground);
                if (isForeground) {
                    boolean isValidRunningActivity = isValidRunningActivity();
                    boolean isApplicationInForeground = MyLifecycleHandler.isApplicationInForeground();
                    boolean isApplicationVisible = MyLifecycleHandler.isApplicationVisible();

                    if (isValidRunningActivity && isApplicationInForeground && isApplicationVisible) {
                        mLastCheck = true;
                        Logger.d("WORKING GOOD");
                    } else {
                        Logger.d("isValidRunningActivity " + isValidRunningActivity);
                        Logger.d("isApplicationInForeground " + isApplicationInForeground);
                        Logger.d("isApplicationVisible " + isApplicationVisible);

                        if (mLastCheck == false) {
                            // TODO restart application
                            RestartApplicationService.startService(this, 2000);
                        }
                        mLastCheck = false;
                    }
                } else {
                    // TODO restart application
                    RestartApplicationService.startService(this, 2000);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            timerHandler.postDelayed(this, timerTime);
        }
    }

    /**
     * Returns the foreground application
     */
    private boolean isApplicationForeground(@NonNull ActivityManager activityManager) {
        List<ActivityManager.RunningAppProcessInfo> runningAppProcessInfoList = activityManager.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo runningAppProcess : runningAppProcessInfoList) {
            if (runningAppProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND &&
                    runningAppProcess.processName.equalsIgnoreCase(ApplicationUtils.getApplicationName(this))) {
                return true;
            }
        }
        return false;
    }

    private boolean isValidRunningActivity() {
        Activity runningActivity = getRunningActivity();
        if (runningActivity == null) {
            return false;
        }

        if (runningActivity instanceof BaseActivity) {
            return true;
        }

        return false;
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private Activity getRunningActivity() {
        try {
            Class activityThreadClass = Class.forName("android.app.ActivityThread");
            Object activityThread = activityThreadClass.getMethod("currentActivityThread")
                    .invoke(null);
            Field activitiesField = activityThreadClass.getDeclaredField("mActivities");
            activitiesField.setAccessible(true);
            ArrayMap activities = (ArrayMap) activitiesField.get(activityThread);
            for (Object activityRecord : activities.values()) {
                Class activityRecordClass = activityRecord.getClass();
                Field pausedField = activityRecordClass.getDeclaredField("paused");
                pausedField.setAccessible(true);
                if (!pausedField.getBoolean(activityRecord)) {
                    Field activityField = activityRecordClass.getDeclaredField("activity");
                    activityField.setAccessible(true);
                    return (Activity) activityField.get(activityRecord);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        throw new RuntimeException("Didn't find the running activity");
    }

    private Notification builtNotification() {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        assert notificationManager != null;

        NotificationCompat.Builder builder = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel notificationChannel = new NotificationChannel("ID", "Name", importance);
            notificationManager.createNotificationChannel(notificationChannel);
            builder = new NotificationCompat.Builder(this, notificationChannel.getId());
        } else {
            builder = new NotificationCompat.Builder(this);
        }

        builder.setDefaults(Notification.DEFAULT_LIGHTS);

        String message = "Running service";
        builder.setSmallIcon(R.mipmap.ic_launcher)
                .setAutoCancel(false)
                .setPriority(Notification.PRIORITY_MAX)
                .setOngoing(true)
                .setOnlyAlertOnce(true)
                .setColor(Color.parseColor("#0f9595"))
                .setContentTitle(getString(R.string.app_name))
                .setContentText(message);

        Intent launchIntent = getPackageManager().getLaunchIntentForPackage(getPackageName());
        launchIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, launchIntent,
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
                        ? PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
                        : PendingIntent.FLAG_UPDATE_CURRENT
        );
        builder.setContentIntent(contentIntent);

        Notification notification = builder.build();
        notification.flags = Notification.FLAG_ONGOING_EVENT;
        return notification;
    }
}
