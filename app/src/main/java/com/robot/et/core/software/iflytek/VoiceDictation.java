package com.robot.et.core.software.iflytek;

/**
 * Created by houdeming on 2016/7/25.
 * 语音听写的接口
 */
public interface VoiceDictation {
    //开始语音听写
    void startListen();

    //取消语音听写
    void cancelListen();

}
