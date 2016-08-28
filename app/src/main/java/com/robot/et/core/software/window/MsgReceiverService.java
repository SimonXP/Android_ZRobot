package com.robot.et.core.software.window;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;

import com.robot.et.R;
import com.robot.et.common.BroadcastAction;
import com.robot.et.util.BroadcastEnclosure;
import com.robot.et.common.DataConfig;
import com.robot.et.core.software.face.detector.FaceDetectorActivity;
import com.robot.et.util.SpeechlHandle;
import com.robot.et.core.software.script.ScriptHandler;

import java.util.Random;

/**
 * Created by houdeming on 2016/7/27.
 * 接受硬件消息的service
 */
public class MsgReceiverService extends Service {
    private Intent intent;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i("accept", "MsgReceiverService  onCreate()");
        intent = new Intent();

        IntentFilter filter = new IntentFilter();
        filter.addAction(BroadcastAction.ACTION_WAKE_UP_OR_INTERRUPT);
        filter.addAction(BroadcastAction.ACTION_PLAY_MUSIC_END);
        filter.addAction(BroadcastAction.ACTION_SPEAK);
        filter.addAction(BroadcastAction.ACTION_FACE_DISTINGUISH);
        filter.addAction(BroadcastAction.ACTION_NOTIFY_SOFTWARE);

        registerReceiver(receiver, filter);

    }

    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(BroadcastAction.ACTION_WAKE_UP_OR_INTERRUPT)) {//唤醒中断
                Log.i("accept", "MsgReceiverService 接受到唤醒中断的广播");
                responseAwaken();
            } else if (intent.getAction().equals(BroadcastAction.ACTION_SPEAK)) {//说话
                Log.i("accept", "MsgReceiverService  说话");
                int currentType = intent.getIntExtra("type", 0);
                String content = intent.getStringExtra("content");
                if (!TextUtils.isEmpty(content)) {
                    SpeechlHandle.startSpeak(currentType, content);
                } else {
                    SpeechlHandle.startListen();
                }
            } else if (intent.getAction().equals(BroadcastAction.ACTION_PLAY_MUSIC_END)) {//音乐播放完成
                Log.i("accept", "MsgReceiverService  音乐播放完成");
                SpeechlHandle.startListen();
            } else if (intent.getAction().equals(BroadcastAction.ACTION_FACE_DISTINGUISH)) {//脸部识别
                Log.i("accept", "MsgReceiverService  脸部识别");
                String contetn = intent.getStringExtra("content");
                SpeechlHandle.startSpeak(DataConfig.SPEAK_TYPE_CHAT, contetn);
            } else if (intent.getAction().equals(BroadcastAction.ACTION_NOTIFY_SOFTWARE)) {//接受到硬件反馈
                Log.i("accept", "MsgReceiverService  接受到硬件反馈");
                if (DataConfig.isPlayScript) {
                    new ScriptHandler().scriptAction(MsgReceiverService.this);
                }
            }

        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }

    private void responseAwaken() {
        //停止说
        SpeechlHandle.cancelSpeak();
        //停止听
        SpeechlHandle.cancelListen();
        //停止唱歌
        BroadcastEnclosure.stopMusic(this);

        //是否在人脸识别
        if (FaceDetectorActivity.instance != null) {
            FaceDetectorActivity.instance.finish();
            FaceDetectorActivity.instance = null;
        }

        DataConfig.isScriptQA = false;
        DataConfig.isAppPushRemind = false;
        DataConfig.isStartTime = false;
        DataConfig.isControlToyCar = false;

        SpeechlHandle.startSpeak(DataConfig.SPEAK_TYPE_CHAT, getAwakenContent());

    }

    //获取唤醒时要说的内容
    private String getAwakenContent() {
        String content = "";
        String[] wakeUpSpeakContent = getResources().getStringArray(R.array.wake_up_speak_content);
        int size = wakeUpSpeakContent.length;
        if (wakeUpSpeakContent != null && size > 0) {
            int i = new Random().nextInt(size);
            content = wakeUpSpeakContent[i];
        }
        return content;
    }

}
