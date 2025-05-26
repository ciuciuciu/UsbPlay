package com.ingen.usbapp.ui;

import android.os.Bundle;
import android.view.View;

import com.ingen.usbapp.R;
import com.ingen.usbapp.playlist.MediaObject;
import com.ingen.usbapp.ui.common.BaseFragment;

public class ScreenSlidePageFragment extends BaseFragment {

    private MediaObject mMediaObject;

    public ScreenSlidePageFragment(MediaObject mediaObject) {
        mMediaObject = mediaObject;
    }

    @Override
    protected int getLayout() {
        return R.layout.fragment_screen_slide_item;
    }

    @Override
    protected void initView(View view) {

    }

    @Override
    protected void initView(Bundle savedInstanceState) {

    }
}
