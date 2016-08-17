package com.robot.et.core.software.common.network;

import android.text.TextUtils;
import android.util.Log;

import com.robot.et.entity.JpushInfo;
import com.robot.et.entity.RemindInfo;
import com.robot.et.entity.RobotInfo;
import com.robot.et.util.SharedPreferencesKeys;
import com.robot.et.util.SharedPreferencesUtils;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

/**
 * Created by houdeming on 2016/8/2.
 */
public class NetResultParse {

    //解析机器人信息的结果
    public static RobotInfo parseRobotInfo(String json) {
        RobotInfo info = null;
        if (!TextUtils.isEmpty(json)) {
            try {
                JSONTokener tokener = new JSONTokener(json);
                JSONObject object = new JSONObject(tokener);
                String message = object.getString("message");
                String resultCode = object.getString("resultCode");
                if (TextUtils.equals(resultCode, "00")) {
                    JSONObject jObject = object.getJSONObject("robot");
                    info = new RobotInfo();
                    info.setRobotNum(jObject.getString("robotNumber"));
                }
            } catch (JSONException e) {
                Log.i("netty", "getRobotInfo JSONException");
                info = null;
            }
        }
        return info;
    }

    //请求是否成功
    public static boolean isSuccess(String result) {
        if (!TextUtils.isEmpty(result)) {
            try {
                JSONTokener tokener = new JSONTokener(result);
                JSONObject object = new JSONObject(tokener);
                String resultCode = object.getString("resultCode");
                if (TextUtils.equals(resultCode, "00")) {
                    return true;
                }
            } catch (JSONException e) {
                Log.i("netty", "isSuccess JSONException");
                return false;
            }
        }
        return false;
    }

    //解析netty发来的json
    public static JpushInfo getJpushInfo(String jsonData) {
        JpushInfo info = null;
        if (!TextUtils.isEmpty(jsonData)) {
            try {
                JSONTokener tokener = new JSONTokener(jsonData);
                JSONObject jsonObject = new JSONObject(tokener);
                String msg = jsonObject.getString("msg");
                if (!TextUtils.isEmpty(msg)) {
                    info = new JpushInfo();
                    if (!msg.contains("pushCode")) {
                        info.setDirection(msg);
                        return info;
                    }

                    JSONTokener tokener2 = new JSONTokener(msg);
                    JSONObject json = new JSONObject(tokener2);
                    if (json.has("content")) {
                        String content = json.getString("content");
                        if (!TextUtils.isEmpty(content)) {
                            info.setContent(content);
                        }
                    }

                    if (json.has("roomNumber")) {
                        String roomNumber = json.getString("roomNumber");
                        if (!TextUtils.isEmpty(roomNumber)) {
                            info.setRoomNum(roomNumber);
                        }
                    }

                    if (json.has("pushCode")) {
                        String extra = json.getString("pushCode");
                        if (!TextUtils.isEmpty(extra)) {
                            info.setExtra(Integer.parseInt(extra));
                        }
                    }

                    if (json.has("comandContent")) {
                        String comandContent = json.getString("comandContent");
                        if (!TextUtils.isEmpty(comandContent)) {
                            info.setMusicContent(comandContent);
                        }
                    }

                    if (json.has("mobile")) {
                        String mobile = json.getString("mobile");
                        Log.i("netty", "mobile====" + mobile);
                        if (!TextUtils.isEmpty(mobile)) {
                            SharedPreferencesUtils share = SharedPreferencesUtils.getInstance();
                            share.putString(SharedPreferencesKeys.AGORA_CALL_PHONENUM, mobile);
                            share.commitValue();
                        }
                    }

                    if (json.has("setAlarmTime")) {
                        String alarmTime = json.getString("setAlarmTime");
                        if (!TextUtils.isEmpty(alarmTime)) {
                            info.setAlarmTime(alarmTime);
                        }
                    }

                    if (json.has("setAlarmContent")) {
                        String alarmContent = json.getString("setAlarmContent");
                        if (!TextUtils.isEmpty(alarmContent)) {
                            info.setAlarmContent(alarmContent);
                        }
                    }

                    if (json.has("setRemindNum")) {
                        info.setRemindNum(json.getInt("setRemindNum"));
                    }

                    if (json.has("setFreInterval")) {
                        info.setRemindInteval(json.getInt("setFreInterval"));
                    }

                    if (json.has("setFrequency")) {
                        info.setFrequency(json.getInt("setFrequency"));
                    }

                    if (json.has("question")) {
                        info.setQuestion(json.getString("question"));
                    }

                    if (json.has("answer")) {
                        info.setAnswer(json.getString("answer"));
                    }

                    if (json.has("user")) {
                        JSONObject object = json.getJSONObject("user");
                        String mobile = object.getString("mobile");
                        Log.i("netty", "mobile====" + mobile);
                        SharedPreferencesUtils share = SharedPreferencesUtils.getInstance();
                        share.putString(SharedPreferencesKeys.AGORA_CALL_PHONENUM, mobile);
                        share.commitValue();
                    }

                }

            } catch (JSONException e) {
                Log.i("netty", "getJpushInfo JSONException");
                info = null;
            }
        }
        return info;
    }

    //解析app发来的提醒
    public static RemindInfo parseAppRemind(String jsonContent) {
        RemindInfo info = new RemindInfo();
        if (!TextUtils.isEmpty(jsonContent)) {
            try {
                JSONTokener tokener = new JSONTokener(jsonContent);
                JSONObject json = new JSONObject(tokener);
                info.setOriginalAlarmTime(json.getString("remindTime"));
                info.setContent(json.getString("remindContent"));
                info.setRemindMen(json.getString("remindMen"));
                if (json.has("requireAnswer")) {
                    String requireAnswer = json.getString("requireAnswer");
                    if (!TextUtils.isEmpty(requireAnswer)) {
                        info.setRequireAnswer(requireAnswer);
                    }
                }
                if (json.has("spareContent")) {
                    String spareContent = json.getString("spareContent");
                    if (!TextUtils.isEmpty(spareContent)) {
                        info.setSpareContent(spareContent);
                    }
                }
                if (json.has("spareType")) {
                    String spareType = json.getString("spareType");
                    if (!TextUtils.isEmpty(spareType)) {
                        info.setSpareType(Integer.parseInt(spareType));
                    }
                }
                return info;
            } catch (Exception e) {
                Log.i("alarm", "parseAppRemind  JSONException");
            }
        }
        return info;
    }

}
