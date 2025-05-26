package com.ingen.usbapp.ui.videoPlayerRecycler;

import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ingen.usbapp.R;
import com.ingen.usbapp.playlist.MediaObject;
import com.bumptech.glide.RequestManager;

public class VideoPlayerViewHolder extends RecyclerView.ViewHolder {

    FrameLayout media_container;
    ImageView thumbnail;
    View parent;
    RequestManager requestManager;

    public VideoPlayerViewHolder(@NonNull View itemView) {
        super(itemView);
        parent = itemView;
        media_container = itemView.findViewById(R.id.media_container);
        thumbnail = itemView.findViewById(R.id.thumbnail);
    }

    public void onBind(MediaObject mediaObject, RequestManager requestManager) {
        this.requestManager = requestManager;
        parent.setTag(this);

        if (mediaObject.isImageFile()) {
            this.requestManager
                    .load(mediaObject.getFile())
                    .into(thumbnail);
        }
    }

}