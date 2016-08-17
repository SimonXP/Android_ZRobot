package com.robot.et.core.software.system.media;

import android.content.Context;
import android.media.AudioManager;

import com.robot.et.main.CustomApplication;

//调节系统音量大小
public class MediaManager {
    private static MediaManager instance = null;
    private AudioManager mAudioManager;

    private MediaManager() {
        mAudioManager = (AudioManager) CustomApplication.getInstance().getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
    }

    public static MediaManager getInstance() {
        if (instance == null) {
            synchronized (MediaManager.class) {
                if (instance == null) {
                    instance = new MediaManager();
                }
            }
        }
        return instance;
    }

    // 增加音量
    public void increaseVolume() {
        mAudioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC,
                AudioManager.ADJUST_RAISE, AudioManager.FX_FOCUS_NAVIGATION_UP);
    }

    // 降低音量
    public void reduceVolume() {
        if (getCurrentVolume() > 0) {
            mAudioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC,
                    AudioManager.ADJUST_LOWER, AudioManager.FX_FOCUS_NAVIGATION_UP);
        }
    }

    // 获取最大音量值
    public int getMaxVolume() {
        return mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
    }

    // 获取当前音量值
    public int getCurrentVolume() {
        return mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
    }

    // 设置当前音量
    public void setCurrentVolume(int volumeValue) {
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volumeValue, 0);
    }

    // 设置最大音量
    public void setMaxVolume() {
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, getMaxVolume(), 0);
    }

}
