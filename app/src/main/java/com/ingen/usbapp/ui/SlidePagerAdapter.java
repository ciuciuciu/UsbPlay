package com.ingen.usbapp.ui;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.ingen.usbapp.playlist.MediaObject;

import java.util.ArrayList;

public class SlidePagerAdapter extends FragmentStateAdapter {

    private ArrayList<MediaObject> mPlaylist;

    public SlidePagerAdapter(@NonNull FragmentActivity fragmentActivity, ArrayList<MediaObject> playlist) {
        super(fragmentActivity);

        mPlaylist = playlist;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        MediaObject mediaObject = mPlaylist.get(position);
        return new ScreenSlidePageFragment(mediaObject);
    }

    @Override
    public int getItemCount() {
        return mPlaylist == null ? 0 : mPlaylist.size();
    }
}
