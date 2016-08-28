package com.robot.et.util;

import android.text.TextUtils;

import com.robot.et.common.enums.ControlMoveEnum;
import com.robot.et.common.enums.EmotionEnum;
import com.robot.et.common.enums.MatchSceneEnum;
import com.robot.et.common.enums.SceneServiceEnum;

import java.util.Random;

public class EnumManager {

    //获取科大讯飞提供的场景service
    public static SceneServiceEnum getIflyScene(String str) {
        for (SceneServiceEnum serviceEnum : SceneServiceEnum.values()) {
            if (TextUtils.equals(str, serviceEnum.getServiceKey())) {
                return serviceEnum;
            }
        }
        return null;
    }

    //获取场景的enum
    public static MatchSceneEnum getScene(String str) {
        if (!TextUtils.isEmpty(str)) {
            for (MatchSceneEnum sceneEnum : MatchSceneEnum.values()) {
                if (sceneEnum.isScene(str)) {
                    return sceneEnum;
                }
            }
        }
        return null;
    }

    //获取控制运动的key
    public static int getMoveKey(String str) {
        int moveKey = 0;
        if (!TextUtils.isEmpty(str)) {
            for (ControlMoveEnum moveEnum : ControlMoveEnum.values()) {
                if (str.contains(moveEnum.getMoveName())) {
                    moveKey = moveEnum.getMoveKey();
                }
            }
        }
        return moveKey;
    }

    //获取表情的int型值
    public static int getEmotionKey(String emotionName) {
        int key = 0;
        if (!TextUtils.isEmpty(emotionName)) {
            if (TextUtils.equals(emotionName, "随意表情")) {
                EmotionEnum[] motions = EmotionEnum.values();
                int size = motions.length;
                if (motions != null && size > 0) {
                    Random random = new Random();
                    int randNum = random.nextInt(size);
                    EmotionEnum newEnum = motions[randNum];
                    key = newEnum.getEmotionKey();
                }
            } else {
                for (EmotionEnum emotionEnum : EmotionEnum.values()) {
                    if (TextUtils.equals(emotionName, emotionEnum.getEmotionName())) {
                        key = emotionEnum.getEmotionKey();
                    }
                }
            }
        }
        return key;
    }

    //获取表情的枚举值
    public static EmotionEnum getEmotionEnum(String str) {
        if (!TextUtils.isEmpty(str)) {
            for (EmotionEnum emotionEnum : EmotionEnum.values()) {
                if (str.contains(emotionEnum.getEmotionName())) {
                    return emotionEnum;
                }
            }
        }
        return null;
    }

}
