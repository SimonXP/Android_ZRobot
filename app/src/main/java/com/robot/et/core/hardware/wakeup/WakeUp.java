package com.robot.et.core.hardware.wakeup;

public class WakeUp {

    static {
        System.loadLibrary("WakeUp");
    }

    public static native int wakeUpInit();

    public static native int getWakeUpState();

    public static native int getWakeUpDegree();

    public static native void setGainDirection(int direction);

    public static native int wakeUpReset();

    public static native int faceWakeUpInit();

    public static native int getFaceWakeUpState();

    public static native int close();

}
