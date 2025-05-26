package com.ingen.usbapp.usb;

import android.content.Context;
import android.text.TextUtils;

import androidx.lifecycle.MutableLiveData;

import com.ingen.usbapp.executor.FixedExecutorManager;
import com.ingen.usbapp.utils.Logger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import me.jahnen.libaums.core.UsbMassStorageDevice;
import me.jahnen.libaums.core.fs.FileSystem;
import me.jahnen.libaums.core.fs.UsbFile;
import me.jahnen.libaums.core.fs.UsbFileInputStream;

public class UsbHelper {

    private MutableLiveData<UsbMediaStatus> LiveDataCopyDataFromUsb = new MutableLiveData<>();

    public MutableLiveData<UsbMediaStatus> getLiveDataCopyDataFromUsb() {
        return LiveDataCopyDataFromUsb;
    }

    public void copyDataFromUsb(Context context) {
        FixedExecutorManager.getUsbExecutorService().execute(new Runnable() {
            @Override
            public void run() {
                List<UsbFile> usbFileList = getUsbRootListFile(context);

                if (usbFileList.size() == 0) {
                    LiveDataCopyDataFromUsb.postValue(UsbMediaStatus.NOT_FOUND_USB);
                } else {
                    LiveDataCopyDataFromUsb.postValue(UsbMediaStatus.USB_COPYING);
                    copyFileFromUsb(context, usbFileList, "Images");
                    copyFileFromUsb(context, usbFileList, "Videos");
                    LiveDataCopyDataFromUsb.postValue(UsbMediaStatus.USB_COPY_COMPLETED);
                }
            }
        });
    }

    private static List<UsbFile> getUsbRootListFile(Context context) {
        List<UsbFile> usbFileList = new ArrayList<>();

        UsbMassStorageDevice[] devices = UsbMassStorageDevice.getMassStorageDevices(context);
        for (UsbMassStorageDevice device : devices) {
            try {
                // before interacting with a device you need to call init()!
                device.init();
                // Only uses the first partition on the device
                FileSystem currentFs = device.getPartitions().get(0).getFileSystem();
                UsbFile root = currentFs.getRootDirectory();

                usbFileList.addAll(Arrays.asList(root.listFiles()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return usbFileList;
    }

    private static void copyFileFromUsb(Context context, List<UsbFile> usbFileList, String folderName) {
        if (usbFileList == null || usbFileList.size() == 0 || TextUtils.isEmpty(folderName)) {
            return;
        }

        //TODO Copy files from USB to Device
        for (UsbFile file : usbFileList) {
            Logger.d(file.getName());

            OutputStream outputStream = null;
            InputStream inputStream = null;
            try {
                if (file.isDirectory() && file.getName().equalsIgnoreCase(folderName)) {
                    //TODO Delete exist files on Device
                    File mediaFolder = getMediaDir(context, folderName);
                    for (File mediaFile : mediaFolder.listFiles()) {
                        mediaFile.delete();
                    }

                    //TODO Copy files in media folder
                    for (UsbFile mediaFile : file.listFiles()) {
                        File tempFile = new File(mediaFolder, "temp.dat");
                        if (tempFile.exists()) {
                            tempFile.delete();
                        }

                        outputStream = new FileOutputStream(tempFile);
                        inputStream = new UsbFileInputStream(mediaFile);
                        byte[] buffer = new byte[4096];

                        while (true) {
                            int read = inputStream.read(buffer);
                            if (read == -1) {
                                break;
                            }
                            outputStream.write(buffer, 0, read);
                        }
                        outputStream.flush();

                        // rename temp file to target file
                        tempFile.renameTo(new File(mediaFolder, mediaFile.getName()));
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            } finally {
                try {
                    if (inputStream != null) {
                        inputStream.close();
                    }
                    if (outputStream != null) {
                        outputStream.close();
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
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
