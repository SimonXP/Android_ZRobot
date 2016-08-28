package com.robot.et.core.software.common.speech;

import com.robot.et.core.software.voice.iflytek.IflySpeakService;
import com.robot.et.core.software.voice.iflytek.IflyTextUnderstanderService;
import com.robot.et.core.software.voice.iflytek.IflyVoiceToTextService;
import com.robot.et.core.software.voice.turing.TuRingService;

/**
 * Created by houdeming on 2016/8/8.
 */
public class SpeechImpl implements Speech {
    private static SpeechImpl instance = null;
    private static IflySpeakService speakService;
    private static IflyVoiceToTextService voiceToTextService;
    private static IflyTextUnderstanderService understanderService;
    private static TuRingService tuRingService;

    private SpeechImpl() {
    }

    public static SpeechImpl getInstance() {
        if (instance == null) {
            synchronized (SpeechImpl.class) {
                if (instance == null) {
                    instance = new SpeechImpl();
                }
            }
        }
        return instance;
    }

    @Override
    public void startSpeak(int speakType, String speakContent) {
        if (speakService != null) {
            speakService.startSpeak(speakType, speakContent);
        }
    }

    @Override
    public void cancelSpeak() {
        if (speakService != null) {
            speakService.cancelSpeak();
        }
    }

    @Override
    public void startListen() {
        if (voiceToTextService != null) {
            voiceToTextService.startListen();
        }
    }

    @Override
    public void cancelListen() {
        if (voiceToTextService != null) {
            voiceToTextService.cancelListen();
        }
    }

    @Override
    public void understanderTextByIfly(String content) {
        if (understanderService != null) {
            understanderService.understanderTextByIfly(content);
        }
    }

    @Override
    public void understanderTextByTuring(String content) {
        if (tuRingService != null) {
            tuRingService.understanderTextByTuring(content);
        }
    }

    public static void setService(IflySpeakService service) {
        SpeechImpl.speakService = service;
    }

    public static void setService(IflyVoiceToTextService service) {
        SpeechImpl.voiceToTextService = service;
    }

    public static void setService(IflyTextUnderstanderService service) {
        SpeechImpl.understanderService = service;
    }

    public static void setService(TuRingService service) {
        SpeechImpl.tuRingService = service;
    }

}
