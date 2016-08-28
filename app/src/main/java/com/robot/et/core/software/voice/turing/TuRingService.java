package com.robot.et.core.software.voice.turing;

import android.content.Intent;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;

import com.robot.et.common.DataConfig;
import com.robot.et.core.software.common.speech.SpeechImpl;
import com.robot.et.core.software.voice.SpeechService;
import com.turing.androidsdk.InitListener;
import com.turing.androidsdk.SDKInit;
import com.turing.androidsdk.SDKInitBuilder;
import com.turing.androidsdk.TuringApiManager;

import org.json.JSONException;
import org.json.JSONObject;

import turing.os.http.core.ErrorMessage;
import turing.os.http.core.HttpConnectionListener;
import turing.os.http.core.RequestResult;

public class TuRingService extends SpeechService {

    private TuringApiManager mTuringApiManager;

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i("turing", "TuRingService  onCreate()");
        SpeechImpl.setService(this);

        initTuringSDK();

    }

    // turingSDK初始化
    private void initTuringSDK() {
        SDKInitBuilder builder = new SDKInitBuilder(this).setSecret(DataConfig.TURING_SECRET)
                .setTuringKey(DataConfig.TURING_APPID).setUniqueId(DataConfig.TURING_UNIQUEID);

        SDKInit.init(builder, new InitListener() {
            @Override
            public void onFail(String error) {
                Log.i("turing", "图灵error===" + error);
                //异常处理 异常后重新去初始化
                initTuringSDK();
            }

            @Override
            public void onComplete() {
                // 获取userid成功后，才可以请求Turing服务器，需要请求必须在此回调成功，才可正确请求
                mTuringApiManager = new TuringApiManager(TuRingService.this);
                mTuringApiManager.setHttpListener(myHttpConnectionListener);
            }
        });
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    HttpConnectionListener myHttpConnectionListener = new HttpConnectionListener() {

        @Override
        public void onSuccess(RequestResult result) {
            if (result != null) {
                try {
                    JSONObject result_obj = new JSONObject(result.getContent().toString());
                    if (result_obj.has("text")) {
                        String content = (String) result_obj.get("text");
                        Log.i("turing", "图灵content====" + content);
                        if (!TextUtils.isEmpty(content)) {
                            //从科大讯飞没有获取到天气问图灵
                            if (content.contains(":") && content.contains("周") && content.contains("风") && content.contains(";")) {
                                Log.i("turing", "从科大讯飞没有获取到天气问图灵");
                                content = getWeatherContent(content);
                            }

                            SpeechImpl.getInstance().startSpeak(DataConfig.SPEAK_TYPE_CHAT, content);
                        } else {
                            SpeechImpl.getInstance().startListen();
                        }

                    } else {
                        SpeechImpl.getInstance().startListen();
                    }
                } catch (JSONException e) {
                    Log.i("turing", "图灵JSONException====" + e.getMessage());
                    SpeechImpl.getInstance().startListen();
                }
            } else {
                SpeechImpl.getInstance().startListen();
            }
        }

        @Override
        public void onError(ErrorMessage errorMessage) {
            Log.i("turing", "图灵errorMessage.getMessage()====" + errorMessage.getMessage());
            SpeechImpl.getInstance().startListen();
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void understanderTextByTuring(String content) {
        super.understanderTextByTuring(content);
        if (!TextUtils.isEmpty(content)) {
            //发出请求  以防mTuringApiManager为null
            if (mTuringApiManager != null) {
                mTuringApiManager.requestTuringAPI(content);
            } else {
                SpeechImpl.getInstance().startListen();
            }
        } else {
            SpeechImpl.getInstance().startListen();
        }
    }

    /*
         * content weather格式 上海:05/16 周一,15-24° 23° 晴 北风微风; 05/17 周二,16-26° 晴 东南风微风;
         * 05/18 周三,17-26° 多云 东风微风; 05/19 周四,19-26° 多云 东风微风;
         */
    private String getWeatherContent(String content) {
        String result = "";
        if (!TextUtils.isEmpty(content)) {
            String[] datas = content.split(";");
            result = datas[0];
            String[] tempDatas = result.split(",");
            result = tempDatas[1];
            String cityData = tempDatas[0];
            String[] citys = cityData.split("\\:");
            String city = citys[0];

            String[] weathers = result.split(" ");
            // weathers[0]15-24° weathers[1]23° weathers[2]晴 weathers[3]北风微风
            if (weathers != null && weathers.length > 0) {
                StringBuffer buffer = new StringBuffer(1024);
                buffer.append(city).append("市").append("天气：").append(weathers[2]).append(",气温：").append(weathers[0]).append(",风力：").append(weathers[3]);
                result = buffer.toString();
            }
        }
        return result;
    }

}
