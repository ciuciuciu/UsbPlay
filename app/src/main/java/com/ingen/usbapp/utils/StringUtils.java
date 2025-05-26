package com.ingen.usbapp.utils;

import android.util.Base64;

public class StringUtils {

    public static String encodeBase64String(String input) {
        return Base64.encodeToString(input.getBytes(), Base64.NO_WRAP);
    }

    public static String decodeBase64String(String encodedString) {
        return new String(Base64.decode(encodedString, Base64.NO_WRAP));
    }
}
