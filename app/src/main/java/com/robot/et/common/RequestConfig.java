package com.robot.et.common;

/**
 * Created by houdeming on 2016/8/1.
 * 推送的类型
 */
public class RequestConfig {
    //视频
    public final static int JPUSH_CALL_VIDEO = 40004;
    //语音
    public final static int JPUSH_CALL_VOICE = 40001;
    //查看
    public final static int JPUSH_CALL_LOOK = 40007;
    //关闭声网
    public final static int JPUSH_CALL_CLOSE = 60000;
    //agora语音转视频
    public final static int JPUSH_CALL_VOICE_TO_VIDEO = 40014;
    //agora视频转语音
    public final static int JPUSH_CALL_VIDEO_TO_VOICE = 40041;
    //更新用户电话联系方式
    public final static int JPUSH_UPDATE_USERPHONE_INFO = 40003;
    //给本体设置闹钟
    public final static int JPUSH_ALARM = 70001;
    //机器人问答库
    public final static int JPUSH_ROBOT_LEARN = 70002;
    //个人问答库
    public final static int JPUSH_PERSON_LEARN = 70003;
    //音乐
    public final static int JPUSH_MUSIC = 40006;
    //故事
    public final static int JPUSH_STORY = 40008;
    //同步课堂
    public final static int JPUSH_SYNCHRONOUS_CLASSROOM = 40009;
    //十万个为什么
    public final static int JPUSH_THOUSANDS_WHY = 40010;
    //百科
    public final static int JPUSH_ENCYCLOPEDIAS = 40011;
    //音量调节
    public final static int JPUSH_VOLUME_ADJUST = 40017;
    //上一首
    public final static int JPUSH_UPPER = 40018;
    //下一首
    public final static int JPUSH_LOWER = 40019;
    //音乐暂停
    public final static int JPUSH_PAUSE = 40020;
    //获取媒体当前状态
    public final static int JPUSH_GET_MEDIASTATE = 40021;
    //表演剧本
    public final static int JPUSH_PLAY_SCRIPT = 40002;
    //机器人看世界
    public final static int JPUSH_ROBOT_LOOK_WORLD = 40012;
    //机器人说话
    public final static int JPUSH_ROBOT_SPEAK = 40013;
    //视觉学习
    public final static int JPUSH_VISUAL_LEARNING = 40015;
    //场景互动
    public final static int JPUSH_SCENE_INTERACTION = 40016;
    //本体媒体播放通知
    public final static int JPUSH_MEDIA_PLAY_NOTIFY = 40022;
    //本体巡逻移动轨迹
    public final static int JPUSH_PATROL_MOVING_TRACK = 40023;
    //录制动作
    public final static int JPUSH_RECORDING_ACTION = 40024;
    //删除留言
    public final static int JPUSH_DELETE_A_MESSAGE = 40025;
    //为某首歌曲编排舞蹈
    public final static int JPUSH_CHOREOGRAPHY_DANCE = 40026;
    //APP发来的提醒
    public final static int JPUSH_REMIND = 40027;
    //本体提示app提醒情况
    public final static int JPUSH_REPLY_REMIND = 40028;
    //图形编辑
    public final static int JPUSH_GRAPHIC_EDITOR = 40029;
    //嬉闹
    public final static int JPUSH_FROLIC = 40005;
    //蓝牙控制器
    public final static int JPUSH_BLUETOOTH_CONTROLLER = 40031;


    //机器人本体给APP推送code信息标示
    //返回给APP提醒的情况
    public final static String TO_APP_REMIND = "REPLY_REMIND";
    //返回给APP蓝牙控制家电的情况
    public final static String TO_APP_BLUETOOTH_CONTROLLER = "BLUETOOTH_CONTROLLER";

}
