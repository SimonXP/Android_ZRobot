package com.robot.et.core.software.script;

import android.content.Context;

/**
 * Created by houdeming on 2016/8/3.
 */
public interface Script {
    //音乐播放
    void scriptPlayMusic(Context context, boolean isStart);

    //说话
    void scriptSpeak(Context context);

    //剧本动作
    void scriptAction(Context context);

    //剧本对话
    void appScriptQA(Context context, String result);

}
