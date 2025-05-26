package com.ingen.usbapp.ui.common;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.ColorRes;
import androidx.annotation.DimenRes;
import androidx.annotation.IntegerRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;

public abstract class BaseFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(getLayout(), container, false);
        initView(view);
        return view;

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(savedInstanceState);
    }

    protected abstract int getLayout();

    protected abstract void initView(View view);

    protected abstract void initView(Bundle savedInstanceState);

    protected void addFragment(int viewId, Fragment fragment, boolean addToBackStack) {
        if (getActivity() instanceof BaseActivity) {
            if (addToBackStack) {
                ((BaseActivity) getActivity()).addFragmentToBackStack(viewId, fragment);
            } else {
                ((BaseActivity) getActivity()).addFragment(viewId, fragment);
            }
        } else {
            throw new ClassCastException("Current Activity is not of com.ideaone.usbapp.ui.common.BaseActivity");
        }
    }

    protected void setMargins(View view, int left, int top, int right, int bottom) {
        if (view.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
            params.setMargins(left, top, right, bottom);
            view.requestLayout();
        }
    }

    protected void resizeFragment(Fragment fragment, int newWidth, int newHeight) {
        if (fragment != null) {
            try {
                View view = fragment.getView();
                ViewGroup.LayoutParams params = view.getLayoutParams();
                params.width = newWidth;
                params.height = newHeight;
                view.setLayoutParams(params);
                view.requestLayout();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    protected void resizeView(View view, int newWidth, int newHeight) {
        if (view != null) {
            try {
                ViewGroup.LayoutParams params = view.getLayoutParams();
                params.width = newWidth;
                if (newHeight > 0) {
                    params.height = newHeight;
                }
                view.setLayoutParams(params);
                view.requestLayout();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    protected void hideNavBar() {
        if (getActivity().getCurrentFocus() != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
        View decorView = getActivity().getWindow().getDecorView();
        // Hide both the navigation bar and the status bar.
        // SYSTEM_UI_FLAG_FULLSCREEN is only available on Android 4.1 and higher, but as
        // a general rule, you should design your app to hide the status bar whenever you
        // hide the navigation bar.
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        decorView.setSystemUiVisibility(uiOptions);
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

}
