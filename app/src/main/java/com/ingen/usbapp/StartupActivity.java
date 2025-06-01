package com.ingen.usbapp;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.Observer;

import com.ingen.usbapp.playlist.MediaObject;
import com.ingen.usbapp.playlist.PlaylistHelper;
import com.ingen.usbapp.ui.MainActivity;
import com.ingen.usbapp.ui.common.BaseActivity;
import com.ingen.usbapp.usb.UsbHelper;
import com.ingen.usbapp.usb.UsbMediaStatus;
import com.ingen.usbapp.utils.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class StartupActivity extends BaseActivity {

    private TextView txtTitle;

    private UsbHelper mUsbHelper;

    private final BroadcastReceiver usbReceiver = new BroadcastReceiver() {

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            String actionString = getPackageName() + ".action.USB_PERMISSION";

            if (actionString.equals(action)) {
                synchronized (this) {
                    UsbDevice device = (UsbDevice) intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);

                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        if (device != null) {
                            Logger.d("call method to set up device communication");
                            copyDataFromUsb();
                        }
                    } else {
                        Logger.d("permission denied for device " + device);
                    }
                }
            }
        }
    };

    @Override
    public int getLayoutRes() {
        return R.layout.activity_startup;
    }

    @Override
    protected void initActivity(Bundle savedInstanceState) {
        txtTitle = findViewById(R.id.txtTitle);

        mUsbHelper = new UsbHelper();

        initObservable();

        IntentFilter filter = new IntentFilter(getPackageName() + ".action.USB_PERMISSION");
        registerReceiver(usbReceiver, filter);
        discoverDevice();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(usbReceiver);
    }

    private void discoverDevice() {
        String actionString = getPackageName() + ".action.USB_PERMISSION";
        PendingIntent permissionIntent = PendingIntent.getBroadcast(this, 0, new Intent(actionString), PendingIntent.FLAG_IMMUTABLE);
        UsbManager usbManager = (UsbManager) getSystemService(USB_SERVICE);

        HashMap<String, UsbDevice> deviceList = usbManager.getDeviceList();
        Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();

        List<UsbDevice> notPermissionDevices = new ArrayList<>();
        while (deviceIterator.hasNext()) {
            UsbDevice usbDevice = deviceIterator.next();
            if (usbDevice != null && usbManager.hasPermission(usbDevice) == false) {
                notPermissionDevices.add(usbDevice);
            }
        }

        if (notPermissionDevices.size() > 0) {
            usbManager.requestPermission(notPermissionDevices.get(0), permissionIntent);
        } else {
            copyDataFromUsb();
        }

    }

    private void copyDataFromUsb() {
        mUsbHelper.copyDataFromUsb(this);
    }


    private void initObservable() {
        mUsbHelper.getLiveDataCopyDataFromUsb().observe(this, new Observer<UsbMediaStatus>() {
            @Override
            public void onChanged(UsbMediaStatus usbMediaStatus) {
                Logger.d("LiveDataCopyDataFromUsb: " + usbMediaStatus.getText());
                switch (usbMediaStatus) {
                    case NOT_FOUND_USB:
                    case USB_COPY_COMPLETED:
                        ArrayList<MediaObject> playlist = PlaylistHelper.getPlaylist(StartupActivity.this);
                        if (playlist.size() == 0) {
                            txtTitle.setText("There is no playlist");
                        } else {
                            startActivity(MainActivity.getIntent(StartupActivity.this, playlist));
                            StartupActivity.this.finish();
                        }
                        break;
                }
            }
        });
    }
}
