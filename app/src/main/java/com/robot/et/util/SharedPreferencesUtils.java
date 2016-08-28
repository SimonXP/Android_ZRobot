package com.robot.et.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.text.TextUtils;

import com.robot.et.app.CustomApplication;

public class SharedPreferencesUtils {
    public static SharedPreferencesUtils instance = null;
    private SharedPreferences sharedPreferences;
    private Editor editor;

    private SharedPreferencesUtils() {
        sharedPreferences = CustomApplication.getInstance().getApplicationContext().getSharedPreferences(SharedPreferencesKeys.ET_ROBOT_PREFERENCES_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public synchronized static SharedPreferencesUtils getInstance() {
        if (instance == null) {
            instance = new SharedPreferencesUtils();
        }
        return instance;
    }

    public boolean putBoolean(String name, boolean value) {
        if (editor == null || TextUtils.isEmpty(name)) {
            return false;
        }
        editor.putBoolean(name, value);
        return true;
    }

    public Boolean getBoolean(String name, boolean defaultValue) {
        if (sharedPreferences == null || TextUtils.isEmpty(name)) {
            return defaultValue;
        }
        return sharedPreferences.getBoolean(name, defaultValue);
    }

    public boolean putString(String name, String value) {
        if (editor == null || TextUtils.isEmpty(name)) {
            return false;
        }
        editor.putString(name, value);
        return true;
    }

    public String getString(String name, String defaultValue) {
        if (sharedPreferences == null || TextUtils.isEmpty(name)) {
            return defaultValue;
        }
        return sharedPreferences.getString(name, defaultValue);
    }

    public boolean putLong(String name, long value) {
        if (editor == null || TextUtils.isEmpty(name)) {
            return false;
        }
        editor.putLong(name, value);
        return true;
    }

    public Long getLong(String name, long defaultValue) {
        if (sharedPreferences == null || TextUtils.isEmpty(name)) {
            return defaultValue;
        }
        return sharedPreferences.getLong(name, defaultValue);
    }

    public boolean putInt(String name, int value) {
        if (editor == null || TextUtils.isEmpty(name)) {
            return false;
        }
        editor.putInt(name, value);
        return true;
    }

    public int getInt(String name, int defaultValue) {
        if (sharedPreferences == null || TextUtils.isEmpty(name)) {
            return defaultValue;
        }
        return sharedPreferences.getInt(name, defaultValue);
    }

    public boolean putFloat(String name, float value) {
        if (editor == null || TextUtils.isEmpty(name)) {
            return false;
        }
        editor.putFloat(name, value);
        return true;
    }

    public Float getFolat(String name, float defaultValue) {
        if (sharedPreferences == null || TextUtils.isEmpty(name)) {
            return defaultValue;
        }
        return sharedPreferences.getFloat(name, defaultValue);
    }

    public boolean commitValue() {
        if (editor == null) {
            return false;
        }
        return editor.commit();
    }

    public boolean removeValue(String key) {
        if (TextUtils.isEmpty(key) || sharedPreferences == null || editor == null) {
            return false;
        }
        if (!sharedPreferences.contains(key)) {
            return false;
        }
        editor.remove(key);
        return true;
    }

    public boolean clearData() {
        if (editor == null) {
            return false;
        }
        editor.clear();
        return true;
    }

}
