package com.ingen.usbapp.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.ingen.usbapp.Configuration;
import com.ingen.usbapp.R;
import com.ingen.usbapp.StartupActivity;
import com.ingen.usbapp.playlist.MediaObject;
import com.ingen.usbapp.services.ForegroundServiceLauncher;
import com.ingen.usbapp.services.LifeTimeService;
import com.ingen.usbapp.services.RunningService;
import com.ingen.usbapp.ui.common.BaseActivity;
import com.ingen.usbapp.ui.common.MenuDialog;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends BaseActivity {

    private long startPressTime;
    private boolean pressing = false;
    private Rect pressRect;

    public static Intent getIntent(Context context, ArrayList<MediaObject> playlist) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.putParcelableArrayListExtra("Playlist", playlist);

        return intent;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.activity_main;
    }

    @Override
    protected void initActivity(Bundle savedInstanceState) {
        ArrayList<MediaObject> playlist = getIntent().getParcelableArrayListExtra("Playlist");

        addFragment(R.id.fragmentContainer, ScreenSlideFragment.getInstance(playlist));

        pressRect = new Rect(0, 0, dpToPx(300), dpToPx(100));
        initButtonTopLeftTouch();

        Configuration.saveAppRunning(this, true);
        ForegroundServiceLauncher.getInstance().startService(getApplication());
    }

    private void initButtonTopLeftTouch() {
        findViewById(R.id.buttonTopLeft).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                int x = (int) motionEvent.getX();
                int y = (int) motionEvent.getY();

                //Logger.d("debug_log", "motionEvent " + motionEvent.toString() + " at " + x + ":" + y);

                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        if (pressRect.contains(x, y)) {
                            Log.i("debug_log", "start touched down");
                            pressing = true;
                            startPressTime = System.currentTimeMillis();
                        } else {
                            pressing = false;
                            startPressTime = System.currentTimeMillis();
                        }
                        break;

                    case MotionEvent.ACTION_MOVE:
                        if (!pressRect.contains(x, y)) {
                            Log.i("debug_log", "moving out: " + x + ":" + y);
                            pressing = false;
                            startPressTime = System.currentTimeMillis();
                        } else {
                            Log.i("debug_log", "press duration: " + (System.currentTimeMillis() - startPressTime));
                        }
                        break;

                    case MotionEvent.ACTION_UP:
                        Log.i("debug_log", "touched up");
                        if (pressRect.contains(x, y) && pressing) {
                            if (System.currentTimeMillis() - startPressTime >= 2000) {
                                Log.i("debug_log", "touched up 2 second");

                                try {
                                    new MenuDialog(MainActivity.this, new MenuDialog.MenuDialogListener() {
                                        @Override
                                        public void onMenuDialogListener(MenuDialog.MenuDialogEvent event) {
                                            try {
                                                switch (event) {
                                                    case UPDATE_USB:
                                                        Intent i = new Intent(MainActivity.this, StartupActivity.class);
                                                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                        MainActivity.this.startActivity(i);
                                                        finish();
                                                        break;

                                                    case EXIT:
                                                        exitApplication();
                                                        break;
                                                }
                                            } catch (Exception exception) {
                                                exception.printStackTrace();
                                            }
                                        }
                                    }).show();
                                } catch (Exception ex) {
                                    ex.printStackTrace();
                                }
                            }
                        }

                        pressing = false;
                        startPressTime = System.currentTimeMillis();
                        break;
                }
                return false;
            }
        });
    }

    private void exitApplication() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            //TODO Background work here
            Configuration.saveAppRunning(MainActivity.this, false);
            try {
                Thread.sleep(150);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            ForegroundServiceLauncher.getInstance().stopService(MainActivity.this, LifeTimeService.class);
            ForegroundServiceLauncher.getInstance().stopService(MainActivity.this, RunningService.class);

            handler.post(() -> {
                //TODO UI Thread work here
                dismissKeyboard();
                finishAndRemoveTask();
                finishAffinity();
                System.exit(0);
            });
        });
    }
}
