package com.ingen.usbapp.ui.common;

import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;

import com.ingen.usbapp.R;

public class MenuDialog extends Dialog implements Runnable {

    private MenuDialogListener mListener;
    private Handler mHandler = new Handler();
    private int mNotTouchCounter = 0;

    public enum MenuDialogEvent {UPDATE_USB, EXIT, NONE}

    public interface MenuDialogListener {
        void onMenuDialogListener(MenuDialogEvent event);
    }

    public MenuDialog(@NonNull Context context, MenuDialogListener menuDialogListener) {
        super(context, R.style.MenuDialogTheme);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setCancelable(true);
        setContentView(R.layout.dialog_menu);

        mListener = menuDialogListener;

        initLayout();
        hideNavBar();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mNotTouchCounter = 0;
        mHandler.postDelayed(this, 1000);

    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
    }

    @Override
    public void run() {
        if (mNotTouchCounter >= 10) {
            dismiss();
            mHandler.postDelayed(this, 1000);
            return;
        }

        mNotTouchCounter += 1;
        mHandler.postDelayed(this, 1000);
    }

    private void initLayout() {
        findViewById(R.id.btnUpdate).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null) {
                    mListener.onMenuDialogListener(MenuDialogEvent.UPDATE_USB);
                }
                dismiss();
            }
        });

        findViewById(R.id.btnExit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null) {
                    mListener.onMenuDialogListener(MenuDialogEvent.EXIT);
                }
                dismiss();
            }
        });

        findViewById(R.id.btnReturn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
    }

    protected void hideNavBar() {
        if (getCurrentFocus() != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
        View decorView = getWindow().getDecorView();
        // Hide both the navigation bar and the status bar.
        // SYSTEM_UI_FLAG_FULLSCREEN is only available on Android 4.1 and higher, but as
        // a general rule, you should design your app to hide the status bar whenever you
        // hide the navigation bar.
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        decorView.setSystemUiVisibility(uiOptions);
    }
}
