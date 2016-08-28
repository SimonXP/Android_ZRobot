package com.robot.et.util;

import com.robot.et.core.software.iflytek.VoiceDictation;
import com.robot.et.core.software.iflytek.SpeechSynthesis;
import com.robot.et.core.software.iflytek.TextUnderstand;
import com.robot.et.core.software.turing.TuringUnderstander;

/**
 * Created by houdeming on 2016/7/25.
 */
public class SpeechlHandle {
    private static VoiceDictation speechRecognizer;
    private static SpeechSynthesis speechSynthesizer;
    private static TextUnderstand textUnderstander;
    private static TuringUnderstander turingUnderstander;

    public static void setSpeechRecognizer(VoiceDictation speechRecognizer) {
        SpeechlHandle.speechRecognizer = speechRecognizer;
    }

    public static void setSpeechSynthesizer(SpeechSynthesis speechSynthesizer) {
        SpeechlHandle.speechSynthesizer = speechSynthesizer;
    }

    public static void setTextUnderstander(TextUnderstand textUnderstander) {
        SpeechlHandle.textUnderstander = textUnderstander;
    }

    public static void setTuringUnderstander(TuringUnderstander turingUnderstander) {
        SpeechlHandle.turingUnderstander = turingUnderstander;
    }

    public static void startSpeak(int speakType, String speakContent) {
        if (speechSynthesizer != null) {
            speechSynthesizer.startSpeak(speakType, speakContent);
        }
    }

    public static void cancelSpeak() {
        if (speechSynthesizer != null) {
            speechSynthesizer.cancelSpeak();
        }
    }

    public static void startListen() {
        if (speechRecognizer != null) {
            speechRecognizer.startListen();
        }
    }

    public static void cancelListen() {
        if (speechRecognizer != null) {
            speechRecognizer.cancelListen();
        }
    }

    public static void understanderText(String content) {
        if (textUnderstander != null) {
            textUnderstander.understanderText(content);
        }
    }

    public static void turingUnderstander(String content) {
        if (turingUnderstander != null) {
            turingUnderstander.understanderText(content);
        }
    }

}
