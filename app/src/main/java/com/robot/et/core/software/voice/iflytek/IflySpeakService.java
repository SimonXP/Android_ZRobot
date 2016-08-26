package com.robot.et.core.software.voice.iflytek;

import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SynthesizerListener;
import com.robot.et.R;
import com.robot.et.common.DataConfig;
import com.robot.et.common.RequestConfig;
import com.robot.et.core.software.common.script.ScriptHandler;
import com.robot.et.core.software.common.speech.SpeechImpl;
import com.robot.et.core.software.common.view.EmotionManager;
import com.robot.et.core.software.common.view.ViewCommon;
import com.robot.et.core.software.voice.SpeechService;
import com.robot.et.util.AlarmRemindManager;
import com.robot.et.util.BroadcastEnclosure;
import com.robot.et.util.DateTools;
import com.robot.et.util.MusicManager;

public class IflySpeakService extends SpeechService {
    // 语音合成对象
    private SpeechSynthesizer mTts;
    private int currentType;
    private boolean isFirstSetParam;

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i("ifly", "IflySpeakService  onCreate()");
        // 初始化合成对象
        mTts = SpeechSynthesizer.createSynthesizer(this, mTtsInitListener);
        SpeechImpl.setService(this);

        DataConfig.isSleep = false;
        //入口，说欢迎语
        startSpeak(DataConfig.SPEAK_TYPE_WELCOME, getWelcomeContent());

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    private void speakContent(String content) {
        if (!isFirstSetParam) {
            isFirstSetParam = true;
            setTextToVoiceParam(mTts, DataConfig.DEFAULT_SPEAK_MEN, "60", "50", "100");
        }

        int code = mTts.startSpeaking(content, mTtsListener);
        // * 只保存音频不进行播放接口,调用此接口请注释startSpeaking接口
        // * text:要合成的文本，uri:需要保存的音频全路径，listener:回调接口
        // String path =
        // Environment.getExternalStorageDirectory()+"/tts.pcm";
        // int code = mTts.synthesizeToUri(text, path, mTtsListener);

        if (code != ErrorCode.SUCCESS) {
            if (code == ErrorCode.ERROR_COMPONENT_NOT_INSTALLED) {
                // 未安装则跳转到提示安装页面
            } else {
                Log.i("ifly", "语音合成失败,错误码=== " + code);
                SpeechImpl.getInstance().startListen();
            }
        }
    }

    // 初始化监听
    private InitListener mTtsInitListener = new InitListener() {
        @Override
        public void onInit(int code) {
            if (code != ErrorCode.SUCCESS) {
                // 初始化失败,错误码
            } else {
                // 初始化成功，之后可以调用startSpeaking方法
                // 注：有的开发者在onCreate方法中创建完合成对象之后马上就调用startSpeaking进行合成，
                // 正确的做法是将onCreate中的startSpeaking调用移至这里
            }
        }
    };

    // 合成回调监听
    private SynthesizerListener mTtsListener = new SynthesizerListener() {
        // 开始播放
        @Override
        public void onSpeakBegin() {
            Log.i("ifly", "IflySpeakService  onSpeakBegin()");
        }

        // 暂停播放
        @Override
        public void onSpeakPaused() {
        }

        // 继续播放
        @Override
        public void onSpeakResumed() {
        }

        // 合成进度
        @Override
        public void onBufferProgress(int percent, int beginPos, int endPos,
                                     String info) {
        }

        // 播放进度
        @Override
        public void onSpeakProgress(int percent, int beginPos, int endPos) {
        }

        @Override
        public void onCompleted(SpeechError error) {
            Log.i("ifly", "IflySpeakService  onCompleted()");

            if (error == null) {
                responseSpeakCompleted();
            } else {
                Log.i("ifly", "onCompleted  error=" + error.getPlainDescription(true));
                SpeechImpl.getInstance().startListen();
            }

        }

        @Override
        public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {
            // 以下代码用于获取与云端的会话id，当业务出错时将会话id提供给技术支持人员，可用于查询会话日志，定位出错原因
            // 若使用本地能力，会话id为null
            // if (SpeechEvent.EVENT_SESSION_ID == eventType) {
            // String sid = obj.getString(SpeechEvent.KEY_EVENT_SESSION_ID);
            // Log.d(TAG, "session id =" + sid);
            // }
        }
    };

    private void responseSpeakCompleted() {
        switch (currentType) {
            case DataConfig.SPEAK_TYPE_CHAT://对话
                showNormalEmotion(true);
                SpeechImpl.getInstance().startListen();
                break;
            case DataConfig.SPEAK_TYPE_MUSIC_START://音乐开始播放前的提示
                showNormalEmotion(true);
                BroadcastEnclosure.startPlayMusic(this, MusicManager.getMusicSrc());
                break;
            case DataConfig.SPEAK_TYPE_DO_NOTHINF://什么都不处理
                showNormalEmotion(false);
                EmotionManager.showEmotion(R.mipmap.emotion_blink);//睡觉状态
                //do nothing
                break;
            case DataConfig.SPEAK_TYPE_SHOW_QRCODE://显示二维码的图片
                SpeechImpl.getInstance().startListen();
                break;
            case DataConfig.SPEAK_TYPE_REMIND_TIPS://闹铃提醒
                showNormalEmotion(true);
                if (DataConfig.isAppPushRemind) {
                    SpeechImpl.getInstance().startListen();
                    return;
                }

                String alarmContent = AlarmRemindManager.getMoreAlarmContent();
                startSpeak(DataConfig.SPEAK_TYPE_REMIND_TIPS, alarmContent);
                break;
            case DataConfig.SPEAK_TYPE_WELCOME://欢迎语
                showNormalEmotion(true);
//                String weatherContent = new StringBuffer(1024).append("今天").append(city).append(area).append("的天气").toString();
//                SpeechImpl.getInstance().understanderTextByIfly(weatherContent);
                SpeechImpl.getInstance().startListen();

                break;
            case DataConfig.SPEAK_TYPE_SCRIPT://剧本对话
                if (DataConfig.isScriptQA) {
                    SpeechImpl.getInstance().startListen();
                    return;
                }
                new ScriptHandler().scriptSpeak(this);

                break;
            case RequestConfig.JPUSH_CALL_CLOSE://视频或语音时电话挂断
                showNormalEmotion(true);
                // do nothing
                break;
            case RequestConfig.JPUSH_CALL_VIDEO://视频通话
                showNormalEmotion(true);
                BroadcastEnclosure.connectAgora(this, RequestConfig.JPUSH_CALL_VIDEO);
                break;
            case RequestConfig.JPUSH_CALL_VOICE://语音通话
                showNormalEmotion(true);
                BroadcastEnclosure.connectAgora(this, RequestConfig.JPUSH_CALL_VOICE);
                break;
            default:
                break;
        }
    }

    private void showNormalEmotion(boolean isShow) {
        ViewCommon.initView();
        if (isShow) {
            EmotionManager.showEmotion(R.mipmap.emotion_normal);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopSpeak();
        // 退出时释放连接
        mTts.destroy();
    }

    private void stopSpeak() {
        if (mTts.isSpeaking()) {
            mTts.stopSpeaking();
        }
    }

    @Override
    public void startSpeak(int speakType, String speakContent) {
        super.startSpeak(speakType, speakContent);
        Log.i("ifly", "IflySpeakService  speakType===" + speakType);
        Log.i("ifly", "IflySpeakService  speakContent===" + speakContent);
        currentType = speakType;
        if (!TextUtils.isEmpty(speakContent)) {
            if (currentType == DataConfig.SPEAK_TYPE_REMIND_TIPS) {//提醒
                cancelSpeak();
                SpeechImpl.getInstance().cancelListen();
                //停止唱歌
                BroadcastEnclosure.stopMusic(IflySpeakService.this);
            }

            speakContent(speakContent);
        } else {
            SpeechImpl.getInstance().startListen();
        }
    }

    @Override
    public void cancelSpeak() {
        super.cancelSpeak();
        stopSpeak();
    }

    /*科大讯飞语音合成参数设置
         * speakMen 发音人
         * speed 语速
         * pitch 语调
         * volume 音量
         */
    private void setTextToVoiceParam(com.iflytek.cloud.SpeechSynthesizer mTts, String speakMen, String speed, String pitch, String volume) {
        // 清空参数
        mTts.setParameter(SpeechConstant.PARAMS, null);
        // 根据合成引擎设置相应参数
        mTts.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD);
        // 设置合成发音人
        mTts.setParameter(SpeechConstant.VOICE_NAME, speakMen);
        // 设置合成语速
        mTts.setParameter(SpeechConstant.SPEED, speed);
        // 设置合成音调
        mTts.setParameter(SpeechConstant.PITCH, pitch);
        // 设置合成音量
        mTts.setParameter(SpeechConstant.VOLUME, volume);
        // 设置播放器音频流类型
        mTts.setParameter(SpeechConstant.STREAM_TYPE, "3");
        // 设置播放合成音频打断音乐播放，默认为true
        mTts.setParameter(SpeechConstant.KEY_REQUEST_FOCUS, "true");
    }

    //得到当前时间的欢迎语
    private String getWelcomeContent() {
        int currentHour = DateTools.getCurrentHour(System.currentTimeMillis());
        String content = "";
        if (0 <= currentHour && currentHour < 12) {// 早上
            content = "主人，早上好";
        } else if (12 <= currentHour && currentHour < 13) {// 中午
            content = "主人，中午好";
        } else if (13 <= currentHour && currentHour < 18) {// 下午
            content = "主人，下午好";
        } else if (18 <= currentHour && currentHour < 24) {// 晚上
            content = "主人，晚上好";
        }
        return content;
    }

}
