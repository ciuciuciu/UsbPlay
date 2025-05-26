package com.ingen.usbapp.playlist;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.webkit.MimeTypeMap;

import com.ingen.usbapp.utils.Logger;

import java.io.File;

public class MediaObject implements Parcelable {

    private File file;

    public MediaObject(File file) {
        this.file = file;
    }

    public File getFile() {
        return file;
    }

    public Uri getUri() {
        return Uri.fromFile(file);
    }

    public boolean isImageFile() {
        Uri uri = Uri.fromFile(file);
        String fileExt = MimeTypeMap.getFileExtensionFromUrl(uri.toString());

        Logger.d(file.getName() + " - " + fileExt);

        if (fileExt.equalsIgnoreCase("jpg") || fileExt.equalsIgnoreCase("jpeg") || fileExt.equalsIgnoreCase("png")) {
            return true;
        }

        return false;
    }

    public boolean isVideoFile() {
        Uri uri = Uri.fromFile(file);
        String fileExt = MimeTypeMap.getFileExtensionFromUrl(uri.toString());

        Logger.d(file.getName() + " - " + fileExt);

        if (fileExt.equalsIgnoreCase("mpg") || fileExt.equalsIgnoreCase("mpeg") || fileExt.equalsIgnoreCase("mp4")) {
            return true;
        }

        return false;
    }

    public int getDurationOfImage() {
        String fileName = file.getName();
        int firstIndex = fileName.lastIndexOf("(");
        int lastIndex = fileName.lastIndexOf(")");

        if (firstIndex >= 0 && firstIndex < lastIndex) {
            try {
                String sub = fileName.substring(firstIndex + 1, lastIndex);
                return Integer.parseInt(sub);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        return 10;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeSerializable(this.file);
    }

    public void readFromParcel(Parcel source) {
        this.file = (File) source.readSerializable();
    }

    protected MediaObject(Parcel in) {
        this.file = (File) in.readSerializable();
    }

    public static final Creator<MediaObject> CREATOR = new Creator<MediaObject>() {
        @Override
        public MediaObject createFromParcel(Parcel source) {
            return new MediaObject(source);
        }

        @Override
        public MediaObject[] newArray(int size) {
            return new MediaObject[size];
        }
    };
}
