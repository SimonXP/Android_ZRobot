package com.robot.et.core.software.video.agora;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;

import com.robot.et.common.BroadcastAction;
import com.robot.et.common.DataConfig;
import com.robot.et.common.RequestConfig;
import com.robot.et.common.ScriptConfig;
import com.robot.et.core.software.common.speech.SpeechImpl;
import com.robot.et.entity.JpushInfo;
import com.robot.et.util.BroadcastEnclosure;
import com.robot.et.util.SharedPreferencesKeys;
import com.robot.et.util.SharedPreferencesUtils;
import com.xsj.crasheye.Crasheye;

public class AgoraService extends Service {

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i("agora", "AgoraService  onCreate()");
        Crasheye.initWithNativeHandle(this, DataConfig.AGORA_CRASHEYE_KEY);

        IntentFilter filter = new IntentFilter();
        filter.addAction(BroadcastAction.ACTION_CONNECT_AGORA);
        filter.addAction(BroadcastAction.ACTION_JOIN_AGORA_ROOM);
        registerReceiver(receiver, filter);

    }

    BroadcastReceiver receiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(BroadcastAction.ACTION_CONNECT_AGORA)) {//连接进入agora界面
                Log.i("agora", "type===" + intent.getIntExtra("type", 0));
                openChannelActivity(intent.getIntExtra("type", 0));

                intent.setAction(BroadcastAction.ACTION_MONITOR_WATCH_NETWORK_TRAFFIC_OPEN);
                sendBroadcast(intent);
            } else if (intent.getAction().equals(BroadcastAction.ACTION_JOIN_AGORA_ROOM)) {//极光推送过来进入agora
                Log.i("agora", "AgoraService    极光推送过来进入agora");
                JpushInfo info = intent.getParcelableExtra("JpushInfo");
                if (info != null) {
                    if (DataConfig.isVideoOrVoice) {//正在通话中
                        return;
                    }
                    joinAgoraRoom(info);
                }
            }
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }

    //加入agora房间
    private void joinAgoraRoom(JpushInfo info) {
        int extra = info.getExtra();
        String roomNum = info.getRoomNum();
        int agoraType = SharedPreferencesUtils.getInstance().getInt(SharedPreferencesKeys.AGORA_CALL_PATTERN, 0);
        Log.i("agoravideo", "extra===" + extra);
        Log.i("agoravideo", "agoraType===" + agoraType);
        switch (extra) {
            case RequestConfig.JPUSH_CALL_VIDEO:// agora视频
                if (agoraType == DataConfig.AGORA_CALL_NORMAL_PATTERN) {//正常模式
                    joinRoomerBegin(roomNum);
                    SystemClock.sleep(500);
                    DataConfig.isAgoraVideo = true;
                    SpeechImpl.getInstance().startSpeak(RequestConfig.JPUSH_CALL_VIDEO, info.getContent());
                }

                break;
            case RequestConfig.JPUSH_CALL_VOICE:// agora语音
                if (agoraType == DataConfig.AGORA_CALL_NORMAL_PATTERN) {//正常模式
                    joinRoomerBegin(roomNum);
                    SystemClock.sleep(500);
                    DataConfig.isAgoraVoice = true;
                    SpeechImpl.getInstance().startSpeak(RequestConfig.JPUSH_CALL_VOICE, info.getContent());
                }

                break;
            case RequestConfig.JPUSH_CALL_LOOK:// agora查看
                if (agoraType == DataConfig.AGORA_CALL_NORMAL_PATTERN) {//正常模式
                    joinRoomerBegin(roomNum);
                    openChannelActivity(RequestConfig.JPUSH_CALL_LOOK);
                }

                break;
            case RequestConfig.JPUSH_CALL_CLOSE:// 关闭agora声网
                Log.i("agoravideo", "关闭agora声网");
                if (DataConfig.isVideoOrVoice) {
                    Intent intent = new Intent();
                    intent.setAction(BroadcastAction.ACTION_CLOSE_AGORA);
                    sendBroadcast(intent);
                }

                break;

            default:
                break;
        }
    }

    private void joinRoomerBegin(String roomNum) {
        SpeechImpl.getInstance().cancelSpeak();
        SpeechImpl.getInstance().cancelListen();
        BroadcastEnclosure.stopMusic(this);
        BroadcastEnclosure.controlWaving(this, ScriptConfig.HAND_STOP, ScriptConfig.HAND_TWO, "0");
        BroadcastEnclosure.controlMouthLED(this, ScriptConfig.LED_OFF);

        SharedPreferencesUtils share = SharedPreferencesUtils.getInstance();
        share.putString(SharedPreferencesKeys.AGORA_ROOM_NUM, roomNum);
        share.putInt(SharedPreferencesKeys.AGORA_CALL_TYPE, DataConfig.PHONE_CALL_BY_MEN);
        share.commitValue();
    }

    //进入声网
    private void openChannelActivity(int type) {
        Intent intent = new Intent(this, ChannelActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(DataConfig.AGORA_EXTRA_CALLING_TYPE, type);
        intent.putExtra(DataConfig.AGORA_EXTRA_VENDOR_KEY, DataConfig.AGORA_KEY);
        intent.putExtra(DataConfig.AGORA_EXTRA_CHANNEL_ID, SharedPreferencesUtils.getInstance().getString(SharedPreferencesKeys.AGORA_ROOM_NUM, ""));
        startActivity(intent);
    }

}
