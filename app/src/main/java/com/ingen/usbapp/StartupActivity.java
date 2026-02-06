package com.ingen.usbapp;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.TextView;

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

    /**
     * Action xin USB permission
     */
    private static final String ACTION_USB_PERMISSION = BuildConfig.APPLICATION_ID + ".USB_PERMISSION";

    private TextView txtTitle;

    private UsbManager usbManager;
    private UsbHelper usbHelper;

    // Queue of devices (we will request permission sequentially)
    private List<UsbDevice> pendingDevices = new ArrayList<>();

    private final Handler handler = new Handler(Looper.getMainLooper());

    // BroadcastReceiver must be registered before calling requestPermission()
    private final BroadcastReceiver usbReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action == null) return;

            if (UsbManager.ACTION_USB_DEVICE_ATTACHED.equals(action)) {
                UsbDevice device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                Logger.d("Receiver: USB attached -> " + device);
                if (device != null) {
                    // Add to queue and process if not currently waiting
                    if (!containsDeviceByName(pendingDevices, device)) {
                        pendingDevices.add(device);
                    }
                    processNextDeviceInQueue();
                }
            } else if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)) {
                UsbDevice device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                Logger.d("Receiver: USB detached -> " + device);
                // Remove from queue if present
                removeDeviceByName(pendingDevices, device);
            } else if (ACTION_USB_PERMISSION.equals(action)) {

                handler.postDelayed(() -> {
                    scanAndProcessUsbDevicesAtStartup();
                }, 5000);
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

        usbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
        usbHelper = new UsbHelper();

        // Register receiver immediately so we don't miss permission broadcasts
        registerUsbReceiver();

        // Start scanning and processing devices
        scanAndProcessUsbDevicesAtStartup();

        initObservable();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
        try {
            unregisterReceiver(usbReceiver);
        } catch (Exception e) {
            Logger.d("Receiver already unregistered or error unregister");
        }
    }

    // -------------------------
    // Startup scanning logic
    // -------------------------
    private void scanAndProcessUsbDevicesAtStartup() {
        if (usbManager == null) {
            Logger.d("usbManager is null");
            return;
        }

        HashMap<String, UsbDevice> deviceList = usbManager.getDeviceList();
        if (deviceList == null || deviceList.isEmpty()) {
            Logger.d("No USB devices found at startup.");
            return;
        }

        // Fill pending queue with discovered devices
        Iterator<UsbDevice> iterator = deviceList.values().iterator();
        while (iterator.hasNext()) {
            UsbDevice dev = iterator.next();
            if (!containsDeviceByName(pendingDevices, dev)) {
                pendingDevices.add(dev);
            }
        }

        // Start processing queue
        processNextDeviceInQueue();
    }

    /**
     * Process next device in queue:
     * - If device has permission -> call copy immediately
     * - If device has no permission -> request permission and wait for callback
     */
    private void processNextDeviceInQueue() {
        // pop first device
        if (pendingDevices.isEmpty()) {
            Logger.d("Device queue empty");
            return;
        }

        UsbDevice device = pendingDevices.remove(0);
        if (device == null) {
            processNextDeviceInQueue();
            return;
        }

        Logger.d("Processing device: " + device.getDeviceName());

        if (usbManager.hasPermission(device)) {
            Logger.d("Has permission for device -> start copy immediately");

            usbHelper.copyDataFromUsb(this);

            // continue to next device
            processNextDeviceInQueue();
            return;
        }

        // Otherwise request permission and wait for result
        Logger.d("No permission -> request for device: " + device.getDeviceName());
        requestUsbPermissionForDevice(device);
    }

    private void requestUsbPermissionForDevice(UsbDevice device) {
        if (device == null || usbManager == null) return;

        Intent intent = new Intent(ACTION_USB_PERMISSION);
        // ensure the pending intent targets our app only
        intent.setPackage(getPackageName());
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this,
                device.getDeviceId(), // use deviceId as requestCode to differentiate
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        usbManager.requestPermission(device, pendingIntent);
    }


    private void initObservable() {
        usbHelper.getLiveDataCopyDataFromUsb().observe(this, new Observer<UsbMediaStatus>() {
            @Override
            public void onChanged(UsbMediaStatus usbMediaStatus) {
                Logger.d("LiveDataCopyDataFromUsb: " + usbMediaStatus);
                switch (usbMediaStatus) {
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

    // -------------------------
    // Receiver registration
    // -------------------------
    private void registerUsbReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        filter.addAction(ACTION_USB_PERMISSION);
        registerReceiver(usbReceiver, filter);
    }

    // -------------------------
    // Helpers
    // -------------------------
    private boolean containsDeviceByName(List<UsbDevice> list, UsbDevice device) {
        if (list == null || device == null) return false;
        for (UsbDevice d : list) {
            if (d != null && d.getDeviceName() != null && d.getDeviceName().equals(device.getDeviceName())) {
                return true;
            }
        }
        return false;
    }

    private void removeDeviceByName(List<UsbDevice> list, UsbDevice device) {
        if (list == null || device == null) return;
        List<UsbDevice> toRemove = new ArrayList<>();
        for (UsbDevice d : list) {
            if (d != null && d.getDeviceName() != null && d.getDeviceName().equals(device.getDeviceName())) {
                toRemove.add(d);
            }
        }
        list.removeAll(toRemove);
    }
}
