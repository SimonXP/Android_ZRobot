package com.robot.et.core.software.voice;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.robot.et.util.SharedPreferencesKeys;
import com.robot.et.util.SharedPreferencesUtils;

/**
 * Created by houdeming on 2016/8/8.
 */
public class SpeechService extends Service {

    public SharedPreferencesUtils share;
    public String city;
    public String area;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        share = SharedPreferencesUtils.getInstance();
        city = share.getString(SharedPreferencesKeys.CITY_KEY, "");
        area = share.getString(SharedPreferencesKeys.AREA_KEY, "");

    }

    public void startSpeak(int speakType, String speakContent) {
    }

    public void cancelSpeak() {
    }

    public void startListen() {
    }

    public void cancelListen() {
    }

    public void understanderTextByIfly(String content) {
    }

    public void understanderTextByTuring(String content) {
    }

}
