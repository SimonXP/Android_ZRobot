package com.robot.et.core.software.iflytek;

import org.json.JSONObject;

/**
 * Created by houdeming on 2016/7/25.
 * 解析科大讯飞获取当前的service
 */
public interface ParseResultCallBack {
    //解析结果回调
    void getResult(String question, String service, JSONObject jObject);

    //解析异常
    void onError(String errorMsg);

}
