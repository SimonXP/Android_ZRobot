package com.robot.et.core.software.iflytek.event;

/**
 * 讯飞识别结果POJO
 * Created by Tony on 2016/8/4.
 */
public class SpeechRecognizeResultEvent {

    public final String result;

    public SpeechRecognizeResultEvent(String result) {
        this.result = result;
    }
}
