package com.robot.et.core.software.iflytek;

/**
 * Created by houdeming on 2016/7/25.
 * 语音合成接口
 */
public interface SpeechSynthesis {
    //开始语音合成
    void startSpeak(int speakType, String speakContent);

    //取消语音合成
    void cancelSpeak();

}
