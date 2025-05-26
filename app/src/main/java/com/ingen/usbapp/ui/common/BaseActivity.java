package com.ingen.usbapp.ui.common;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.ColorRes;
import androidx.annotation.DimenRes;
import androidx.annotation.IntegerRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

public abstract class BaseActivity extends AppCompatActivity {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutRes());
        dismissKeyboard();
        hideNavBar();

        initActivity(savedInstanceState);
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
        dismissKeyboard();
    }

    public abstract @LayoutRes
    int getLayoutRes();

    protected abstract void initActivity(Bundle savedInstanceState);

    protected void replaceFragment(int viewId, Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(viewId, fragment)
                .commit();
    }

    protected void addFragment(int viewId, Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .add(viewId, fragment)
                .commit();
    }

    protected void addFragmentToBackStack(int viewId, Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .add(viewId, fragment)
                .addToBackStack(fragment.getClass().getSimpleName())
                .commit();
    }

    public void dismissKeyboard() {
        try {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            View view = findViewById(android.R.id.content);
            if (view == null) {
                view = getCurrentFocus();
                if (view == null) {
                    view = new View(this);
                }
            }
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public int dimen(@DimenRes int resId) {
        return (int) getResources().getDimension(resId);
    }

    public int color(@ColorRes int resId) {
        return getResources().getColor(resId);
    }

    public int integer(@IntegerRes int resId) {
        return getResources().getInteger(resId);
    }

    public String string(@StringRes int resId) {
        return getResources().getString(resId);
    }

    protected int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    protected void hideNavBar() {
        if (getCurrentFocus() != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
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
