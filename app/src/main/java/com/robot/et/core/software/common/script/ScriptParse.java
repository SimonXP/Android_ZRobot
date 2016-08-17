package com.robot.et.core.software.common.script;

import android.text.TextUtils;
import android.util.Log;

import com.robot.et.entity.ScriptActionInfo;
import com.robot.et.entity.ScriptInfo;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by houdeming on 2016/8/2.
 */
public class ScriptParse {
    private final static String TAG = "netty";

    //获取剧本
    public static void parseScript(String jsonContent, ScriptInfoCallBack callBack) {
        if (!TextUtils.isEmpty(jsonContent)) {
            try {
                JSONTokener tokener = new JSONTokener(jsonContent);
                JSONObject json = new JSONObject(tokener);
                String scriptContent = json.getString("scriptContent");
                ScriptInfo scriptInfo = new ScriptInfo();
                scriptInfo.setScriptContent(scriptContent);
                List<ScriptActionInfo> infos = new ArrayList<ScriptActionInfo>();
                JSONArray array = json.getJSONArray("script");
                infos = getInfos(array);

                callBack.getScribt(scriptInfo, infos);
            } catch (Exception e) {
                Log.i(TAG, "parseScript  JSONException");
                callBack.getScribt(null, null);
            }

        }
    }

    //增加APP发过来的录制动作
    public static void parseAppRecordAction(String jsonContent, ScriptInfoCallBack callBack) {
        if (!TextUtils.isEmpty(jsonContent)) {
            try {
                JSONTokener tokener = new JSONTokener(jsonContent);
                JSONObject json = new JSONObject(tokener);
                String scriptContent = json.getString("actionName");
                ScriptInfo scriptInfo = new ScriptInfo();
                scriptInfo.setScriptContent(scriptContent);
                List<ScriptActionInfo> infos = new ArrayList<ScriptActionInfo>();
                JSONArray array = json.getJSONArray("actions");
                infos = getInfos(array);

                callBack.getScribt(scriptInfo, infos);
            } catch (Exception e) {
                Log.i(TAG, "addAppRecordScript  JSONException");
                callBack.getScribt(null, null);
            }

        }
    }

    //增加APP发过来的音乐编舞
    public static void parseAppRecordMusic(String jsonContent, ScriptInfoCallBack callBack) {
        if (!TextUtils.isEmpty(jsonContent)) {
            try {
                JSONTokener tokener = new JSONTokener(jsonContent);
                JSONObject json = new JSONObject(tokener);
                String scriptContent = json.getString("songName");
                ScriptInfo scriptInfo = new ScriptInfo();
                scriptInfo.setScriptContent(scriptContent);
                List<ScriptActionInfo> infos = new ArrayList<ScriptActionInfo>();
                JSONArray array = json.getJSONArray("editDance");
                infos = getInfos(array);

                callBack.getScribt(scriptInfo, infos);
            } catch (Exception e) {
                Log.i(TAG, "addAppRecordScript  JSONException");
                callBack.getScribt(null, null);
            }

        }
    }

    private static List<ScriptActionInfo> getInfos(JSONArray array) {
        List<ScriptActionInfo> infos = new ArrayList<ScriptActionInfo>();
        try {
            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);
                ScriptActionInfo info = new ScriptActionInfo();
                String actionType = object.getString("actionType");
                if (!TextUtils.isEmpty(actionType)) {
                    if (TextUtils.isDigitsOnly(actionType)) {
                        info.setActionType(Integer.parseInt(actionType));
                    }
                }
                if (object.has("content")) {
                    String content = object.getString("content");
                    if (!TextUtils.isEmpty(content)) {
                        info.setContent(content);
                    }
                }
                if (object.has("spareType")) {
                    String spareType = object.getString("spareType");
                    if (!TextUtils.isEmpty(spareType)) {
                        if (TextUtils.isDigitsOnly(spareType)) {
                            info.setSpareType(Integer.parseInt(spareType));
                        }
                    }
                }
                if (object.has("spareContent")) {
                    String spareContent = object.getString("spareContent");
                    if (!TextUtils.isEmpty(spareContent)) {
                        info.setSpareContent(spareContent);
                    }
                }
                if (object.has("spareContent2")) {
                    String spareContent2 = object.getString("spareContent2");
                    if (!TextUtils.isEmpty(spareContent2)) {
                        info.setSpareContent2(spareContent2);
                    }
                }
                infos.add(info);
            }
        } catch (Exception e) {
            Log.i(TAG, "getInfos  JSONException");
        }
        return infos;
    }

}
