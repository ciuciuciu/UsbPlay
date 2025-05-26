package com.ingen.usbapp.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class SharedPrefs {

    private static final String PREFS_NAME = "Config";

    public static SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    @SuppressWarnings("unchecked")
    public static <T> T get(Context context, String key, Class<T> anonymousClass) {
        if (context == null) {
            return null;
        }

        SharedPreferences mSharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        if (anonymousClass == String.class) {
            return (T) mSharedPreferences.getString(key, "");
        } else if (anonymousClass == Boolean.class) {
            return (T) Boolean.valueOf(mSharedPreferences.getBoolean(key, false));
        } else if (anonymousClass == Float.class) {
            return (T) Float.valueOf(mSharedPreferences.getFloat(key, 0));
        } else if (anonymousClass == Integer.class) {
            return (T) Integer.valueOf(mSharedPreferences.getInt(key, 0));
        } else if (anonymousClass == Long.class) {
            return (T) Long.valueOf(mSharedPreferences.getLong(key, 0));
        } else {
            Gson gson = new Gson();
            return (T) gson.fromJson(mSharedPreferences.getString(key, ""), anonymousClass);
        }
    }

    public static <T> T get(Context context, String key, Class<T> anonymousClass, T defaultValue) {
        if (context == null) {
            return null;
        }

        SharedPreferences mSharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        if (anonymousClass == String.class) {
            return (T) mSharedPreferences.getString(key, (String) defaultValue);
        } else if (anonymousClass == Boolean.class) {
            return (T) Boolean.valueOf(mSharedPreferences.getBoolean(key, (Boolean) defaultValue));
        } else if (anonymousClass == Float.class) {
            return (T) Float.valueOf(mSharedPreferences.getFloat(key, (Float) defaultValue));
        } else if (anonymousClass == Integer.class) {
            return (T) Integer.valueOf(mSharedPreferences.getInt(key, (Integer) defaultValue));
        } else if (anonymousClass == Long.class) {
            return (T) Long.valueOf(mSharedPreferences.getLong(key, (Long) defaultValue));
        } else {
            Gson gson = new Gson();
            return (T) gson.fromJson(mSharedPreferences.getString(key, ""), anonymousClass);
        }
    }

    public static <T> List<T> getList(Context context, String key, Class<T> anonymousClass) {
        List<T> result = null;
        try {
            SharedPreferences mSharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            Type founderListType = TypeToken.getParameterized(ArrayList.class, anonymousClass).getType();
            Gson gson = new Gson();

            result = gson.fromJson(mSharedPreferences.getString(key, ""), founderListType);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        if (result == null) {
            result = new ArrayList<>();
        }
        return result;
    }

    public static <T> void put(Context context, String key, T data) {
        SharedPreferences mSharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mSharedPreferences.edit();

        if (data instanceof String) {
            editor.putString(key, (String) data);
        } else if (data instanceof Boolean) {
            editor.putBoolean(key, (Boolean) data);
        } else if (data instanceof Float) {
            editor.putFloat(key, (Float) data);
        } else if (data instanceof Integer) {
            editor.putInt(key, (Integer) data);
        } else if (data instanceof Long) {
            editor.putLong(key, (Long) data);
        } else {
            Gson gson = new Gson();
            editor.putString(key, gson.toJson(data));
        }
        editor.apply();
    }
}
