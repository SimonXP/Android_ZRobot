package com.robot.et.core.software.window.network;

import com.robot.et.entity.RobotInfo;

/**
 * Created by houdeming on 2016/8/1.
 */
public interface RobotInfoCallBack {
    //获取机器人信息
    void onSuccess(RobotInfo info);

    //获取机器人信息失败
    void onFail(String errorMsg);
}
