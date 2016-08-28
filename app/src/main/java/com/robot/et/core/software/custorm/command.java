package com.robot.et.core.software.custorm;

/**
 * Created by houdeming on 2016/7/28.
 */
public interface command {
    // 匹配的场景
    boolean isMatchScene(String result);

    //是否控制运动
    boolean isControlMove(String result);

    //是否是自定义问答
    boolean isCustomDialogue(String result);

    //是否是APP提醒必须要说的话
    boolean isAppPushRemind(String result);

    //是否是APP发来的是剧本的问答
    boolean isScriptQA(String result);

    //没有响应App的命令
    void noResponseApp();

}
