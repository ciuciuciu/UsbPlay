package com.ingen.usbapp.usb;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;

import com.ingen.usbapp.BuildConfig;

import java.util.HashMap;

public class UsbDetector {

    public static final String ACTION_USB_PERMISSION = BuildConfig.APPLICATION_ID + ".USB_PERMISSION";

    private final UsbManager usbManager;
    private final Context context;

    public UsbDetector(Context context) {
        this.context = context;
        this.usbManager = (UsbManager) context.getSystemService(Context.USB_SERVICE);
    }

    public UsbDevice findFirstUsb() {
        HashMap<String, UsbDevice> list = usbManager.getDeviceList();
        if (list == null || list.isEmpty()) {
            return null;
        }

        return list.values().iterator().next();
    }

    public boolean hasPermission(UsbDevice device) {
        return usbManager.hasPermission(device);
    }

    public void requestPermission(UsbDevice device) {
        Intent intent = new Intent(ACTION_USB_PERMISSION);
        intent.setPackage(context.getPackageName());

        PendingIntent pi = PendingIntent.getBroadcast(
                context,
                device.getDeviceId(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
        usbManager.requestPermission(device, pi);
    }
}

