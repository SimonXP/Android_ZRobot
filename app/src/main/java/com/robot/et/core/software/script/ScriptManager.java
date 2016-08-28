package com.robot.et.core.software.script;

import android.text.TextUtils;

import com.robot.et.common.ScriptConfig;
import com.robot.et.entity.ScriptActionInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by houdeming on 2016/8/3.
 */
public class ScriptManager {

    private static List<ScriptActionInfo> infos = new ArrayList<ScriptActionInfo>();

    public static List<ScriptActionInfo> getScriptActionInfos() {
        return infos;
    }

    public static void setScriptActionInfos(List<ScriptActionInfo> infos) {
        ScriptManager.infos = infos;
    }

    private static String scriptAnswer;

    public static String getScriptAnswer() {
        return scriptAnswer;
    }

    public static void setScriptAnswer(String scriptAnswer) {
        ScriptManager.scriptAnswer = scriptAnswer;
    }

    //获取哪一个手
    public static String getHandCategory(String handCategory) {
        String category = "";
        if (!TextUtils.isEmpty(handCategory)) {
            if (TextUtils.equals(handCategory, "左手")) {
                category = ScriptConfig.HAND_LEFT;
            } else if (TextUtils.equals(handCategory, "右手")) {
                category = ScriptConfig.HAND_RIGHT;
            } else if (TextUtils.equals(handCategory, "双手")) {
                category = ScriptConfig.HAND_TWO;
            }
        }
        return category;
    }

    //获取手的方向
    public static String getHandDirection(String handDirection) {
        String direction = "";
        if (!TextUtils.isEmpty(handDirection)) {
            if (TextUtils.equals(handDirection, "举手")) {
                direction = ScriptConfig.HAND_UP;
            } else if (TextUtils.equals(handDirection, "放手")) {
                direction = ScriptConfig.HAND_DOWN;
            } else if (TextUtils.equals(handDirection, "摆手")) {
                direction = ScriptConfig.HAND_WAVING;
            }
        }
        return direction;
    }

    //获取转圈的方向
    public static int getTurnDirection(String turnDirection) {
        int direction = 0;
        if (!TextUtils.isEmpty(turnDirection)) {
            if (TextUtils.equals(turnDirection, "顺时针")) {
                direction = ScriptConfig.SCRIPT_TURN_CLOCKWISE;
            } else if (TextUtils.equals(turnDirection, "逆时针")) {
                direction = ScriptConfig.SCRIPT_TURN_ANTI_CLOCKWISE;
            }
        }
        return direction;
    }

}
