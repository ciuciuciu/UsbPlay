package com.ingen.usbapp.playlist;

import android.content.Context;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class PlaylistHelper {

    public static ArrayList<MediaObject> getPlaylist(Context context) {
        ArrayList<MediaObject> playlist = new ArrayList<>();

        File imagesFolder = getMediaDir(context, "Images");
        for (File file : imagesFolder.listFiles()) {
            MediaObject mediaObject = new MediaObject(file);
            if (mediaObject.isImageFile()) {
                playlist.add(mediaObject);
            }
        }

        File videosFolder = getMediaDir(context, "Videos");
        for (File file : videosFolder.listFiles()) {
            MediaObject mediaObject = new MediaObject(file);
            if (mediaObject.isVideoFile()) {
                playlist.add(mediaObject);
            }
        }

        Collections.sort(playlist, new Comparator<MediaObject>() {
            @Override
            public int compare(MediaObject mediaObject1, MediaObject mediaObject2) {
                return mediaObject1.getFile().getName().compareToIgnoreCase(mediaObject2.getFile().getName());
            }
        });

        return playlist;
    }


    private static File getMediaDir(Context context, String folderName) {
        File parentDir = new File(context.getFilesDir().getParent());
        File imagesDir = new File(parentDir, folderName);
        if (imagesDir.exists() == false) {
            imagesDir.mkdir();
        }
        return imagesDir;
    }
}
