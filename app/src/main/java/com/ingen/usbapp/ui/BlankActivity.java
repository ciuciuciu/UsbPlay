package com.ingen.usbapp.ui;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;

import androidx.annotation.Nullable;

import com.ingen.usbapp.config.Configuration;
import com.ingen.usbapp.services.ForegroundServiceLauncher;
import com.ingen.usbapp.utils.Logger;

public class BlankActivity extends Activity {

    private final int ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE = 2323;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Configuration.USE_OVERLAY_PERMISSION &&
                Build.VERSION.SDK_INT > Build.VERSION_CODES.P &&
                !Settings.canDrawOverlays(this)) {
            RequestPermission();
        } else {
            startMainActivity();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Logger.d("onActivityResult " + resultCode + " - " + requestCode);
        if (requestCode == ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (!Settings.canDrawOverlays(this)) {
                    finish();
                } else {
                    // Permission Granted-System will work
                    startMainActivity();
                }
            }
        }
    }

    private void startMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        Configuration.saveAppRunning(this, true);
        ForegroundServiceLauncher.getInstance().startService(getApplication());

        startActivity(intent);
        finish();
    }

    private void RequestPermission() {
        // Check if Android M or higher
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Show alert dialog to the user saying a separate permission is needed
            // Launch the settings activity if the user prefers
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE);
        }
    }
}
