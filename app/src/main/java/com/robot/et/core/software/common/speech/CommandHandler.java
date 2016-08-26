package com.robot.et.core.software.common.speech;

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
import com.robot.et.common.enums.EmotionEnum;
import com.robot.et.common.enums.MatchSceneEnum;
import com.robot.et.core.software.common.network.HttpManager;
import com.robot.et.core.software.common.push.netty.NettyClientHandler;
import com.robot.et.core.software.common.script.ScriptHandler;
import com.robot.et.core.software.common.view.EmotionManager;
import com.robot.et.core.software.common.view.OneImgManager;
import com.robot.et.core.software.common.view.ViewCommon;
import com.robot.et.core.software.system.media.MediaManager;
import com.robot.et.db.RobotDB;
import com.robot.et.entity.LearnAnswerInfo;
import com.robot.et.entity.ResponseAppRemindInfo;
import com.robot.et.entity.ScriptActionInfo;
import com.robot.et.entity.VisionRecogniseEnvironmentInfo;
import com.robot.et.util.AlarmRemindManager;
import com.robot.et.util.BroadcastEnclosure;
import com.robot.et.util.EnumManager;
import com.robot.et.util.FaceManager;
import com.robot.et.util.MatchStringUtil;
import com.robot.et.util.RobotLearnManager;
import com.robot.et.util.SharedPreferencesKeys;
import com.robot.et.util.SharedPreferencesUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by houdeming on 2016/7/28.
 */
public class CommandHandler {

    private Context context;

    public CommandHandler(Context context) {
        this.context = context;
    }

    public boolean isCustorm(String result) {
        if (!TextUtils.isEmpty(result)) {
            if (isAppPushRemind(result)) {
                return true;
            }
            if (isRosService(result)) {
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

            if (isMatchEmotion(result)) {
                return true;
            }
        }

        return false;
    }

    // 匹配的场景
    public boolean isMatchScene(String result) {
        MatchSceneEnum sceneEnum = EnumManager.getScene(result);
        Log.i("ifly", "sceneEnum=====" + sceneEnum);
        if (sceneEnum == null) {
            DataConfig.isFaceDetector = false;
            LearnAnswerInfo info = RobotLearnManager.getRobotLearnInfo(result);
            String content = info.getAnswer();//回答的话语
            boolean flag = false;
            if (!TextUtils.isEmpty(content)) {
                SpeechImpl.getInstance().startSpeak(DataConfig.SPEAK_TYPE_CHAT, content);
                flag = true;
            }
            return flag;
        }

        boolean flag = false;
        switch (sceneEnum) {
            case VOICE_BIGGEST_SCENE:// 声音最大
                flag = true;
                MediaManager.getInstance().setMaxVolume();
                SpeechImpl.getInstance().startSpeak(DataConfig.SPEAK_TYPE_CHAT, "音量已经最大");

                break;
            case VOICE_LITTEST_SCENE:// 声音最小
                flag = true;
                MediaManager.getInstance().setCurrentVolume(6);
                SpeechImpl.getInstance().startSpeak(DataConfig.SPEAK_TYPE_CHAT, "音量已经最小");

                break;
            case VOICE_BIGGER_INDIRECT_SCENE:// 间接增加声音
                flag = true;
                MediaManager.getInstance().increaseVolume();
                SpeechImpl.getInstance().startSpeak(DataConfig.SPEAK_TYPE_CHAT, "音量增加");

                break;
            case VOICE_LITTER_INDIRECT_SCENE://间接降低声音
                flag = true;
                MediaManager.getInstance().reduceVolume();
                SpeechImpl.getInstance().startSpeak(DataConfig.SPEAK_TYPE_CHAT, "音量减小");

                break;
            case VOICE_BIGGER_SCENE:// 直接增加声音
                flag = true;
                MediaManager.getInstance().increaseVolume();
                SpeechImpl.getInstance().startSpeak(DataConfig.SPEAK_TYPE_CHAT, "音量增加");

                break;
            case VOICE_LITTER_SCENE://直接降低声音
                flag = true;
                MediaManager.getInstance().reduceVolume();
                SpeechImpl.getInstance().startSpeak(DataConfig.SPEAK_TYPE_CHAT, "音量减小");

                break;
            case QUESTION_ANSWER_SCENE:// 智能学习回答话语
                flag = true;
                String content = RobotLearnManager.learnBySpeak(DataConfig.LEARN_BY_ROBOT, result);
                SpeechImpl.getInstance().startSpeak(DataConfig.SPEAK_TYPE_CHAT, content);

                break;
            case DISTURB_OPEN_SCENE:// 免打扰开
                flag = true;
                HttpManager.changeRobotCallStatus(DataConfig.ROBOT_STATUS_DISYURB_NOT);
                SpeechImpl.getInstance().startSpeak(DataConfig.SPEAK_TYPE_CHAT, "好的，进入免打扰模式");

                break;
            case DISTURB_CLOSE_SCENE:// 免打扰关
                flag = true;
                HttpManager.changeRobotCallStatus(DataConfig.ROBOT_STATUS_NORMAL);
                SpeechImpl.getInstance().startSpeak(DataConfig.SPEAK_TYPE_CHAT, "好的，免打扰模式已关闭");

                break;
            case SHUT_UP_SCENE:// 闭嘴
                flag = true;
                SpeechImpl.getInstance().startSpeak(DataConfig.SPEAK_TYPE_DO_NOTHINF, "那我休息了，白白");
                sleep();

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
                SpeechImpl.getInstance().startSpeak(DataConfig.SPEAK_TYPE_CHAT, "好的");

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
                SpeechImpl.getInstance().startSpeak(DataConfig.SPEAK_TYPE_CHAT, "好的");
                HttpManager.pushMsgToApp("开", RequestConfig.TO_APP_BLUETOOTH_CONTROLLER, new NettyClientHandler(context));

                break;
            case CLOSE_HOUSEHOLD_SCENE:// 关闭家电
                flag = true;
                SpeechImpl.getInstance().startSpeak(DataConfig.SPEAK_TYPE_CHAT, "好的");
                HttpManager.pushMsgToApp("关", RequestConfig.TO_APP_BLUETOOTH_CONTROLLER, new NettyClientHandler(context));

                break;
            case FACE_NAME_SCENE:// 脸部名称
                if (DataConfig.isFaceDetector) {
                    DataConfig.isFaceDetector = false;
                    String faceName = MatchStringUtil.getFaceName(result);
                    Log.i("ifly", "faceName=====" + faceName);
                    if (!TextUtils.isEmpty(faceName)) {
                        flag = true;
                        FaceManager.addFaceInfo(faceName);
                        ViewCommon.initView();
                        OneImgManager.showImg(R.mipmap.robot_qr_code);
                        SpeechImpl.getInstance().startSpeak(DataConfig.SPEAK_TYPE_SHOW_QRCODE, "我记住了，您可以扫描我的二维码和我聊天哦");
                    }
                }

                break;
            case FACE_TEST_SCENE:// 开启脸部识别
                flag = true;
                DataConfig.isVoiceFaceRecognise = true;
                BroadcastEnclosure.openFaceRecognise(context, true);

                break;
            case PHOTOGRAPH_SCENE:// 拍照
                flag = true;
                SpeechImpl.getInstance().startSpeak(DataConfig.SPEAK_TYPE_CHAT, "好的");
                DataConfig.isTakePicture = true;
                BroadcastEnclosure.openFaceRecognise(context, true);

                break;
            case VISION_LEARN_SCENE:// 视觉学习
                flag = true;
                String visionContent = MatchStringUtil.getVisionLearnAnswer(result);
                Log.i("ifly", "visionContent=====" + visionContent);
                String speakContent = "";
                if (TextUtils.isEmpty(visionContent)) {//问题：这是什么？
                    //从底层视觉学习库中查找答案
                    // do thing
                    String learnAnswer = "";


                    if (TextUtils.isEmpty(learnAnswer)) {
                        speakContent = "不知道，您能告诉我吗？";
                    } else {
                        speakContent = "这是" + learnAnswer;
                    }
                } else {//告诉答案：例如：这是玩具车
                    //告诉底层视觉学习的答案
                    // do thing


                    speakContent = "好的，我记住了";
                }
                SpeechImpl.getInstance().startSpeak(DataConfig.SPEAK_TYPE_CHAT, speakContent);

                break;
            case ENVIRONMENT_LEARN_SCENE:// 环境认识学习
                //这里是xxx
                String environmentContent = MatchStringUtil.getEnvironmentLearnAnswer(result);
                Log.i("ifly", "environmentContent=====" + environmentContent);
                if (!TextUtils.isEmpty(environmentContent)) {
                    flag = true;
                    //通知视觉学习记住内容 获取物体坐标
                    // do  thing
                    addVisionRecogniseInfo(environmentContent, "", "");

                    SpeechImpl.getInstance().startSpeak(DataConfig.SPEAK_TYPE_CHAT, "好的，我记住了");
                }

                break;
            case GO_WHERE_SCENE:// 去哪里的指令
                String whereContent = MatchStringUtil.getGoWhereAnswer(result);
                Log.i("ifly", "whereContent=====" + whereContent);
                if (!TextUtils.isEmpty(whereContent)) {
                    //通知机器人去哪里
                    // do  thing
                    flag = true;
                    VisionRecogniseEnvironmentInfo info = RobotDB.getInstance().getVisionRecogniseEnvironmentInfo(whereContent);
                    if (info != null) {

                    }
                    SpeechImpl.getInstance().startSpeak(DataConfig.SPEAK_TYPE_CHAT, "好的");
                }

                break;
            case VISION_LEARN_SIGN_SCENE:// 进入视觉学习的标志
                flag = true;
                String knowContent = "";
                if (!DataConfig.isIntoKnowEnvironment) {
                    DataConfig.isIntoKnowEnvironment = true;
                    DataConfig.isStartRoam = true;
                    DataConfig.isRecogniseComplected = true;
                    knowContent = "我会的可多了，但是这是个新环境，我可以走一圈认识一下吗？";
                } else {
                    DataConfig.isIntoKnowEnvironment = false;
                    DataConfig.isStartRoam = false;
                    DataConfig.isRecogniseComplected = false;
                    knowContent = "我会记住我见过的所有东西，你想考考我吗？";
                }

                SpeechImpl.getInstance().startSpeak(DataConfig.SPEAK_TYPE_CHAT, knowContent);

                break;
            case START_RECOGNISE_ENVIRONMENT_SCENE:// 进入识别环境的标志
                if (DataConfig.isStartRoam) {
                    DataConfig.isStartRoam = false;
                    flag = true;
                    ViewCommon.initView();
                    EmotionManager.showEmotion(R.mipmap.emotion_normal);
                    SpeechImpl.getInstance().startListen();
                    Log.i("ifly", "通知本体开始漫游并识别物体");
                    //通知本体开始漫游并识别物体
                    //do thing
                }

                break;
            case RECOGNISE_COMPLECTED_SCENE:// 识别环境完成
                if (DataConfig.isRecogniseComplected) {
                    DataConfig.isRecogniseComplected = false;
                    flag = true;
                    SpeechImpl.getInstance().startSpeak(DataConfig.SPEAK_TYPE_CHAT, "我认识好了，你可以叫我去厨房、客厅，我就会去那儿");
                    //通知本体回到原位
                    //do thing


                }

                break;

            default:
                break;
        }
        DataConfig.isFaceDetector = false;
        return flag;
    }

    //是否控制运动
    public boolean isControlMove(String result) {
        if (!TextUtils.isEmpty(result)) {
            int moveKey = EnumManager.getMoveKey(result);
            Log.i("ifly", "moveKey===" + moveKey);
            if (moveKey != 0) {
                if (DataConfig.isControlToyCar) {//控制小车
                    DataConfig.controlNum = 0;
                    BroadcastEnclosure.controlToyCarMove(context, moveKey, getToyCarNum());
                    ViewCommon.initView();
                    EmotionManager.showEmotion(R.mipmap.emotion_normal);
                    SpeechImpl.getInstance().startListen();
                } else {//控制机器人
                    DataConfig.isControlRobotMove = true;
                    SpeechImpl.getInstance().startSpeak(DataConfig.SPEAK_TYPE_CHAT, getRandomAnswer());
                    BroadcastEnclosure.controlRobotMove(context, moveKey);
                }
                return true;
            }
        }
        return false;
    }

    //是否是自定义问答
    public boolean isCustomDialogue(String result) {
        String[] questions = context.getResources().getStringArray(R.array.custom_question);
        int length = questions.length;
        if (questions != null && length > 0) {
            for (int i = 0; i < length; i++) {
                String question = questions[i];
                if (result.contains(question) || question.contains(result)) {
                    String[] answers = context.getResources().getStringArray(R.array.custom_answer);
                    if (answers != null && answers.length > 0) {
                        SpeechImpl.getInstance().startSpeak(DataConfig.SPEAK_TYPE_CHAT, answers[i]);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    //是否是APP提醒必须要说的话
    public boolean isAppPushRemind(String result) {
        if (DataConfig.isAppPushRemind) {
            handleAppRemind(result);
            return true;
        }
        return false;
    }

    //是否ros服务
    public boolean isRosService(String result) {
        if (!TextUtils.isEmpty(result)) {
            String rosKey = EnumManager.getRosServiceKey(result);
            Log.i("ros", "rosKey===" + rosKey);
            if (!TextUtils.isEmpty(rosKey)) {
                sendRos(rosKey);
                return  true;
            }
        }
        return false;
    }

    //是否是表情
    public boolean isMatchEmotion(String result) {
        EmotionEnum emotionEnum = EnumManager.getEmotionEnum(result);
        if (emotionEnum != null) {
            ViewCommon.initView();
            EmotionManager.showEmotionAnim(emotionEnum.getEmotionKey());
            String answer = emotionEnum.getRequireAnswer();
            if (!TextUtils.isEmpty(answer)) {
                SpeechImpl.getInstance().startSpeak(DataConfig.SPEAK_TYPE_CHAT, answer);
            } else {
                EmotionManager.showEmotion(R.mipmap.emotion_normal);
                SpeechImpl.getInstance().startListen();
            }
            return true;
        }
        return false;
    }

    //是否是APP发来的是剧本的问答
    public boolean isScriptQA(String result) {
        if (DataConfig.isScriptQA) {
            new ScriptHandler().appScriptQA(context, result);
            return true;
        }
        return false;
    }

    //没有响应App的命令
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

    //让机器人睡觉
    public void sleep() {
        DataConfig.isSleep = true;
        Intent intent = new Intent();
        intent.setAction(BroadcastAction.ACTION_WAKE_UP_RESET);
        context.sendBroadcast(intent);

        ViewCommon.initView();
        EmotionManager.showEmotion(R.mipmap.emotion_blink);
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
    private void hand(int num, final String handCategory) {
        BroadcastEnclosure.controlWaving(context, ScriptConfig.HAND_UP, handCategory, "0");
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                BroadcastEnclosure.controlWaving(context, ScriptConfig.HAND_DOWN, handCategory, "0");
            }
        }, 1500);
        ViewCommon.initView();
        EmotionManager.showEmotion(R.mipmap.emotion_normal);
        SpeechImpl.getInstance().startListen();
    }

    //控制移动的时候，随机回答内容
    private String getRandomAnswer() {
        String[] randomDatas = new String[]{"好的", "收到"};
        int randNum = new Random().nextInt(randomDatas.length);
        return randomDatas[randNum];
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
                    SpeechImpl.getInstance().startSpeak(DataConfig.SPEAK_TYPE_CHAT, "嘿嘿，我可以去玩喽");
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
        SpeechImpl.getInstance().cancelSpeak();
        SpeechImpl.getInstance().cancelListen();
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
            ViewCommon.initView();
            EmotionManager.showEmotion(R.mipmap.emotion_normal);
            SpeechImpl.getInstance().startListen();
        }
    }

    //ros的广播
    private void sendRos(String rosKey) {
        Intent intent = new Intent();
        intent.setAction(BroadcastAction.ACTION_ROS_SERVICE);
        intent.putExtra("rosKey", rosKey);
        context.sendBroadcast(intent);
    }

    //增加视觉环境学习到数据库
    private void addVisionRecogniseInfo(String positionName, String positionX, String positionY) {
        RobotDB mDb = RobotDB.getInstance();
        VisionRecogniseEnvironmentInfo info = mDb.getVisionRecogniseEnvironmentInfo(positionName);
        if (info != null) {//已经存在
            mDb.updateVisionPositionXY(positionName, positionX, positionY);
        } else {//不存在
            VisionRecogniseEnvironmentInfo mInfo = new VisionRecogniseEnvironmentInfo();
            String robotNum = SharedPreferencesUtils.getInstance().getString(SharedPreferencesKeys.ROBOT_NUM, "");
            mInfo.setRobotNum(robotNum);
            mInfo.setPositionName(positionName);
            mInfo.setPositionX(positionX);
            mInfo.setPositionY(positionY);
            mDb.addVisionRecogniseEnvironment(mInfo);
        }
    }


}
