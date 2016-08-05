package com.robot.et.core.software.custorm;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.robot.et.R;
import com.robot.et.common.BroadcastAction;
import com.robot.et.common.DataConfig;
import com.robot.et.common.RequestConfig;
import com.robot.et.common.ScriptConfig;
import com.robot.et.common.enums.MatchSceneEnum;
import com.robot.et.util.FaceDataControl;
import com.robot.et.core.software.netty.NettyClientHandler;
import com.robot.et.core.software.script.ScriptHandler;
import com.robot.et.core.software.system.media.MediaManager;
import com.robot.et.core.software.window.network.HttpManager;
import com.robot.et.entity.LearnAnswerInfo;
import com.robot.et.entity.ResponseAppRemindInfo;
import com.robot.et.entity.ScriptActionInfo;
import com.robot.et.util.AlarmRemindManager;
import com.robot.et.util.BroadcastEnclosure;
import com.robot.et.util.EnumManager;
import com.robot.et.util.MatchStringUtil;
import com.robot.et.util.RobotLearnManager;
import com.robot.et.util.SpeechlHandle;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by houdeming on 2016/7/28.
 */
public class commandImpl implements command {

    private Context context;

    public commandImpl(Context context) {
        this.context = context;
    }

    public boolean isCustorm(String result) {
        if (!TextUtils.isEmpty(result)) {
            if (isAppPushRemind(result)) {
                return true;
            }

            if (isScriptQA(result)) {
                return true;
            }

            if (isMatchScene(result)) {
                return true;
            }

            if (isControlMove(result)) {
                return true;
            }

            if (isCustomDialogue(result)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean isMatchScene(String result) {
        MatchSceneEnum sceneEnum = EnumManager.getScene(result);
        Log.i("ifly", "sceneEnum=====" + sceneEnum);
        if (sceneEnum == null) {
            DataConfig.isFaceDetector = false;
            LearnAnswerInfo info = RobotLearnManager.getRobotLearnInfo(context, result);
            String content = info.getAnswer();//回答的话语
            boolean flag = false;
            if (!TextUtils.isEmpty(content)) {
                SpeechlHandle.startSpeak(DataConfig.SPEAK_TYPE_CHAT, content);
                flag = true;
            }
            return flag;
        }

        boolean flag = false;
        switch (sceneEnum) {
            case VOICE_BIGGEST_SCENE:// 声音最大
                flag = true;
                MediaManager.getInstance(context).setMaxVolume();
                SpeechlHandle.startSpeak(DataConfig.SPEAK_TYPE_CHAT, "音量已经最大");

                break;
            case VOICE_LITTEST_SCENE:// 声音最小
                flag = true;
                MediaManager.getInstance(context).setCurrentVolume(6);
                SpeechlHandle.startSpeak(DataConfig.SPEAK_TYPE_CHAT, "音量已经最小");

                break;
            case VOICE_BIGGER_INDIRECT_SCENE:// 间接增加声音
                flag = true;
                MediaManager.getInstance(context).increaseVolume();
                SpeechlHandle.startSpeak(DataConfig.SPEAK_TYPE_CHAT, "音量增加");

                break;
            case VOICE_LITTER_INDIRECT_SCENE://间接降低声音
                flag = true;
                MediaManager.getInstance(context).reduceVolume();
                SpeechlHandle.startSpeak(DataConfig.SPEAK_TYPE_CHAT, "音量减小");

                break;
            case VOICE_BIGGER_SCENE:// 直接增加声音
                flag = true;
                MediaManager.getInstance(context).increaseVolume();
                SpeechlHandle.startSpeak(DataConfig.SPEAK_TYPE_CHAT, "音量增加");

                break;
            case VOICE_LITTER_SCENE://直接降低声音
                flag = true;
                MediaManager.getInstance(context).reduceVolume();
                SpeechlHandle.startSpeak(DataConfig.SPEAK_TYPE_CHAT, "音量减小");

                break;
            case QUESTION_ANSWER_SCENE:// 智能学习回答话语
                flag = true;
                String content = RobotLearnManager.learnBySpeak(context, DataConfig.LEARN_BY_ROBOT, result);
                SpeechlHandle.startSpeak(DataConfig.SPEAK_TYPE_CHAT, content);

                break;
            case DISTURB_OPEN_SCENE:// 免打扰开
                flag = true;
                HttpManager.changeRobotCallStatus(DataConfig.ROBOT_STATUS_DISYURB_NOT);
                SpeechlHandle.startSpeak(DataConfig.SPEAK_TYPE_CHAT, "好的，进入免打扰模式");

                break;
            case DISTURB_CLOSE_SCENE:// 免打扰关
                flag = true;
                HttpManager.changeRobotCallStatus(DataConfig.ROBOT_STATUS_NORMAL);
                SpeechlHandle.startSpeak(DataConfig.SPEAK_TYPE_CHAT, "好的，免打扰模式已关闭");

                break;
            case SHUT_UP_SCENE:// 闭嘴
                flag = true;
                SpeechlHandle.startSpeak(DataConfig.SPEAK_TYPE_DO_NOTHINF, "好的,我去玩去了");
                Intent intent = new Intent();
                intent.setAction(BroadcastAction.ACTION_WAKE_UP_RESET);
                context.sendBroadcast(intent);

                break;
            case DO_ACTION_SCENE:// 智能学习做动作
                flag = false;

                break;
            case CONTROL_TOYCAR_SCENE:// 控制玩具车
                flag = true;
                DataConfig.isControlToyCar = true;
                int toyCarNum = MatchStringUtil.getToyCarNum(result);
                Log.i("ifly", "toyCarNum=====" + toyCarNum);
                setToyCarNum(toyCarNum);
                SpeechlHandle.startSpeak(DataConfig.SPEAK_TYPE_CHAT, "好的");

                break;
            case RAISE_HAND_SCENE:// 抬手
                flag = true;
                hand(1, ScriptConfig.HAND_RIGHT);

                break;
            case WAVING_SCENE:// 摆手
                flag = true;
                hand(1, ScriptConfig.HAND_TWO);

                break;
            case OPEN_HOUSEHOLD_SCENE:// 打开家电
                flag = true;
                SpeechlHandle.startSpeak(DataConfig.SPEAK_TYPE_CHAT, "好的");
                HttpManager.pushMsgToApp("开", RequestConfig.TO_APP_BLUETOOTH_CONTROLLER, new NettyClientHandler(context));

                break;
            case CLOSE_HOUSEHOLD_SCENE:// 关闭家电
                flag = true;
                SpeechlHandle.startSpeak(DataConfig.SPEAK_TYPE_CHAT, "好的");
                HttpManager.pushMsgToApp("关", RequestConfig.TO_APP_BLUETOOTH_CONTROLLER, new NettyClientHandler(context));

                break;
            case FACE_NAME_SCENE:// 脸部名称
                if (DataConfig.isFaceDetector) {
                    DataConfig.isFaceDetector = false;
                    String faceName = MatchStringUtil.getFaceName(result);
                    Log.i("ifly", "faceName=====" + faceName);
                    if (!TextUtils.isEmpty(faceName)) {
                        flag = true;
                        FaceDataControl.addFaceInfo(context, faceName);
                        SpeechlHandle.startSpeak(DataConfig.SPEAK_TYPE_CHAT, "我记住了，嘿嘿");

                    } else {
                        flag = false;
                    }
                } else {
                    flag = false;
                }

                break;
            case FACE_TEST_SCENE:// 脸部识别
                flag = true;
                SpeechlHandle.startSpeak(DataConfig.SPEAK_TYPE_FACE_DETECTOR, "好的");

                break;

            default:
                break;
        }
        DataConfig.isFaceDetector = false;
        return flag;
    }

    @Override
    public boolean isControlMove(String result) {
        if (!TextUtils.isEmpty(result)) {
            int moveKey = EnumManager.getMoveKey(result);
            Log.i("ifly", "moveKey===" + moveKey);
            if (moveKey != 0) {
                if (DataConfig.isControlToyCar) {//控制小车
                    DataConfig.controlNum = 0;
                    BroadcastEnclosure.controlToyCarMove(context, moveKey, getToyCarNum());
                    SpeechlHandle.startListen();
                } else {//控制机器人
                    SpeechlHandle.startSpeak(DataConfig.SPEAK_TYPE_CHAT, getRandomAnswer());
                    sendMoveAction(moveKey);
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isCustomDialogue(String result) {
        String[] questions = context.getResources().getStringArray(R.array.custom_question);
        if (questions != null && questions.length > 0) {
            for (int i = 0; i < questions.length; i++) {
                String question = questions[i];
                if (result.contains(question) || question.contains(result)) {
                    String[] answers = context.getResources().getStringArray(R.array.custom_answer);
                    if (answers != null && answers.length > 0) {
                        SpeechlHandle.startSpeak(DataConfig.SPEAK_TYPE_CHAT, answers[i]);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public boolean isAppPushRemind(String result) {
        if (DataConfig.isAppPushRemind) {
            handleAppRemind(result);
            return true;
        }
        return false;
    }

    @Override
    public boolean isScriptQA(String result) {
        if (DataConfig.isScriptQA) {
            handleAppScriptQA(result);
            return true;
        }
        return false;
    }

    @Override
    public void noResponseApp() {
        if (!DataConfig.isStartTime) {
            DataConfig.isStartTime = true;
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    if (DataConfig.isStartTime) {
                        ResponseAppRemindInfo mInfo = new ResponseAppRemindInfo();
                        mInfo.setAnswer("");
                        mInfo.setOriginalTime(AlarmRemindManager.getOriginalAlarmTime());
                        HttpManager.pushMsgToApp(JSON.toJSONString(mInfo), RequestConfig.TO_APP_REMIND, new NettyClientHandler(context));

                        doAppRemindNoResponse();
                    }
                }
            }, 15 * 1000);
        }
    }

    //机器人周围的小车的编号
    private static int mToyCarNum;

    public static int getToyCarNum() {
        return mToyCarNum;
    }

    public static void setToyCarNum(int toyCarNum) {
        mToyCarNum = toyCarNum;
    }

    //手臂
    private void hand(int num, String handCategory) {
        BroadcastEnclosure.controlWaving(context, ScriptConfig.HAND_UP, handCategory, "0");
        while (true) {
            num++;
            if (num == 150) {
                BroadcastEnclosure.controlWaving(context, ScriptConfig.HAND_DOWN, handCategory, "0");
                SpeechlHandle.startListen();
                return;
            }
        }
    }

    //控制移动的时候，随机回答内容
    private String getRandomAnswer() {
        String[] randomDatas = new String[]{"好的", "收到"};
        int randNum = new Random().nextInt(randomDatas.length);
        return randomDatas[randNum];
    }

    //控制走的广播
    private void sendMoveAction(int direction) {
        Intent intent = new Intent();
        intent.setAction(BroadcastAction.ACTION_CONTROL_ROBOT_MOVE_WITH_VOICE);
        intent.putExtra("direction", direction);
        context.sendBroadcast(intent);
    }

    //APP发来的提醒需求处理
    private void handleAppRemind(String result) {
        ResponseAppRemindInfo mInfo = new ResponseAppRemindInfo();
        mInfo.setAnswer(result);
        mInfo.setOriginalTime(AlarmRemindManager.getOriginalAlarmTime());
        HttpManager.pushMsgToApp(JSON.toJSONString(mInfo), RequestConfig.TO_APP_REMIND, new NettyClientHandler(context));

        if (!TextUtils.isEmpty(result)) {
            String answer = AlarmRemindManager.getRequireAnswer();
            if (!TextUtils.isEmpty(answer)) {
                if (result.contains(answer)) {//回答正确
                    DataConfig.isAppPushRemind = false;
                    DataConfig.isStartTime = false;
                    SpeechlHandle.startSpeak(DataConfig.SPEAK_TYPE_CHAT, "嘿嘿，我可以去玩喽");
                } else {//回答错误
                    doAppRemindNoResponse();
                }
            }
        }
    }

    //APP发来的提醒没有按照主人设置要求的话的处理
    private void doAppRemindNoResponse() {
        DataConfig.isAppPushRemind = false;
        DataConfig.isStartTime = false;
        SpeechlHandle.cancelSpeak();
        SpeechlHandle.cancelListen();
        int type = AlarmRemindManager.getSpareType();
        if (type != 0) {
            List<ScriptActionInfo> infos = new ArrayList<ScriptActionInfo>();
            ScriptActionInfo info = new ScriptActionInfo();
            info.setActionType(type);
            info.setContent(AlarmRemindManager.getSpareContent());
            infos.add(info);
            DataConfig.isPlayScript = false;
            ScriptHandler.doScriptAction(context, infos);
        } else {
            SpeechlHandle.startListen();
        }
    }

    //APP剧本的对话
    private void handleAppScriptQA(String result) {
        if (!TextUtils.isEmpty(result)) {
            new ScriptHandler().appScriptQA(context, result);
        }
    }

}
