package com.ingen.usbapp.ui;

import android.os.Bundle;
import android.view.View;

import com.ingen.usbapp.R;
import com.ingen.usbapp.ui.common.BaseFragment;

public class LoadingFragment extends BaseFragment {

    public static LoadingFragment getInstance() {
        LoadingFragment fragment = new LoadingFragment();
        return fragment;
    }

    @Override
    protected int getLayout() {
        return R.layout.fragment_loading;
    }

    @Override
    protected void initView(View view) {

    }

    @Override
    protected void initView(Bundle savedInstanceState) {

    }
}
