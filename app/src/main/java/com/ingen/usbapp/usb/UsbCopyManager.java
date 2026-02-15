package com.ingen.usbapp.usb;

import android.content.Context;

import com.ingen.usbapp.utils.Logger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.Executors;

import me.jahnen.libaums.core.UsbMassStorageDevice;
import me.jahnen.libaums.core.fs.UsbFile;
import me.jahnen.libaums.core.fs.UsbFileInputStream;

public class UsbCopyManager {

    public interface Callback {
        void onCompleted();
        void onFailed();
    }

    public void copyFromUsb(Context context, Callback callback) {
        Logger.d("copyFromUsb");
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                UsbMassStorageDevice[] devices = UsbMassStorageDevice.getMassStorageDevices(context);

                if (devices.length == 0) {
                    callback.onFailed();
                    return;
                }

                UsbMassStorageDevice device = devices[0];
                device.init();

                UsbFile root = device.getPartitions().get(0).getFileSystem().getRootDirectory();

                Logger.d("copyFolder Videos");
                copyFolder(context, root, "Videos");

                Logger.d("copyFolder Images");
                copyFolder(context, root, "Images");

                callback.onCompleted();
            } catch (Exception e) {
                e.printStackTrace();
                callback.onFailed();
            }
        });
    }

    private void copyFolder(Context context, UsbFile root, String folder) throws Exception {
        for (UsbFile f : root.listFiles()) {
            if (f.isDirectory() && f.getName().equalsIgnoreCase(folder)) {
                File targetDir = new File(context.getFilesDir(), folder);
                if (!targetDir.exists()) targetDir.mkdirs();

                for (File old : targetDir.listFiles()) old.delete();

                for (UsbFile usbFile : f.listFiles()) {
                    File out = new File(targetDir, usbFile.getName());
                    InputStream in = new UsbFileInputStream(usbFile);
                    OutputStream os = new FileOutputStream(out);

                    byte[] buf = new byte[4096];
                    int len;
                    while ((len = in.read(buf)) > 0) {
                        os.write(buf, 0, len);
                    }
                    in.close();
                    os.close();
                }
            }
        }
    }
}

