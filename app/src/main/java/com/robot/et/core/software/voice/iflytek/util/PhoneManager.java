package com.robot.et.core.software.voice.iflytek.util;

import android.text.TextUtils;
import android.util.Log;

import com.robot.et.common.DataConfig;
import com.robot.et.util.SharedPreferencesKeys;
import com.robot.et.util.SharedPreferencesUtils;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

/**
 * Created by houdeming on 2016/8/4.
 */
public class PhoneManager {
    private final static String TAG = "ifly";

    private static String userName;//要拨打电话的人的用户名

    public static String getUserName() {
        return userName;
    }

    public static void setUserName(String userName) {
        PhoneManager.userName = userName;
    }

    //获取语音打电话要说的内容
    public static String getCallContent(String userName, String result) {
        String content = "";
        if (!TextUtils.isEmpty(result)) {
            String roomNum = getRoomNum(result);
            Log.i(TAG, "roomNum==" + roomNum);
            if (!TextUtils.isEmpty(roomNum)) {
                SharedPreferencesUtils share = SharedPreferencesUtils.getInstance();
                share.putString(SharedPreferencesKeys.AGORA_ROOM_NUM, roomNum);
                share.putInt(SharedPreferencesKeys.AGORA_CALL_TYPE, DataConfig.PHONE_CALL_TO_MEN);
                share.commitValue();
                if (TextUtils.isDigitsOnly(userName)) {// 是电话号码
                    content = getUserName();
                } else {// 是用户名
                    content = userName;
                }
                Log.i(TAG, "content==" + content);
            }
        }
        return content;
    }

    //解析获取房间号
    private static String getRoomNum(String result) {
        String roomNumber = "";
        if (!TextUtils.isEmpty(result)) {
            try {
                JSONTokener tokener = new JSONTokener(result);
                JSONObject object = new JSONObject(tokener);
                String resultCode = object.getString("resultCode");
                if (TextUtils.equals(resultCode, "00")) {
                    JSONObject json = object.getJSONObject("agora");
                    roomNumber = json.getString("roomNumber");
                    if (object.has("requestUser")) {
                        JSONObject jObject = object.getJSONObject("requestUser");
                        String mobile = jObject.getString("mobile");
                        String userName = jObject.getString("username");
                        if (!TextUtils.isEmpty(userName)) {
                            setUserName(userName);
                        }
                        Log.i(TAG, "mobile====" + mobile);
                        Log.i(TAG, "userName====" + userName);
                        SharedPreferencesUtils share = SharedPreferencesUtils.getInstance();
                        share.putString(SharedPreferencesKeys.AGORA_CALL_PHONENUM, mobile);
                        share.commitValue();
                    }
                }
            } catch (JSONException e) {
                Log.i(TAG, "getRoomNum JSONException");
                roomNumber = "";
            }
        }
        return roomNumber;
    }

}
