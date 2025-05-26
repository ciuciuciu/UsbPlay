package com.ingen.usbapp.utils;

import android.util.Base64;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

public class ImageLoader {

    public static void loadImageFromBase64String(ImageView imageView, String base64Encoded, boolean centerCrop) {
        try {
            byte[] decodedBytes = Base64.decode(base64Encoded, Base64.DEFAULT);
            RequestBuilder requestBuilder = Glide
                    .with(imageView.getContext())
                    .load(decodedBytes)
                    .format(DecodeFormat.PREFER_RGB_565)
                    .diskCacheStrategy(DiskCacheStrategy.ALL);
            if (centerCrop) {
                requestBuilder.centerCrop();
            } else {
                requestBuilder.fitCenter();
            }

            requestBuilder.into(imageView);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
