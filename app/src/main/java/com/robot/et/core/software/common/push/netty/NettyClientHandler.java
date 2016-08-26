package com.robot.et.core.software.common.push.netty;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.robot.et.common.BroadcastAction;
import com.robot.et.common.DataConfig;
import com.robot.et.common.RequestConfig;
import com.robot.et.core.software.common.network.HttpManager;
import com.robot.et.core.software.common.network.NetResultParse;
import com.robot.et.core.software.common.network.NettyClientCallBack;
import com.robot.et.core.software.common.script.ScriptHandler;
import com.robot.et.core.software.common.script.TouchHandler;
import com.robot.et.core.software.common.speech.SpeechImpl;
import com.robot.et.core.software.system.media.MediaManager;
import com.robot.et.entity.JpushInfo;
import com.robot.et.util.AlarmRemindManager;
import com.robot.et.util.BroadcastEnclosure;
import com.robot.et.util.MusicManager;
import com.robot.et.util.RobotLearnManager;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class NettyClientHandler extends SimpleChannelInboundHandler<Object> implements NettyClientCallBack {
    private Context context;

    public NettyClientHandler(Context context) {
        this.context = context;
    }

    @Override
    protected void messageReceived(ChannelHandlerContext ctx, Object msg) throws Exception {
        Log.i("netty", "接受到netty服务器发来的消息msg===" + msg);
        String message = (String) msg;
        if (!TextUtils.isEmpty(message)) {
            setPushResult(message);
        }

    }

    //推送来的结果
    private void setPushResult(String result) {
        if (!TextUtils.isEmpty(result)) {
            JpushInfo info = NetResultParse.getJpushInfo(result);
            if (info != null) {
                String direction = info.getDirection();
                if (TextUtils.isEmpty(direction)) {
                    doPushResult(info);
                    return;
                }

                Log.i("netty", "direction===" + direction);
                if (TextUtils.isDigitsOnly(direction)) {
                    int moveKey = Integer.parseInt(direction);
                    if (moveKey < 10) {
                        Log.i("netty", "控制脚走");
                        BroadcastEnclosure.controlMoveByApp(context, moveKey);
                    } else {
                        int directionTurn = DataConfig.TURN_HEAD_ABOUT;
                        String angle = "";
                        //上下以垂直方向为0度，向前10度即-10，向后10度即+10  左右横向运动以正中为0度，向右10度即-10，向左10度即+10
                        switch (moveKey) {
                           case 11://头向前
                               Log.i("netty", "头向前");
                               directionTurn = DataConfig.TURN_HEAD_UP_DOWN;
                               angle = "-5";
                               break;
                           case 12://头向后
                               Log.i("netty", "头向后");
                               directionTurn = DataConfig.TURN_HEAD_UP_DOWN;
                               angle = "5";
                               break;
                           case 13://头向左
                               Log.i("netty", "头向左");
                               directionTurn = DataConfig.TURN_HEAD_ABOUT;
                               angle = "5";
                               break;
                           case 14://头向右
                               Log.i("netty", "头向右");
                               directionTurn = DataConfig.TURN_HEAD_ABOUT;
                               angle = "-5";
                               break;
                           default:
                               break;
                       }
                        BroadcastEnclosure.controlHead(context, directionTurn, angle);
                    }
                } else {
                    String splite = "__";
                    if (direction.contains(splite)) {//1_1(小车编号__方向指令)
                        String[] datas = direction.split(splite);
                        Log.i("netty", "datas[0]===" + datas[0]);
                        Log.i("netty", "datas[1]===" + datas[1]);
                        BroadcastEnclosure.controlToyCarMove(context, getIntNum(datas[1]), getIntNum(datas[0]));
                    }
                }
            }
        }
    }

    // 获取小车、方向的int值
    private int getIntNum(String result) {
        int num = 0;
        if (!TextUtils.isEmpty(result)) {
            if (TextUtils.isDigitsOnly(result)) {
                num = Integer.parseInt(result);
            }
        }
        return num;
    }

    // 处理推送的结果
    private void doPushResult(JpushInfo info) {
        if (info != null) {
            int extra = info.getExtra();
            String musicContent = info.getMusicContent();
            Log.i("netty", "pushCode===" + extra);
            Log.i("netty", "musicContent===" + musicContent);

            DataConfig.isJpushStop = false;

            switch (extra) {
                case RequestConfig.JPUSH_MUSIC:// 音乐
                    Log.i("netty", "音乐");
                    playMp3(RequestConfig.JPUSH_MUSIC, "MUSIC", musicContent);

                    break;
                case RequestConfig.JPUSH_STORY:// 故事
                    Log.i("netty", "故事");
                    playMp3(RequestConfig.JPUSH_STORY, "STORY", musicContent);

                    break;
                case RequestConfig.JPUSH_SYNCHRONOUS_CLASSROOM:// 同步课堂
                    Log.i("netty", "同步课堂");
                    playMp3(RequestConfig.JPUSH_SYNCHRONOUS_CLASSROOM, "SYNCHRONOUS_CLASSROOM", musicContent);

                    break;
                case RequestConfig.JPUSH_THOUSANDS_WHY:// 十万个为什么
                    Log.i("netty", "十万个为什么");
                    playMp3(RequestConfig.JPUSH_THOUSANDS_WHY, "THOUSANDS_WHY", musicContent);

                    break;
                case RequestConfig.JPUSH_ENCYCLOPEDIAS:// 百科
                    Log.i("netty", "百科");
                    playMp3(RequestConfig.JPUSH_ENCYCLOPEDIAS, "ENCYCLOPEDIAS", musicContent);

                    break;
                case RequestConfig.JPUSH_VOLUME_ADJUST:// 播放器音量控制
                    Log.i("netty", "播放器音量控制");
                    if (!TextUtils.isEmpty(musicContent)) {
                        MediaManager media = MediaManager.getInstance();
                        int currentValue = (int) (media.getMaxVolume() * Double.parseDouble(musicContent));
                        media.setCurrentVolume(currentValue);
                    }

                    break;
                case RequestConfig.JPUSH_UPPER:// 上一首
                    Log.i("netty", "上一首 currentMediaType===" + MusicManager.getCurrentMediaType());
                    playMp3(MusicManager.getCurrentMediaType(), MusicManager.getCurrentMediaName(), musicContent);

                    break;
                case RequestConfig.JPUSH_LOWER:// 下一首
                    Log.i("netty", "下一首 currentMediaType===" + MusicManager.getCurrentMediaType());
                    playMp3(MusicManager.getCurrentMediaType(), MusicManager.getCurrentMediaName(), musicContent);

                    break;
                case RequestConfig.JPUSH_PAUSE:// 音乐暂停
                    Log.i("netty", "音乐暂停");
                    DataConfig.isJpushStop = true;
                    SpeechImpl.getInstance().cancelSpeak();
                    BroadcastEnclosure.stopMusic(context);

                    break;
                case RequestConfig.JPUSH_GET_MEDIASTATE:// 获取媒体当前状态
                    Log.i("netty", "获取媒体当前状态");
                    if (DataConfig.isPlayMusic) {//正在播放
                        HttpManager.pushMediaState(MusicManager.getCurrentMediaName(), "open", MusicManager.getCurrentPlayName(), this);
                    } else {
                        HttpManager.pushMediaState("", "close", "", this);
                    }

                    break;
                case RequestConfig.JPUSH_ALARM:// 闹铃
                    Log.i("netty", "闹铃");
                    AlarmRemindManager.addAppAlarmClock(info);

                    break;
                case RequestConfig.JPUSH_REMIND:// APP提醒
                    Log.i("netty", "APP提醒");
                    AlarmRemindManager.addAppAlarmRemind(NetResultParse.parseAppRemind(musicContent));

                    break;
                case RequestConfig.JPUSH_ROBOT_LEARN:// 机器人问答库
                    Log.i("netty", "机器人问答库");
                    RobotLearnManager.insertLeanInfo(info.getQuestion(), info.getAnswer(), "", DataConfig.LEARN_BY_ROBOT);

                    break;
                case RequestConfig.JPUSH_ROBOT_SPEAK:// 机器人学习库，通过说话学习
                    Log.i("netty", "机器人问答库通过说话学习");
                    RobotLearnManager.learnByAppSpeak(DataConfig.LEARN_BY_ROBOT, musicContent);

                    break;
                case RequestConfig.JPUSH_PERSON_LEARN:// 个人问答库
                    Log.i("netty", "个人问答库");
                    RobotLearnManager.insertLeanInfo(info.getQuestion(), info.getAnswer(), "", DataConfig.LEARN_BY_PERSON);

                    break;
                case RequestConfig.JPUSH_UPDATE_USERPHONE_INFO:// 更新用户联系方式
                    Log.i("netty", "更新用户联系方式");
                    //do  nothing
                    break;
                case RequestConfig.JPUSH_PLAY_SCRIPT:// 表演剧本
                    Log.i("netty", "表演剧本");
                    if (DataConfig.isVideoOrVoice) {
                        return;
                    }
                    ScriptHandler.playScriptStart(context);
                    ScriptHandler.playScript(context, musicContent);

                    break;
                case RequestConfig.JPUSH_PATROL_MOVING_TRACK:// 本体巡逻移动轨迹
                    Log.i("netty", "本体巡逻移动轨迹");
                    //do nothing
                    break;
                case RequestConfig.JPUSH_RECORDING_ACTION:// 录制动作
                    Log.i("netty", "录制动作");
                    ScriptHandler.addAppRecordAction(musicContent);

                    break;
                case RequestConfig.JPUSH_DELETE_A_MESSAGE:// 删除留言
                    Log.i("netty", "删除留言");
                    AlarmRemindManager.deleteAppRemindTips(musicContent);

                    break;
                case RequestConfig.JPUSH_CHOREOGRAPHY_DANCE:// 为某首歌曲编排舞蹈
                    Log.i("netty", "为某首歌曲编排舞蹈");
                    ScriptHandler.addAppRecordMusic(musicContent);

                    break;
                case RequestConfig.JPUSH_SCENE_INTERACTION:// 场景互动
                    Log.i("netty", "场景互动");
                    if (DataConfig.isVideoOrVoice) {
                        return;
                    }
                    ScriptHandler.playScriptStart(context);
                    ScriptHandler.playScript(context, musicContent);

                    break;
                case RequestConfig.JPUSH_GRAPHIC_EDITOR:// 图形编辑
                    Log.i("netty", "图形编辑");
                    ScriptHandler.addAppGraphicEdit(musicContent);

                    break;
                case RequestConfig.JPUSH_FROLIC:// 嬉闹
                    Log.i("netty", "嬉闹");
                    if (DataConfig.isVideoOrVoice) {
                        return;
                    }
                    ScriptHandler.playScriptStart(context);
                    TouchHandler.responseTouch(context, musicContent);

                    break;

                default:// agora音视频
                    Log.i("netty", "agora音视频");
                    Intent intent = new Intent();
                    intent.setAction(BroadcastAction.ACTION_JOIN_AGORA_ROOM);
                    intent.putExtra("JpushInfo", info);
                    context.sendBroadcast(intent);

                    break;
            }

        }
    }

    //播放.mp3文件
    private void playMp3(int mediaType, String mediaName, String musicName) {
        MusicManager.setCurrentMediaType(mediaType);
        MusicManager.setCurrentMediaName(mediaName);
        MusicManager.setCurrentPlayName(musicName);

        HttpManager.pushMediaState(mediaName, "open", musicName, this);
        if (DataConfig.isVideoOrVoice) {
            return;
        }
        SpeechImpl.getInstance().cancelSpeak();
        BroadcastEnclosure.stopMusic(context);
        SpeechImpl.getInstance().cancelListen();
        DataConfig.isJpushPlayMusic = true;
        String speakContent = MusicManager.getMusicSpeakContent(DataConfig.MUSIC_SRC_FROM_JPUSH, mediaType, musicName);
        SpeechImpl.getInstance().startSpeak(DataConfig.SPEAK_TYPE_MUSIC_START, speakContent);
    }

    @Override
    public void connect(String result) {
        BroadcastEnclosure.connectNetty(context);
    }

}
