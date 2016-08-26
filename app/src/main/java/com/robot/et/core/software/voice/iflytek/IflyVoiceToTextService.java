package com.robot.et.core.software.voice.iflytek;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.LexiconListener;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.robot.et.R;
import com.robot.et.common.DataConfig;
import com.robot.et.core.software.common.speech.CommandHandler;
import com.robot.et.core.software.common.speech.SpeechImpl;
import com.robot.et.core.software.common.view.EmotionManager;
import com.robot.et.core.software.common.view.TextManager;
import com.robot.et.core.software.common.view.ViewCommon;
import com.robot.et.core.software.voice.SpeechService;
import com.robot.et.core.software.voice.iflytek.util.ResultParse;
import com.robot.et.util.FileUtils;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Timer;
import java.util.TimerTask;

public class IflyVoiceToTextService extends SpeechService {
    // 语音听写对象
    private SpeechRecognizer mIat;
    // 用HashMap存储听写结果
    private HashMap<String, String> mIatResults = new LinkedHashMap<String, String>();
    private boolean isFirstSetParam;
    private Timer timer;
    private boolean isFirstListen;
    private CommandHandler commandHandler;

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i("ifly", "IflyVoiceToTextService  onCreate()");
        // 初始化SpeechRecognizer对象
        mIat = SpeechRecognizer.createRecognizer(this, mTtsInitListener);
        SpeechImpl.setService(this);
        commandHandler = new CommandHandler(this);

        uploadUserThesaurus();//上传词表

    }

    private void beginListen() {
        if (DataConfig.isAppPushRemind) {
            commandHandler.noResponseApp();
        }

        if (!isFirstListen) {
            isFirstListen = true;
            startTimer();
        }

        ViewCommon.initView();
        EmotionManager.showEmotion(R.mipmap.emotion_normal);

        listen(DataConfig.DEFAULT_SPEAK_MEN);
    }

    //开始计时
    private void startTimer() {
        stopTimer();
        if (timer == null) {
            timer = new Timer();
        }
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.sendEmptyMessage(0);
            }
        }, 5 * 60 * 1000);//设置多少分钟沉睡（单位毫秒）
    }

    //停止计时
    private void stopTimer() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            stopTimer();
            stopListen();
            commandHandler.sleep();
        }
    };

    private void listen(String language) {
        mIatResults.clear();
        // 设置参数
        if (!isFirstSetParam) {
            isFirstSetParam = true;
            setVoiceToTextParam(mIat, language);
        }
        // 不显示听写对话框
        int ret = mIat.startListening(mRecognizerListener);

        if (ret != ErrorCode.SUCCESS) {
            Log.i("ifly", "IflyVoiceToTextService  听写失败 ret===" + ret);
            beginListen();
        }

    }

    //听写监听器
    private RecognizerListener mRecognizerListener = new RecognizerListener() {

        @Override
        public void onBeginOfSpeech() {
            // 此回调表示：sdk内部录音机已经准备好了，用户可以开始语音输入
            Log.i("ifly", "onBeginOfSpeech()");
        }

        @Override
        public void onError(SpeechError error) {
            Log.i("ifly", "onError ");
            // 错误码：10118(您没有说话)，可能是录音机权限被禁，需要提示用户打开应用的录音权限。
            // 如果使用本地功能（语记）需要提示用户开启语记的录音权限。
            stopListen();
            beginListen();
        }

        @Override
        public void onEndOfSpeech() {
            // 此回调表示：检测到了语音的尾端点，已经进入识别过程，不再接受语音输入
            Log.i("ifly", "结束说话 ");
        }

        @Override
        public void onResult(RecognizerResult results, boolean isLast) {
            Log.i("ifly", "onResult ");

            // 有人说话
            String result = ResultParse.printResult(results, mIatResults);
            Log.i("ifly", "问题原版result====" + result);
            if (isLast) {
                Log.i("ifly", "onResult  isLast");
                stopListen();
                if (!TextUtils.isEmpty(result)) {
                    if (result.length() == 1) {
                        beginListen();
                        return;
                    }

                    stopTimer();
                    isFirstListen = false;

                    ViewCommon.initView();
                    TextManager.showText(result);

                    if (commandHandler.isCustorm(result)) {
                        return;
                    }
                    SpeechImpl.getInstance().understanderTextByIfly(result);

                } else {
                    beginListen();
                }
            }
        }

        @Override
        public void onVolumeChanged(int volume, byte[] data) {
            Log.i("ifly", "当前正在说话，音量大小： volume==" + volume);
        }

        @Override
        public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {
            Log.i("ifly", "onEvent");
            // 以下代码用于获取与云端的会话id，当业务出错时将会话id提供给技术支持人员，可用于查询会话日志，定位出错原因
            // 若使用本地能力，会话id为null
            // if (SpeechEvent.EVENT_SESSION_ID == eventType) {
            // String sid = obj.getString(SpeechEvent.KEY_EVENT_SESSION_ID);
            // Log.d(TAG, "session id =" + sid);
            // }
        }
    };

    //初始化监听
    private InitListener mTtsInitListener = new InitListener() {
        @Override
        public void onInit(int code) {
            if (code != ErrorCode.SUCCESS) {
                //初始化失败,错误码
                Log.i("ifly", "初始化失败");
            } else {
                // 初始化成功，之后可以调用startSpeaking方法
                // 注：有的开发者在onCreate方法中创建完合成对象之后马上就调用startSpeaking进行合成，
                // 正确的做法是将onCreate中的startSpeaking调用移至这里
            }
        }
    };

    //上传词表监听器
    private LexiconListener mLexiconListener = new LexiconListener() {

        @Override
        public void onLexiconUpdated(String lexiconId, SpeechError error) {
            if (error != null) {
                Log.i("ifly", "上传联系人词表error===" + error.toString());
                uploadUserThesaurus();
            } else {
                Log.i("ifly", "上传联系人词表成功");
            }
        }
    };

    //上传词表
    private void uploadUserThesaurus() {
        String contents = FileUtils.readFile(this, "userwords", "utf-8");
        // 指定引擎类型
        mIat.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD);
        // 置编码类型
        mIat.setParameter(SpeechConstant.TEXT_ENCODING, "utf-8");
        mIat.updateLexicon("userword", contents, mLexiconListener);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopListen();
        mIat.destroy();
        stopTimer();
    }

    private void stopListen() {
        if (mIat.isListening()) {
            mIat.cancel();
        }
    }

    @Override
    public void startListen() {
        super.startListen();
        stopTimer();
        isFirstListen = false;
        DataConfig.isSleep = false;

        beginListen();
    }

    @Override
    public void cancelListen() {
        super.cancelListen();
        stopListen();
    }

    /*科大讯飞语音听写
    * isTypeCloud  本地还是云端
    * language 听的语言
    * thresholdValue  门限值
    */
    private void setVoiceToTextParam(com.iflytek.cloud.SpeechRecognizer mIat, String language) {
        // 清空参数
        mIat.setParameter(SpeechConstant.PARAMS, null);
        // 设置听写引擎
        mIat.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD);
        // 设置返回结果格式
        mIat.setParameter(SpeechConstant.RESULT_TYPE, "json");
        if (language.equals("en_us")) {
            // 设置语言
            mIat.setParameter(SpeechConstant.LANGUAGE, "en_us");
        } else {
            // 设置语言
            mIat.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
            // 设置语言区域
            mIat.setParameter(SpeechConstant.ACCENT, language);
        }

        // 设置语音前端点:静音超时时间，即用户多长时间不说话则当做超时处理
        mIat.setParameter(SpeechConstant.VAD_BOS, "4000");

        // 设置语音后端点:后端点静音检测时间，即用户停止说话多长时间内即认为不再输入， 自动停止录音
        mIat.setParameter(SpeechConstant.VAD_EOS, "1000");

        // 设置标点符号,设置为"0"返回结果无标点,设置为"1"返回结果有标点
        mIat.setParameter(SpeechConstant.ASR_PTT, "0");
    }

}
