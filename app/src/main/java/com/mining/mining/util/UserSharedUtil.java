package com.mining.mining.util;

import android.content.Context;
import android.content.SharedPreferences;

public class UserSharedUtil {

    private static SharedPreferences mSharedPreferences;
    private static SharedPreferences.Editor mEditor;

    public static void init(Context context, String name) {
        mSharedPreferences = context.getSharedPreferences(name, Context.MODE_PRIVATE);
        mEditor = mSharedPreferences.edit();
    }

    public static void save(String key, Object value) {
        if (value instanceof String) {
            mEditor.putString(key, (String) value);
        } else if (value instanceof Integer) {
            mEditor.putInt(key, (Integer) value);
        } else if (value instanceof Long) {
            mEditor.putLong(key, (Long) value);
        } else if (value instanceof Float) {
            mEditor.putFloat(key, (Float) value);
        } else if (value instanceof Boolean) {
            mEditor.putBoolean(key, (Boolean) value);
        }
        mEditor.apply();
    }

    public static Object get(String key, Object defaultValue) {
        if (defaultValue instanceof String) {
            return mSharedPreferences.getString(key, (String) defaultValue);
        } else if (defaultValue instanceof Integer) {
            return mSharedPreferences.getInt(key, (Integer) defaultValue);
        } else if (defaultValue instanceof Long) {
            return mSharedPreferences.getLong(key, (Long) defaultValue);
        } else if (defaultValue instanceof Float) {
            return mSharedPreferences.getFloat(key, (Float) defaultValue);
        } else if (defaultValue instanceof Boolean) {
            return mSharedPreferences.getBoolean(key, (Boolean) defaultValue);
        }
        return null;
    }

    public static void remove(String key) {
        mEditor.remove(key).apply();
    }
}
