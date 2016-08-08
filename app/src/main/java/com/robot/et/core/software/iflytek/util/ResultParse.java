package com.robot.et.core.software.iflytek.util;

import android.text.TextUtils;
import android.util.Log;

import com.iflytek.cloud.RecognizerResult;
import com.robot.et.common.enums.EnumManager;
import com.robot.et.common.enums.SceneServiceEnum;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

/**
 * Created by houdeming on 2016/7/25.
 * 科大讯飞结果json解析
 */
public class ResultParse {

    private final static String TAG = "json";

    // 科大讯飞语音听写的结果json解析
    public static String printResult(RecognizerResult results, HashMap<String, String> mIatResults) {
        String text = parseVoiceToTextResult(results.getResultString());
        String sn = "";
        // 读取json结果中的sn字段
        try {
            JSONObject resultJson = new JSONObject(results.getResultString());
            sn = resultJson.optString("sn");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mIatResults.put(sn, text);
        StringBuffer resultBuffer = new StringBuffer();
        for (String key : mIatResults.keySet()) {
            resultBuffer.append(mIatResults.get(key));
        }
        String result = resultBuffer.toString();
        return result;
    }

    // 科大讯飞语音听写json解析
    private static String parseVoiceToTextResult(String json) {
        StringBuffer ret = new StringBuffer();
        try {
            JSONTokener tokener = new JSONTokener(json);
            JSONObject joResult = new JSONObject(tokener);

            JSONArray words = joResult.getJSONArray("ws");
            for (int i = 0; i < words.length(); i++) {
                // 转写结果词，默认使用第一个结果
                JSONArray items = words.getJSONObject(i).getJSONArray("cw");
                JSONObject obj = items.getJSONObject(0);
                ret.append(obj.getString("w"));
                // 如果需要多候选结果，解析数组其他字段
                // for(int j = 0; j < items.length(); j++)
                // {
                // JSONObject obj = items.getJSONObject(j);
                // ret.append(obj.getString("w"));
                // }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret.toString();
    }

    /*
         * 科大讯飞语义理解的json解析
         * service + question + answer
         */
    public static String[] parseAnswerResult(String result) {
        String[] results = null;
        try {
            JSONTokener tokener = new JSONTokener(result);
            JSONObject jObject = new JSONObject(tokener);
            int isSuccessInt = jObject.getInt("rc");
            results = new String[3];
            String question = jObject.getString("text");
            results[1] = question;

            // rc=0 操作成功
            if (isSuccessInt == 0) {
                String service = jObject.getString("service");

                results[0] = service;
                SceneServiceEnum serviceEnum = EnumManager.getIflyService(service);
                Log.i(TAG, "serviceEnum===" + serviceEnum);
                if(serviceEnum != null){
                    switch (serviceEnum) {
                        case BAIKE://百科
                            results[2] = getAnswerData(jObject);
                            break;
                        case CALC://计算器
                            results[2] = getAnswerData(jObject);
                            break;
                        case COOKBOOK://菜谱
                            results[2] = getCookBookData(jObject);
                            break;
                        case DATETIME://日期
                            results[2] = getAnswerData(jObject);
                            break;
                        case FAQ://社区问答
                            results[2] = getAnswerData(jObject);
                            break;
                        case FLIGHT://航班查询
                            // do nothing
                            results[2] = "";
                            break;
                        case HOTEL://酒店查询
                            // do nothing
                            results[2] = "";
                            break;
                        case MAP://地图查询
                            // do nothing
                            results[2] = "";
                            break;
                        case MUSIC://音乐
                            results[2] = getMusicData(jObject);
                            break;
                        case RESTAURANT://餐馆
                            // do nothing
                            results[2] = "";
                            break;
                        case SCHEDULE://提醒
                            results[2] = getRemindData(jObject);
                            break;
                        case STOCK://股票查询
                            // do nothing
                            results[2] = "";
                            break;
                        case TRAIN://火车查询
                            // do nothing
                            results[2] = "";
                            break;
                        case TRANSLATION://翻译
                            // do nothing
                            results[2] = "";
                            break;
                        case WEATHER://天气查询
                            results[2] = getWeatherData(jObject);
                            break;
                        case OPENQA://褒贬&问候&情绪
                            results[2] = getAnswerData(jObject);
                            break;
                        case TELEPHONE://打电话
                            results[2] = getPhoneData(jObject);
                            break;
                        case MESSAGE://发短信
                            // do nothing
                            results[2] = "";
                            break;
                        case CHAT://闲聊
                            results[2] = getAnswerData(jObject);
                            break;
                        case PM25://空气质量
                            results[2] = getPm25Data(jObject);
                            break;

                        default:
                            results[2] = "";
                            break;
                    }
                }else{
                    results[0] = "";
                    results[2] = "";
                }

            } else {
                // 返回的 rc!=0 无内容
                results[0] = "";
                results[2] = "";
            }

        } catch (JSONException e) {
            Log.i(TAG, "parseIatAnswerResult  JSONException");
        }
        return results;
    }

    //百科,计算器,日期,社区问答,褒贬&问候&情绪,闲聊
    public static String getAnswerData(JSONObject jObject){
        String json = "";
        try {
            JSONObject jsonObject = jObject.getJSONObject("answer");
            json = jsonObject.getString("text");
        } catch (JSONException e) {
            Log.i(TAG, "getAnswerData  JSONException");
        }
        return json;
    }

    //获取菜谱
    public static String getCookBookData(JSONObject jObject) {
        String json = "";
        try {
            JSONObject jsonObject = jObject.getJSONObject("data");
            JSONArray cookArray = jsonObject.getJSONArray("result");
            List<String> cooks = new ArrayList<String>();
            for (int i = 0; i < cookArray.length(); i++) {
                JSONObject object = cookArray.getJSONObject(i);
                String ingredient = object.getString("ingredient");// 主要材料
                String accessory = object.getString("accessory");// 辅助材料
                String content = "";
                if (!TextUtils.isEmpty(ingredient) && TextUtils.isEmpty(accessory)) {
                    content = "主料：" + ingredient;
                } else if (TextUtils.isEmpty(ingredient) && !TextUtils.isEmpty(accessory)) {
                    content = "辅料：" + accessory;
                } else if (!TextUtils.isEmpty(ingredient) && !TextUtils.isEmpty(accessory)) {
                    content = "主料：" + ingredient + "辅料：" + accessory;
                }
                cooks.add(content);
            }

            int size = cooks.size();
            if (cooks != null && size > 0) {
                Random random = new Random();
                int randNum = random.nextInt(size);
                json = cooks.get(randNum);
            }
        } catch (JSONException e) {
            Log.i(TAG, "getCookBookData  JSONException");
        }
        return json;
    }

    //获取音乐
    public static String getMusicData(JSONObject jObject){
        String json = "";
        try {
            JSONObject jsonObject = jObject.getJSONObject("data");
            JSONArray musicArray = jsonObject.getJSONArray("result");
            List<String> musics = new ArrayList<String>();
            for (int i = 0; i < musicArray.length(); i++) {
                JSONObject object = musicArray.getJSONObject(i);
                String url = object.getString("downloadUrl");// 音乐地址
                String singer = "";
                if(object.has("singer")){
                    singer = object.getString("singer");//歌手
                }
                String musicName = "";
                if(object.has("name")){
                    musicName = object.getString("name");//歌曲
                }
                if (!TextUtils.isEmpty(url)) {
                    //歌手+歌名 + 歌曲src
                    musics.add(singer + "&" + musicName + "&" + url);
                }
            }

            int size = musics.size();
            if (musics != null && size > 0) {
                Random random = new Random();
                int randNum = random.nextInt(size);
                json = musics.get(randNum);
            }
        } catch (JSONException e) {
            Log.i(TAG, "getMusicData  JSONException");
        }
        return json;
    }

    //获取提醒
    public static String getRemindData(JSONObject jObject){
        String json = "";
        try {
            JSONObject jsonObject = jObject.getJSONObject("semantic");
            JSONObject object = jsonObject.getJSONObject("slots");
            String content = object.getString("content");// 做什么事
            JSONObject dataObject = object.getJSONObject("datetime");
            String time = dataObject.getString("time");// 时间
            String date = dataObject.getString("date");// 日期

            // 日期 + 时间 + 做什么事
            json = date + "," + time + "," + content;

        } catch (JSONException e) {
            Log.i(TAG, "getRemindData  JSONException");
        }
        return json;
    }

    //获取天气
    public static String getWeatherData(JSONObject jObject){
        String json = "";
        try {
            JSONObject object1 = jObject.getJSONObject("semantic");
            JSONObject object2 = object1.getJSONObject("slots");
            JSONObject object3 = object2.getJSONObject("datetime");
            String time = "";
            if(object3.has("dateOrig")){
                time = object3.getString("dateOrig");// 日期
            }else{
                time = "今天";
            }
            JSONObject object4 = object2.getJSONObject("location");
            String iflyCity = object4.getString("city");// 城市
            String iflyArea = "";
            if (object4.has("area")) {// 区域
                iflyArea = object4.getString("area");// 区域
            }

            JSONObject jsonObject = jObject.getJSONObject("data");
            JSONArray dataArray = jsonObject.getJSONArray("result");
            List<String> weathers = new ArrayList<String>();
            List<String> weathersUnKnow = new ArrayList<String>();
            for (int i = 0; i < dataArray.length(); i++) {
                JSONObject object = dataArray.getJSONObject(i);
                String airQuality = object.getString("airQuality");// 空气质量
                String wind = object.getString("wind");// 风向以及风力
                String weather = object.getString("weather");// 天气现象
                String tempRange = object.getString("tempRange");// 气温范围25℃~19℃
                String content = "";
//                if (city.contains(iflyCity)) {// 是当前城市
//                    if (!TextUtils.isEmpty(iflyArea)) {
//                        content = time + iflyCity + iflyArea;
//                    } else {
//                        content = time + iflyCity + area;
//                    }
//                } else {// 不是当前城市
                    if (!TextUtils.isEmpty(iflyArea)) {
                        content = time + iflyCity + iflyArea;
                    } else {
                        if(TextUtils.equals(iflyCity, "CURRENT_CITY")){
                            json = time;
                            return json;
                        }else{
                            content = time + iflyCity;
                        }

                    }
//                }
                content = content + "天气："+ weather+ ",空气质量："+ airQuality+ ",风力："+ wind+ ",气温："+ tempRange	+ ",";

                if (!TextUtils.equals(airQuality, "未知")) {
                    weathers.add(content);
                } else {
                    weathersUnKnow.add(content);
                }
            }

            int size = weathers.size();
            if (weathers != null && size > 0) {
                Random random = new Random();
                int randNum = random.nextInt(size);
                json = weathers.get(randNum);
            } else if (weathersUnKnow != null && weathersUnKnow.size() > 0) {
                int sizes = weathersUnKnow.size();
                for (int i = 0; i < sizes; i++) {
                    Random random = new Random();
                    int randNum = random.nextInt(sizes);
                    json = weathersUnKnow.get(randNum);
                }
            } else {
                json = "";
            }

        } catch (JSONException e) {
            Log.i(TAG, "getWeatherData  JSONException");
        }
        return json;
    }

    //获取打电话
    public static String getPhoneData(JSONObject jObject){
        String json = "";
        try {
            JSONObject jsonObject = jObject.getJSONObject("semantic");
            JSONObject object = jsonObject.getJSONObject("slots");
            String name = "";
            if (object.has("name")) {
                name = object.getString("name");// 拨打电话的人名
            } else if (object.has("code")) {
                name = object.getString("code");// 拨打电话的号码
            }
            json = name;
        } catch (JSONException e) {
            Log.i(TAG, "getPhoneData  JSONException");
        }
        return json;
    }

    //获取空气质量
    public static String getPm25Data(JSONObject jObject){
        String json = "";
        try {
            JSONObject json1 = jObject.getJSONObject("semantic");
            JSONObject json2 = json1.getJSONObject("slots");
            JSONObject json3 = json2.getJSONObject("location");
            String iflyCity = json3.getString("city");// 城市
            String iflyArea = "";
            if (json3.has("area")) {
                iflyArea = json3.getString("area");// 区域
            }

            JSONObject jsonObject = jObject.getJSONObject("data");
            JSONArray dataArray = jsonObject.getJSONArray("result");
            List<String> weathers = new ArrayList<String>();

            for (int i = 0; i < dataArray.length(); i++) {
                JSONObject object = dataArray.getJSONObject(i);
                String pmValue = object.getString("pm25");// pm值
                String weather = object.getString("quality");// 空气质量
                String aqi = object.getString("aqi");// 空气质量指数

                String content = "";

//                if (city.contains(iflyCity)) {// 是当前城市
//                    if (!TextUtils.isEmpty(iflyArea)) {// 有返回区
//                        content = iflyCity + iflyArea;
//                    } else {// 没有返回区
//                        content = iflyCity + area;
//                    }
//                }
//                else {// 不是当前城市
                    if (!TextUtils.isEmpty(iflyArea)) {// 有返回区
                        content = iflyCity + iflyArea;
                    } else {// 没有返回区
                        content = iflyCity;
                    }
//                }

                content = content + "pm值：" + pmValue + ",空气质量指数：" + aqi + ",空气质量：" + weather;
                weathers.add(content);
            }

            int size = weathers.size();
            if (weathers != null && size > 0) {
                Random random = new Random();
                int randNum = random.nextInt(size);
                json = weathers.get(randNum);
            } else {
                json = "";
            }
        } catch (JSONException e) {
            Log.i(TAG, "getPm25Data  JSONException");
        }
        return json;
    }

}
