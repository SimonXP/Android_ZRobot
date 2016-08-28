package com.robot.et.core.software.voice.iflytek.util;

import android.text.TextUtils;
import android.util.Log;

import com.iflytek.cloud.RecognizerResult;
import com.robot.et.core.software.voice.iflytek.ParseResultCallBack;

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
    public static void parseAnswerResult(String result, ParseResultCallBack callBack) {
        try {
            JSONTokener tokener = new JSONTokener(result);
            JSONObject jObject = new JSONObject(tokener);
            int isSuccessInt = jObject.getInt("rc");
            String question = jObject.getString("text");
            String service = "";
            // rc=0 操作成功
            if (isSuccessInt == 0) {
                service = jObject.getString("service");
            }
            callBack.getResult(question, service, jObject);
        } catch (JSONException e) {
            Log.i(TAG, "parseIatAnswerResult  JSONException");
            callBack.onError(e.getMessage());
        }
    }

    //百科,计算器,日期,社区问答,褒贬&问候&情绪,闲聊
    public static String getAnswerData(JSONObject jObject) {
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
        String content = "";
        try {
            JSONObject jsonObject = jObject.getJSONObject("data");
            JSONArray cookArray = jsonObject.getJSONArray("result");
            //菜谱返回的是json数组，默认使用第一个（提高解析效率）备注：第一条数据比较准确。
            JSONObject object = cookArray.getJSONObject(0);
            String ingredient = object.getString("ingredient");// 主要材料
            String accessory = object.getString("accessory");// 辅助材料

            StringBuffer buffer = new StringBuffer(1024);
            buffer.append("主料：").append(ingredient);
            if (!TextUtils.isEmpty(accessory)) {
                buffer.append("，辅料：").append(accessory);
            }
            content = buffer.toString();

        } catch (JSONException e) {
            Log.i(TAG, "getCookBookData  JSONException");
        }
        return content;
    }

    //获取音乐
    public static String getMusicData(JSONObject jObject, String musicSplit) {
        String json = "";
        try {
            JSONObject jsonObject = jObject.getJSONObject("data");
            JSONArray musicArray = jsonObject.getJSONArray("result");
            List<String> musics = new ArrayList<String>();
            int length = musicArray.length();
            for (int i = 0; i < length; i++) {
                JSONObject object = musicArray.getJSONObject(i);
                String url = object.getString("downloadUrl");// 音乐地址
                String singer = "";
                if (object.has("singer")) {
                    singer = object.getString("singer");//歌手
                }
                String musicName = "";
                if (object.has("name")) {
                    musicName = object.getString("name");//歌曲
                }
                if (!TextUtils.isEmpty(url)) {
                    StringBuffer buffer = new StringBuffer(1024);
                    buffer.append(singer).append(musicSplit).append(musicName).append(musicSplit).append(url);
                    //歌手+歌名 + 歌曲src
                    musics.add(buffer.toString());
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
    public static String getRemindData(JSONObject jObject, String scheduleSplit) {
        String json = "";
        try {
            JSONObject jsonObject = jObject.getJSONObject("semantic");
            JSONObject object = jsonObject.getJSONObject("slots");
            String content = object.getString("content");// 做什么事
            JSONObject dataObject = object.getJSONObject("datetime");
            String time = dataObject.getString("time");// 时间
            String date = dataObject.getString("date");// 日期

            StringBuffer buffer = new StringBuffer(1024);
            // 日期 + 时间 + 做什么事
            buffer.append(date).append(scheduleSplit).append(time).append(scheduleSplit).append(content);
            json = buffer.toString();

        } catch (JSONException e) {
            Log.i(TAG, "getRemindData  JSONException");
        }
        return json;
    }

    //获取天气
    public static String getWeatherData(JSONObject jObject, String city, String area) {
        String content = "";
        try {
            JSONObject object1 = jObject.getJSONObject("semantic");
            JSONObject object2 = object1.getJSONObject("slots");
            JSONObject object3 = object2.getJSONObject("datetime");
            String time = "";
            if (object3.has("dateOrig")) {
                time = object3.getString("dateOrig");// 日期
            } else {
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

            //天气返回的是json数组，默认使用第一个（提高解析效率）备注：第一条数据字段是最全面的。
            JSONObject object = dataArray.getJSONObject(0);
            String airQuality = "";
            if (object.has("airQuality")) {
                airQuality = object.getString("airQuality");// 空气质量
            }
            String wind = object.getString("wind");// 风向以及风力
            String weather = object.getString("weather");// 天气现象
            String tempRange = object.getString("tempRange");// 气温范围25℃~19℃
            String pmValue = "";
            if (object.has("pm25")) {
                pmValue = object.getString("pm25");// 空气质量
            }

            StringBuffer buffer = new StringBuffer(1024);
            if (city.contains(iflyCity)) {// 是当前城市
                if (!TextUtils.isEmpty(iflyArea)) {
                    buffer.append(time).append(iflyCity).append(iflyArea);
                } else {
                    buffer.append(time).append(iflyCity).append(area);
                }
            } else {// 不是当前城市
                if (!TextUtils.isEmpty(iflyArea)) {
                    buffer.append(time).append(iflyCity).append(iflyArea);
                } else {
                    if (TextUtils.equals(iflyCity, "CURRENT_CITY")) {
                        content = buffer.append(time).toString();
                        return content;
                    } else {
                        buffer.append(time).append(iflyCity);
                    }

                }
            }

            if (TextUtils.isEmpty(airQuality)) {
                buffer.append("天气：").append(weather).append(",风力：").append(wind).append(",气温：").append(tempRange);
            } else {
                buffer.append("天气：").append(weather).append(",空气质量：").append(airQuality).append(",风力：").append(wind).append(",气温：").append(tempRange);
            }

            if (!TextUtils.isEmpty(pmValue)) {
                buffer.append(",pm值：").append(pmValue);
            }

            content = buffer.toString();
        } catch (JSONException e) {
            Log.i(TAG, "getWeatherData  JSONException");
        }
        return content;
    }

    //获取打电话
    public static String getPhoneData(JSONObject jObject) {
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
    public static String getPm25Data(JSONObject jObject, String city, String area) {
        String content = "";
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

            //空气质量返回的是json数组，默认使用第一个（提高解析效率）备注：第一条数据比较准确。
            JSONObject object = dataArray.getJSONObject(0);
            String pmValue = object.getString("pm25");// pm值
            String weather = object.getString("quality");// 空气质量
            String aqi = object.getString("aqi");// 空气质量指数

            StringBuffer buffer = new StringBuffer(1024);
            if (city.contains(iflyCity)) {// 是当前城市
                if (!TextUtils.isEmpty(iflyArea)) {// 有返回区
                    buffer.append(iflyCity).append(iflyArea);
                } else {// 没有返回区
                    buffer.append(iflyCity).append(area);
                }
            } else {// 不是当前城市
                if (!TextUtils.isEmpty(iflyArea)) {// 有返回区
                    buffer.append(iflyCity).append(iflyArea);
                } else {// 没有返回区
                    buffer.append(iflyCity);
                }
            }

            buffer.append("pm值：").append(pmValue).append(",空气质量：").append(weather).append(",空气质量指数：").append(aqi);
            content = buffer.toString();

        } catch (JSONException e) {
            Log.i(TAG, "getPm25Data  JSONException");
        }
        return content;
    }

}
