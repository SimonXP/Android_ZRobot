package com.robot.et.core.software.common.script;

import android.content.Context;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;

import com.robot.et.common.DataConfig;
import com.robot.et.common.ScriptConfig;
import com.robot.et.common.enums.EmotionEnum;
import com.robot.et.core.software.common.speech.SpeechImpl;
import com.robot.et.util.BroadcastEnclosure;

/**
 * Created by houdeming on 2016/7/16.
 * app触摸机器人的控制
 */
public class TouchHandler {

    private final static int TOUCH_HEAD = 0;//头
    private final static int TOUCH_EYE = 1;//眼睛
    private final static int TOUCH_HAND_LEFT = 2;//左手
    private final static int TOUCH_HAND_RIGHT = 3;//右手
    private final static int TOUCH_BELLY = 4;//肚子
    private final static int TOUCH_HAND_FOOT = 5;//脚

    public static void responseTouch(Context context, String touchKey) {
        if (!TextUtils.isEmpty(touchKey)) {
            if (TextUtils.isDigitsOnly(touchKey)) {
                int key = Integer.parseInt(touchKey);
                String content = "";
                switch (key) {
                    case TOUCH_HEAD://头
                        Log.i("netty", "头");
                        content = "摸摸我的头，感觉头变小了呢";
                        SpeechImpl.getInstance().startSpeak(DataConfig.SPEAK_TYPE_CHAT, content);

                        break;
                    case TOUCH_EYE://眼睛
                        Log.i("netty", "眼睛key===" + EmotionEnum.EMOTION_BLINK_TWO.getEmotionKey());
                        BroadcastEnclosure.controlRobotEmotion(context, EmotionEnum.EMOTION_BLINK_TWO.getEmotionKey());
                        content = "看我眼睛大，你是不是很羡慕呢，嘿嘿";
                        SpeechImpl.getInstance().startSpeak(DataConfig.SPEAK_TYPE_CHAT, content);

                        break;
                    case TOUCH_HAND_LEFT://左手
                        Log.i("netty", "左手");
                        waving(context, ScriptConfig.HAND_LEFT);

                        break;
                    case TOUCH_HAND_RIGHT://右手
                        Log.i("netty", "右手");
                        waving(context, ScriptConfig.HAND_RIGHT);

                        break;
                    case TOUCH_BELLY://肚子
                        Log.i("netty", "肚子");
                        content = "看我肚子这么大，你有木有羡慕呢，哈哈";
                        SpeechImpl.getInstance().startSpeak(DataConfig.SPEAK_TYPE_CHAT, content);

                        break;
                    case TOUCH_HAND_FOOT://脚
                        Log.i("netty", "脚");
                        content = "好久没人给我挠痒痒了呢，嘿嘿";
                        SpeechImpl.getInstance().startSpeak(DataConfig.SPEAK_TYPE_CHAT, content);

                        break;
                    default:
                        break;

                }
            }

        }

    }

    //摆手
    private static void waving(final Context context, final String handCategory) {
        BroadcastEnclosure.controlWaving(context, ScriptConfig.HAND_UP, handCategory, "0");
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                BroadcastEnclosure.controlWaving(context, ScriptConfig.HAND_DOWN, handCategory, "0");
            }
        }, 1500);
        SpeechImpl.getInstance().startListen();
    }

}
