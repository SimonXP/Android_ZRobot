package com.robot.et.core.software.common.script;

import android.content.Context;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;

import com.robot.et.common.DataConfig;
import com.robot.et.common.RequestConfig;
import com.robot.et.common.ScriptConfig;
import com.robot.et.core.software.common.speech.SpeechImpl;
import com.robot.et.db.RobotDB;
import com.robot.et.entity.ScriptActionInfo;
import com.robot.et.entity.ScriptInfo;
import com.robot.et.util.BroadcastEnclosure;
import com.robot.et.util.EnumManager;
import com.robot.et.util.FileUtils;
import com.robot.et.util.MatchStringUtil;
import com.robot.et.util.MusicManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by houdeming on 2016/7/6.
 */
public class ScriptHandler implements Script {

    //表演剧本
    public static void playScript(Context context, String content) {
        if (!TextUtils.isEmpty(content)) {
            BroadcastEnclosure.controlWaving(context, ScriptConfig.HAND_STOP, ScriptConfig.HAND_TWO, "0");
            List<ScriptActionInfo> infos = getScriptActions(content);
            Log.i("netty", "playScript() infos.size()====" + infos.size());
            if (infos != null && infos.size() > 0) {
                DataConfig.isPlayScript = true;
                DataConfig.isSleep = false;
                doScriptAction(context, infos);
            }
        }
    }

    //执行剧本剧本动作
    public static void doScriptAction(Context context, List<ScriptActionInfo> infos) {
        Log.i("netty", "doScriptAction()   infos.size()====" + infos.size());
        if (infos != null && infos.size() > 0) {
            ScriptActionInfo info = infos.get(0);
            String content = info.getContent();
            Log.i("netty", "doScriptAction() info.getContent()====" + content);
            switch (info.getActionType()) {
                case ScriptConfig.SCRIPT_EXPRESSION://表情
                    Log.i("netty", "doScriptAction() 表情");
                    int emotionKey = EnumManager.getEmotionKey(content);
                    Log.i("netty", "doScriptAction() emotionKey====" + emotionKey);
                    BroadcastEnclosure.controlRobotEmotion(context, emotionKey);
                    handleNewScriptInfos(context, infos, true, getDealyTime(2000));

                    break;
                case ScriptConfig.SCRIPT_MUSIC://音乐
                    Log.i("netty", "doScriptAction() 音乐");
                    ScriptManager.setScriptActionInfos(infos);
                    DataConfig.isScriptPlayMusic = true;
                    DataConfig.isJpushPlayMusic = true;
                    playScriptMusic(context, RequestConfig.JPUSH_MUSIC, content, info.getSpareContent());

                    break;
                case ScriptConfig.SCRIPT_FOLLOW://跟随
                    Log.i("netty", "doScriptAction() 跟随");
                    String robotNum = "";
                    int toyCarNum = 0;
                    if (content.contains("机器人")) {
                        robotNum = info.getSpareContent();
                    } else if (content.contains("小车")) {
                        toyCarNum = MatchStringUtil.getToyCarNum(content);
                    }
                    Log.i("netty", "doScriptAction() robotNum====" + robotNum);
                    Log.i("netty", "doScriptAction() toyCarNum====" + toyCarNum);
                    BroadcastEnclosure.controlFollow(context, robotNum, toyCarNum);
                    handleNewScriptInfos(context, infos, true, getDealyTime(2000));

                    break;
                case ScriptConfig.SCRIPT_TURN_AROUND://转圈
                    Log.i("netty", "doScriptAction() 转圈");
                    int direction = ScriptManager.getTurnDirection(content);
                    Log.i("netty", "doScriptAction() direction====" + direction);
                    Log.i("netty", "doScriptAction() num====" + info.getSpareContent());
                    BroadcastEnclosure.controlTurnAround(context, direction, info.getSpareContent());
                    handleNewScriptInfos(context, infos, true, getDealyTime(2000));

                    break;
                case ScriptConfig.SCRIPT_QUESTION_ANSWER://问答
                    Log.i("netty", "doScriptAction() 问答");
                    ScriptManager.setScriptActionInfos(infos);
                    ScriptManager.setScriptAnswer("");
                    String requireAnswer = info.getSpareContent();
                    Log.i("netty", "doScriptAction() answer===" + requireAnswer);
                    String speakContent = "";
                    if (!TextUtils.isEmpty(requireAnswer)) {
                        DataConfig.isScriptQA = true;
                        ScriptManager.setScriptAnswer(requireAnswer);
                        speakContent = new StringBuffer(1024).append(content).append("，请回答：").append(requireAnswer).toString();
                    } else {
                        speakContent = content;
                    }
                    SpeechImpl.getInstance().startSpeak(DataConfig.SPEAK_TYPE_SCRIPT, speakContent);

                    break;
                case ScriptConfig.SCRIPT_SPEAK://说话
                    Log.i("netty", "doScriptAction() 说话");
                    ScriptManager.setScriptActionInfos(infos);
                    SpeechImpl.getInstance().startSpeak(DataConfig.SPEAK_TYPE_SCRIPT, content);

                    break;
                case ScriptConfig.SCRIPT_HAND://手
                    Log.i("netty", "doScriptAction() 手");
                    String handDirection = ScriptManager.getHandDirection(info.getSpareContent());
                    String handCategory = ScriptManager.getHandCategory(content);
                    Log.i("netty", "doScriptAction() handDirection===" + handDirection);
                    ScriptManager.setScriptActionInfos(infos);
                    BroadcastEnclosure.controlWaving(context, handDirection, handCategory, "1");

                    break;
                case ScriptConfig.SCRIPT_MOVE://走
                    Log.i("netty", "doScriptAction() 走");
                    int moveDirection = EnumManager.getMoveKey(content);
                    String spareContent = info.getSpareContent();
                    Log.i("netty", "spareContent==" + spareContent);
                    int num = 0;
                    ScriptManager.setScriptActionInfos(infos);
                    if (!TextUtils.isEmpty(spareContent)) {
                        if (spareContent.contains("小车")) {
                            num = MatchStringUtil.getToyCarNum(spareContent);
                            BroadcastEnclosure.controlToyCarMove(context, moveDirection, num);
                        } else {
                            BroadcastEnclosure.controlMoveByApp(context, moveDirection);
                        }
                    }

                    new ScriptHandler().scriptAction(context);

                    break;
                case ScriptConfig.SCRIPT_TURN://左转右转
                    Log.i("netty", "doScriptAction() 左转右转");
                    int turnDirection = EnumManager.getMoveKey(content);
                    ScriptManager.setScriptActionInfos(infos);
                    BroadcastEnclosure.controlMoveByApp(context, turnDirection);


                    new ScriptHandler().scriptAction(context);

                    break;
                case ScriptConfig.SCRIPT_STOP://停止
                    Log.i("netty", "doScriptAction() 停止");
                    BroadcastEnclosure.controlWaving(context, ScriptConfig.HAND_STOP, ScriptConfig.HAND_TWO, "0");
                    handleNewScriptInfos(context, infos, true, getDealyTime(2000));

                    break;
                default:
                    break;
            }

        } else {
            Log.i("netty", "doScriptAction()剧本执行完毕");
            playScriptEnd(context);
        }
    }

    //更新剧本的内容
    public static void handleNewScriptInfos(Context context, List<ScriptActionInfo> infos, boolean isResume, long delayTime) {
        if (DataConfig.isAppPushRemind) {
            DataConfig.isAppPushRemind = false;
            return;
        }

        if (infos != null && infos.size() > 0) {
            infos.remove(0);
            ScriptManager.setScriptActionInfos(infos);
            SystemClock.sleep(delayTime);

            if (isResume) {
                if (DataConfig.isPlayScript) {
                    doScriptAction(context, infos);
                } else {
                    Log.i("netty", "DataConfig.isPlayScript===false 剧本执行完毕");
                    playScriptEnd(context);
                }
            }
        } else {
            Log.i("netty", "setNewScriptInfos()剧本执行完毕");
            playScriptEnd(context);
        }
    }

    //表演剧本之前
    public static void playScriptStart(Context context) {
        SpeechImpl.getInstance().cancelSpeak();
        SpeechImpl.getInstance().cancelListen();
        BroadcastEnclosure.stopMusic(context);
        BroadcastEnclosure.controlMouthLED(context, ScriptConfig.LED_OFF);
    }

    //剧本执行完毕
    private static void playScriptEnd(Context context) {
        Log.i("netty", "playScriptEnd()");
        DataConfig.isPlayScript = false;
        if (!DataConfig.isPlayMusic) {
            SystemClock.sleep(2000);
            BroadcastEnclosure.controlMouthLED(context, ScriptConfig.LED_OFF);
            BroadcastEnclosure.controlWaving(context, ScriptConfig.HAND_STOP, ScriptConfig.HAND_TWO, "0");
        }
        //重连netty
        BroadcastEnclosure.connectNetty(context);

    }

    private static long getDealyTime(long custormTime) {
        long time = 0;
        if (DataConfig.isPlayMusic) {
            time = 4000;
        } else {
            time = custormTime;
        }
        return time;
    }

    //播放剧本音乐
    private static void playScriptMusic(Context context, int mediaType, String content, String url) {
        String musicSrc = "";
        if (!TextUtils.isEmpty(content)) {
            musicSrc = MusicManager.getDetailMusicSrc(mediaType, content);
        } else {
            musicSrc = url;
        }
        BroadcastEnclosure.startPlayMusic(context, musicSrc);
    }

    //插入本地剧本
    public static void addLocalScript(Context context, String scriptName) {
        String content = FileUtils.readFile(context, scriptName, "utf-8");
        ScriptParse.parseScript(content, new ScriptInfoCallBack() {
            @Override
            public void getScribt(ScriptInfo info, List<ScriptActionInfo> infos) {
                if (info != null) {
                    Log.i("netty", "addScript  size==" + infos.size());
                    if (infos != null && infos.size() > 0) {
                        addScript(info, infos);
                    }
                }
            }
        });

    }

    //增加APP发来的图形编辑
    public static void addAppGraphicEdit(String content) {
        if (!TextUtils.isEmpty(content)) {
            ScriptParse.parseScript(content, new ScriptInfoCallBack() {
                @Override
                public void getScribt(ScriptInfo info, List<ScriptActionInfo> infos) {
                    if (info != null) {
                        Log.i("netty", "addScript  size==" + infos.size());
                        if (infos != null && infos.size() > 0) {
                            addScript(info, infos);
                        }
                    }
                }
            });
        }
    }

    //增加APP发过来的录制动作
    public static void addAppRecordAction(String content) {
        if (!TextUtils.isEmpty(content)) {
            ScriptParse.parseAppRecordAction(content, new ScriptInfoCallBack() {
                @Override
                public void getScribt(ScriptInfo info, List<ScriptActionInfo> infos) {
                    if (info != null) {
                        Log.i("netty", "addScript  size==" + infos.size());
                        if (infos != null && infos.size() > 0) {
                            addScript(info, infos);
                        }
                    }
                }
            });
        }
    }

    //增加APP发过来的音乐编舞
    public static void addAppRecordMusic(String content) {
        if (!TextUtils.isEmpty(content)) {
            ScriptParse.parseAppRecordMusic(content, new ScriptInfoCallBack() {
                @Override
                public void getScribt(ScriptInfo info, List<ScriptActionInfo> infos) {
                    if (info != null) {
                        Log.i("netty", "addScript  size==" + infos.size());
                        if (infos != null && infos.size() > 0) {
                            addScript(info, infos);
                        }
                    }
                }
            });
        }
    }

    //增加剧本
    private static void addScript(ScriptInfo info, List<ScriptActionInfo> infos) {
        RobotDB mDao = RobotDB.getInstance();
        String scriptName = info.getScriptContent();
        int scriptId = mDao.getScriptId(scriptName);
        Log.i("netty", "addScript temId===" + scriptId);
        if (scriptId != -1) {//已经存在
            Log.i("netty", "addScript 数据内容已存在");
            mDao.deleteScriptAction(scriptId);
        } else {//没有存在
            Log.i("netty", "addScript 无数据内容");
            mDao.addScript(info);
            scriptId = mDao.getScriptId(scriptName);
            Log.i("netty", "addScript scriptId===" + scriptId);
        }

        if (infos != null && infos.size() > 0) {
            for (ScriptActionInfo actionInfo : infos) {
                actionInfo.setScriptId(scriptId);
                mDao.addScriptAction(actionInfo);
            }
            Log.i("netty", "addScript 加入数据库成功");
        }
    }

    //获取剧本执行的动作
    private static List<ScriptActionInfo> getScriptActions(String scriptContent) {
        List<ScriptActionInfo> infos = new ArrayList<ScriptActionInfo>();
        if (!TextUtils.isEmpty(scriptContent)) {
            RobotDB mDao = RobotDB.getInstance();
            int scriptId = mDao.getScriptId(scriptContent);
            Log.i("netty", "getScriptActions()  scriptId====" + scriptId);
            if (scriptId != -1) {
                infos = mDao.getScriptActionInfos(scriptId);
            }
        }
        return infos;
    }

    @Override
    public void scriptPlayMusic(Context context, boolean isStart) {
        if (isStart) {
            if (DataConfig.isScriptPlayMusic) {
                DataConfig.isScriptPlayMusic = false;
                handleNewScriptInfos(context, ScriptManager.getScriptActionInfos(), true, getDealyTime(0));
            } else {
                playScript(context, MusicManager.getCurrentPlayName());
            }
        }
    }

    @Override
    public void scriptSpeak(Context context) {
        handleNewScriptInfos(context, ScriptManager.getScriptActionInfos(), true, getDealyTime(0));
    }

    @Override
    public void scriptAction(Context context) {
        handleNewScriptInfos(context, ScriptManager.getScriptActionInfos(), true, getDealyTime(2000));
    }

    @Override
    public void appScriptQA(Context context, String result) {
        if (!TextUtils.isEmpty(result)) {
            String answer = ScriptManager.getScriptAnswer();
            if (!TextUtils.isEmpty(answer)) {
                if (result.contains(answer)) {//回答正确
                    DataConfig.isScriptQA = false;
                    scriptSpeak(context);
                } else {//回答错误
                    SpeechImpl.getInstance().startListen();
                }
            }
        }
    }

}
