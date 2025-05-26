package com.ingen.usbapp.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;

import java.io.File;

public class BitmapUtil {

    public static void loadImageFromBase64String(ImageView imageView, String base64Encoded) {
        try {
            byte[] decodedBytes = Base64.decode(base64Encoded, Base64.DEFAULT);
            Glide
                    .with(imageView.getContext())
                    .load(decodedBytes)
                    .fitCenter()
                    .into(imageView);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void loadImageFromFile(ImageView imageView, File imageFile) {
        try {
            Glide
                    .with(imageView.getContext())
                    .load(imageFile)
                    .fitCenter()
                    .into(imageView);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void loadSavedImage(String imageUrl, final View view, int width, int height) {
        try {
            File imageFile = FileUtil.getSettingImagesFile(view.getContext(), imageUrl);
            if (imageFile.exists()) {
                if (view instanceof ImageView) {
                    Glide.with(view.getContext())
                            .load(imageFile)
                            .apply(new RequestOptions().override(width, height))
                            .fitCenter()
                            .into((ImageView) view);
                } else {
                    Glide.with(view.getContext())
                            .load(imageFile)
                            .apply(new RequestOptions().override(width, height))
                            .fitCenter()
                            .into(new CustomTarget<Drawable>(width, height) {
                                @Override
                                public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                                    if (view != null && resource != null) {
                                        view.setBackground(resource);
                                    }
                                }

                                @Override
                                public void onLoadCleared(@Nullable Drawable placeholder) {

                                }
                            });
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void loadSavedImage2(String imageUrl, View view, int width, int height) {
        try {
            File imageFile = FileUtil.getSettingImagesFile(view.getContext(), imageUrl);
            if (imageFile.exists()) {
                Bitmap bitmap = BitmapFactory.decodeFile(imageFile.getPath(), getBitmapOptions(width, height, Bitmap.Config.RGB_565));

                if (view instanceof ImageView) {
                    ((ImageView) view).setImageBitmap(bitmap);
                } else {
                    view.setBackground(new BitmapDrawable(bitmap));
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static BitmapFactory.Options getBitmapOptions(int width, int height, Bitmap.Config config) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        options.inSampleSize = BitmapUtil.calculateInSampleSize(options, width, height);
        options.inPreferredConfig = config;
        return options;
    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }

    /**
     * Change bitmap brightness and contrast
     * brightness from -200 to 200, 0 is default
     * contrast from 0 to 2, 1 is default
     */
    public static Bitmap changeBitmapBrightnessContrast(Bitmap bitmap, float brightness, float contrast) {
        ColorMatrix cm = new ColorMatrix(new float[]{
                contrast, 0, 0, 0, brightness,
                0, contrast, 0, 0, brightness,
                0, 0, contrast, 0, brightness,
                0, 0, 0, 1, 0
        });

        Bitmap resultBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), bitmap.getConfig());

        Canvas canvas = new Canvas(resultBitmap);

        Paint paint = new Paint();
        paint.setColorFilter(new ColorMatrixColorFilter(cm));
        canvas.drawBitmap(bitmap, 0, 0, paint);

        return resultBitmap;
    }

    public static Bitmap resizeBitmap(Bitmap bitmap, int minWidth, int minHeight) {
        if (bitmap == null) {
            return null;
        }

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        float scaleWidth = ((float) minWidth) / width;
        float scaleHeight = ((float) minHeight) / height;
        float scale = Math.max(scaleWidth, scaleHeight);
        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(scale, scale);

        // "RECREATE" THE NEW BITMAP
        Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, false);

        return resizedBitmap;
    }
}
