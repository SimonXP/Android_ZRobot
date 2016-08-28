package com.robot.et.core.software.window.network;

import android.util.Log;

import com.robot.et.common.UrlConfig;
import com.robot.et.core.software.okhttp.HttpEngine;
import com.robot.et.util.SharedPreferencesKeys;
import com.robot.et.util.SharedPreferencesUtils;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;

/**
 * Created by houdeming on 2016/8/2.
 */
public class HttpManager {
    private final static String TAG = "netty";

    //初始化获取机器人的信息
    public static void getRobotInfo(String url, final String deviceId, final RobotInfoCallBack callBack) {
        HttpEngine.Param[] params = new HttpEngine.Param[]{
                new HttpEngine.Param("deviceId", deviceId),
        };
        HttpEngine httpEngine = HttpEngine.getInstance();
        Request request = httpEngine.createRequest(url, params);
        final Call call = httpEngine.createRequestCall(request);
        call.enqueue(new Callback() {

            @Override
            public void onFailure(Request request, IOException arg1) {
                Log.i(TAG, "getRobotInfo()  onFailure");
                callBack.onFail(arg1.getMessage());
            }

            @Override
            public void onResponse(Response request) throws IOException {
                String json = request.body().string();
                Log.i(TAG, "getRobotInfo()  json==" + json);
                callBack.onSuccess(NetResultParse.parseRobotInfo(json));
            }

        });
    }

    //向APP发送当前媒体播放的状态
    public static void pushMediaState(String meidaType, String mediaState, String playName, final NettyClient callBack) {
        final SharedPreferencesUtils share = SharedPreferencesUtils.getInstance();
        HttpEngine.Param[] params = new HttpEngine.Param[]{
                new HttpEngine.Param("mobile", share.getString(SharedPreferencesKeys.AGORA_CALL_PHONENUM, "")),
                new HttpEngine.Param("robotNumber", share.getString(SharedPreferencesKeys.ROBOT_NUM, "")),
                new HttpEngine.Param("mediaType", meidaType),
                new HttpEngine.Param("mediaState", mediaState),
                new HttpEngine.Param("playName", playName)
        };
        HttpEngine httpEngine = HttpEngine.getInstance();
        Request request = httpEngine.createRequest(UrlConfig.PUSH_MEDIASTATE_TO_APP, params);
        Call call = httpEngine.createRequestCall(request);
        call.enqueue(new Callback() {

            @Override
            public void onFailure(Request arg0, IOException arg1) {
            }

            @Override
            public void onResponse(Response response) throws IOException {
                String result = response.body().string();
                Log.i(TAG, "result====" + result);
                if (NetResultParse.isSuccess(result)) {
                    Log.i(TAG, "向APP发送媒体状态成功");
                }
                callBack.connect(result);
            }

        });
    }

    //向APP推送消息
    public static void pushMsgToApp(String sendContent, String remindCode, final NettyClient callBack) {
        final SharedPreferencesUtils share = SharedPreferencesUtils.getInstance();
        HttpEngine.Param[] params = new HttpEngine.Param[]{
                new HttpEngine.Param("robotNumber", share.getString(SharedPreferencesKeys.ROBOT_NUM, "")),
                new HttpEngine.Param("mobile", share.getString(SharedPreferencesKeys.AGORA_CALL_PHONENUM, "")),
                new HttpEngine.Param("msgType", remindCode),
                new HttpEngine.Param("msgContent", sendContent)
        };
        HttpEngine httpEngine = HttpEngine.getInstance();
        Request request = httpEngine.createRequest(UrlConfig.PUSH_MESSAGE_TO_APP, params);
        Call call = httpEngine.createRequestCall(request);
        call.enqueue(new Callback() {

            @Override
            public void onFailure(Request arg0, IOException arg1) {
            }

            @Override
            public void onResponse(Response response) throws IOException {
                String result = response.body().string();
                Log.i(TAG, "result====" + result);
                if (NetResultParse.isSuccess(result)) {
                    Log.i(TAG, "向APP推送消息成功");
                }
                callBack.connect(result);
            }

        });
    }

}
