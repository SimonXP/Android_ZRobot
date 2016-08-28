package com.robot.et.common;

/**
 * Created by Tony on 2016/7/25.
 */
public class BroadcastAction {
    //串口数据接收
    public static String ACTION_MOVE_TO_SERIALPORT ="action.control.robot.serialport";
    //唤醒,打断或者转向的广播
    public static String ACTION_WAKE_UP_OR_INTERRUPT = "action.wake.up.or.interrupt";
    //重置语音板的广播
    public static String ACTION_WAKE_UP_RESET = "action.wake.up.reset";
    //开始播放音乐
    public static String ACTION_PLAY_MUSIC_START = "action.play.music.start";
    //音乐播放完成
    public static String ACTION_PLAY_MUSIC_END = "action.play.music.end";
    //停止音乐播放
    public static String ACTION_STOP_MUSIC = "action.stop.music";
    //说话
    public static String ACTION_SPEAK = "action.speak";
    //脸部识别
    public static String ACTION_FACE_DISTINGUISH = "action.face.distinguish";
    //控制机器人移动(Netty指令)
    public static String ACTION_CONTROL_ROBOT_MOVE_WITH_NETTY = "action.control.robot.move.with.netty";
    //控制机器人移动（语音控制）
    public static String ACTION_CONTROL_ROBOT_MOVE_WITH_VOICE = "action.control.robot.move.with.voice";
    //打开netty的广播
    public static String ACTION_OPEN_NETTY = "action.open.netty";
    //控制机器人周围小车的广播
    public static String ACTION_CONTROL_AROUND_TOYCAR = "action.control.around.toycar";
    //控制机器人表情
    public static String ACTION_CONTROL_ROBOT_EMOTION = "action.contorl.robot.emotion";
    //摆手的广播
    public static String ACTION_CONTROL_WAVING = "action.control.waving";
    //控制机器人嘴LED灯的广播
    public static String ACTION_CONTROL_MOUTH_LED = "action.control.mouth.led";
    //跟随的广播
    public static String ACTION_CONTROL_ROBOT_FOLLOW = "action.control.robot.follow";
    //控制机器人转圈
    public static String ACTION_CONTROL_ROBOT_TURN ="action.control.robot.turn";
    //控制小车不停车的广播
    public static String ACTION_CONTROL_TOYCAR_AWAYS = "action.control.toycar.aways";
    //硬件接受到信息通知软件的广播
    public static String ACTION_NOTIFY_SOFTWARE = "action.notify.software";

}
