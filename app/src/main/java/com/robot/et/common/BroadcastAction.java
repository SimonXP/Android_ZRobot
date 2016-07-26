package com.robot.et.common;

/**
 * Created by Tony on 2016/7/25.
 */
public class BroadcastAction {
    //串口数据接收
    public static String ACTION_MOVE_TO_SERIALPORT ="action.control.robot.serialport";
    //唤醒或打断并且转向的广播
    public static String ACTION_WAKE_UP_AND_MOVE = "action.wake.up.and.move";
    //重置语音板的广播
    public static String ACTION_WAKE_UP_RESET = "action.wake.up.reset";
    //开始播放音乐
    public static String ACTION_PLAY_MUSIC_START = "action.play.music.start";
    //音乐播放完成
    public static String ACTION_PLAY_MUSIC_END = "action.play.music.end";
    //停止音乐播放
    public static String ACTION_STOP_MUSIC = "action.stop.music";

    //开始听(耳朵)
    public static String ACTION_START_LISTEN = "action.start.listen";
    //停止听(耳朵)
    public static String ACTION_STOP_LISTEN = "action.stop.listen";

    //开始理解(大脑)
    public static String ACTION_START_UNDERSTAND = "action.start.understand";
    //停止理解(大脑)
    public static String ACTION_STOP_UNDERSTAND = "action.stop.understand";

    //开始说话（嘴巴）
    public static String ACTION_START_SPEAK = "action.start.speak";
    //停止说话（嘴巴）
    public static String ACTION_STOP_SPEAK = "action.stop.speak";

}
