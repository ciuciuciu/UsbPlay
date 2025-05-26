package com.ingen.usbapp.usb;

import android.text.TextUtils;

public enum UsbMediaStatus {

    NOT_FOUND_USB("Not_Found_USB"),
    USB_COPYING("USB_Copying"),
    USB_COPY_COMPLETED("USB_Copy_Completed"),

    ERROR("Error");

    private String text;

    UsbMediaStatus(String text) {
        this.text = text;
    }

    public String getText() {
        return this.text;
    }

    public static UsbMediaStatus fromString(String text) {
        if (TextUtils.isEmpty(text)) {
            return ERROR;
        }

        for (UsbMediaStatus type : UsbMediaStatus.values()) {
            if (type.text.equalsIgnoreCase(text)) {
                return type;
            }
        }
        return ERROR;
    }
}
