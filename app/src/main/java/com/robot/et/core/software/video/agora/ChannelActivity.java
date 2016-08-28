package com.robot.et.core.software.video.agora;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.robot.et.R;
import com.robot.et.common.BroadcastAction;
import com.robot.et.common.DataConfig;
import com.robot.et.common.RequestConfig;
import com.robot.et.core.software.common.speech.SpeechImpl;
import com.robot.et.util.SharedPreferencesKeys;
import com.robot.et.util.SharedPreferencesUtils;
import com.robot.et.util.Utilities;

import java.util.Random;

import io.agora.rtc.IRtcEngineEventHandler;
import io.agora.rtc.RtcEngine;
import io.agora.rtc.video.VideoCanvas;

public class ChannelActivity extends BaseEngineEventHandlerActivity {
    private int mCallingType;
    private SurfaceView mLocalView;
    private String vendorKey;
    private String channelId;
    private LinearLayout mRemoteUserContainer;
    private int mRemoteUserViewWidth = 0;
    RtcEngine rtcEngine;
    //判断用户是否接通 默认不接通
    private boolean isUserJoined;
    //查看
    private boolean isLook;
    private int mLastRxBytes = 0;
    private int mLastTxBytes = 0;
    private int mLastDuration = 0;
    private SharedPreferencesUtils share;
    private boolean isNetWorkNotGood;
    private int currentType;
    private boolean isCurrentVideo;

    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        setContentView(R.layout.activity_agora_room);
        // 保持屏幕常亮
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        DataConfig.isVideoOrVoice = true;
        //单位标准尺寸转化的一个函数
        mRemoteUserViewWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 80, getResources().getDisplayMetrics());
        mCallingType = getIntent().getIntExtra(DataConfig.AGORA_EXTRA_CALLING_TYPE, 0);
        channelId = getIntent().getStringExtra(DataConfig.AGORA_EXTRA_CHANNEL_ID);
        Log.i("agoravideo", "ChannelActivity channelId===" + channelId);
        currentType = mCallingType;

        //打电话的类型，是被叫还是主动呼叫   0:被叫 1:主动呼叫
        share = SharedPreferencesUtils.getInstance();
        int callType = share.getInt(SharedPreferencesKeys.AGORA_CALL_TYPE, 0);
        Log.i("agoravideo", "ChannelActivity callType  0:被叫 1:主动呼叫===" + callType);

        setupRtcEngine();

        //显示当前通话对方的预览布局
        mRemoteUserContainer = (LinearLayout) findViewById(R.id.user_remote_views);
        setRemoteUserViewVisibility(false);
        isLook = false;

        switch (mCallingType) {
            case RequestConfig.JPUSH_CALL_VIDEO:// 视频
                DataConfig.isAgoraVideo = false;
                DataConfig.isAlarmTips = false;
                video();

                break;
            case RequestConfig.JPUSH_CALL_VOICE:// 语音
                DataConfig.isAgoraVoice = false;
                DataConfig.isAlarmTips = false;
                voice();

                break;
            case RequestConfig.JPUSH_CALL_LOOK:// 查看
                isLook = true;
                DataConfig.isAgoraLook = true;
                video();

                break;

            default:
                break;
        }

        if (!Utilities.isNetworkConnected(this)) {
            onError(104);
        }

        isUserJoined = false;

        Log.i("agoravideo", "islook===" + isLook);
        Log.i("agoravideo", "mCallingType===" + mCallingType);

        //30秒如果用户没有接通的话，自动挂断电话
        if (callType == DataConfig.PHONE_CALL_TO_MEN) {
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    if (!isUserJoined) {
                        setRemoteUserViewVisibility(false);
                        leaveChannel();
                        isUserJoined = true;
                        String content = "主人，对方暂时没有接听，如果需要，要重新拨通哦";
                        SpeechImpl.getInstance().startSpeak(RequestConfig.JPUSH_CALL_CLOSE, content);

                        finish();
                    }
                }
            }, 30 * 1000);
        }

        IntentFilter filter = new IntentFilter();
        filter.addAction(BroadcastAction.ACTION_CLOSE_AGORA);
        filter.addAction(BroadcastAction.ACTION_MONITOR_WATCH_NETWORK_TRAFFIC_SPEED);
        registerReceiver(receiver, filter);

    }

    BroadcastReceiver receiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(BroadcastAction.ACTION_CLOSE_AGORA)) {//关闭agora
                Log.i("agoravideo", "ChannelActivity   关闭agora");
                closeChannel();
            } else if (intent.getAction().equals(BroadcastAction.ACTION_MONITOR_WATCH_NETWORK_TRAFFIC_SPEED)) {//检测网络变化
                boolean isNetBad = intent.getBooleanExtra("NetWorkSpeed", false);
                Log.i("agoravideo", "ChannelActivity   检测网络变化isNetBad===" + isNetBad);
                if (currentType == RequestConfig.JPUSH_CALL_VIDEO) {//当前是视频通话
                    if (isNetBad) {//网络差
                        if (isCurrentVideo) {
                            Log.i("agoravideo", "ChannelActivity   切换语音");
                            voice();
                        }

                    } else {//网络好
                        if (!isCurrentVideo) {
                            Log.i("agoravideo", "ChannelActivity   切换视频");
                            video();
                        }
                    }
                }

            }
        }
    };

    //退出当前通话
    private void closeChannel() {
        setRemoteUserViewVisibility(false);
        leaveChannel();
        isUserJoined = true;
        finish();
        Log.i("agoravideo", "islook222===" + isLook);
        if (isLook || isNetWorkNotGood) { // 查看
            Intent intent = new Intent();
            if (isLook) {
                isLook = false;
                intent.setAction(BroadcastAction.ACTION_PHONE_HANGUP);
                sendBroadcast(intent);
            } else {
                isNetWorkNotGood = false;
                String content = "主人，网络质量太差，如果需要，要重新拨通哦";
                SpeechImpl.getInstance().startSpeak(RequestConfig.JPUSH_CALL_CLOSE, content);
            }
        } else {
            String content = "主人，对方已挂断，如果需要，要重新拨通哦";
            SpeechImpl.getInstance().startSpeak(RequestConfig.JPUSH_CALL_CLOSE, content);
        }

        Intent intent = new Intent();
        intent.setAction(BroadcastAction.ACTION_MONITOR_WATCH_NETWORK_TRAFFIC_CLOSE);
        sendBroadcast(intent);
        Log.i("agoravideo", "ChannelActivity  关闭网络流量监控广播发出");
    }

    //加入通话频道
    private void setupChannel() {
        rtcEngine.joinChannel(vendorKey, channelId, "", new Random().nextInt(Math.abs((int) System.currentTimeMillis())));
    }

    // 初始化声网的RtcEngine对象
    private void setupRtcEngine() {
        vendorKey = getIntent().getStringExtra(DataConfig.AGORA_EXTRA_VENDOR_KEY);
        MessageHandler messageHandler = new MessageHandler();
        rtcEngine = RtcEngine.create(this, vendorKey, messageHandler);
        messageHandler.setActivity(this);
        rtcEngine.enableVideo();
    }

    //语音视频通话的view
    @SuppressWarnings("static-access")
    private void ensureLocalViewIsCreated() {
        if (this.mLocalView == null) {
            FrameLayout localViewContainer = (FrameLayout) findViewById(R.id.user_local_view);
            SurfaceView localView = rtcEngine.CreateRendererView(getApplicationContext());
            this.mLocalView = localView;
            localViewContainer.addView(localView, new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));

            rtcEngine.enableVideo();
            rtcEngine.setupLocalVideo(new VideoCanvas(this.mLocalView));
        }

    }

	/*
     * rtcEngine.muteLocalAudioStream(false);//静音
	 * rtcEngine.setEnableSpeakerphone(false);// 扬声器
	 * rtcEngine.muteLocalVideoStream(false);// 关闭摄像头
	 * rtcEngine.switchCamera();// 切换摄像头
	 */

    //语音通话
    private void voice() {
        mCallingType = RequestConfig.JPUSH_CALL_VOICE;
        findViewById(R.id.user_local_voice_bg).setVisibility(View.VISIBLE);
        isCurrentVideo = false;

        ensureLocalViewIsCreated();

        rtcEngine.setEnableSpeakerphone(true);// 扬声器开
        rtcEngine.muteAllRemoteAudioStreams(false);
        rtcEngine.muteLocalAudioStream(false);
        rtcEngine.disableVideo();
        rtcEngine.muteLocalVideoStream(true);
        rtcEngine.muteAllRemoteVideoStreams(true);

        if (mRemoteUserContainer.getChildCount() == 0) {
            setupChannel();
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                updateRemoteUserViews(RequestConfig.JPUSH_CALL_VOICE);
            }
        }, 500);

    }

    //视频通话
    private void video() {
        findViewById(R.id.user_local_voice_bg).setVisibility(View.GONE);
        isCurrentVideo = true;
        ensureLocalViewIsCreated();

        if (RequestConfig.JPUSH_CALL_VIDEO == mCallingType) { // 视频
            rtcEngine.setEnableSpeakerphone(true);// 扬声器开
            rtcEngine.muteAllRemoteAudioStreams(false);
        } else if (RequestConfig.JPUSH_CALL_LOOK == mCallingType) { // 查看
            rtcEngine.setEnableSpeakerphone(false);// 扬声器关
            rtcEngine.muteAllRemoteAudioStreams(true);//静音所有远端音频
        }

        mCallingType = RequestConfig.JPUSH_CALL_VIDEO;

        rtcEngine.enableVideo();
        rtcEngine.muteLocalVideoStream(false);
        rtcEngine.muteLocalAudioStream(false);
        rtcEngine.muteAllRemoteVideoStreams(false);
        //设置本地视频属性
        rtcEngine.setVideoProfile(41);

        if (mRemoteUserContainer.getChildCount() == 0) {
            setupChannel();
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                updateRemoteUserViews(RequestConfig.JPUSH_CALL_VIDEO);
            }
        }, 500);

    }

    //视频质量回调
    @Override
    public void onNetworkQuality(int quality) {
        super.onNetworkQuality(quality);
        Log.i("agoravideo", "onNetworkQuality  quality===" + quality);
    }

    private void setRemoteUserViewVisibility(boolean isVisible) {
        findViewById(R.id.user_remote_views).getLayoutParams().height = isVisible ? (int) TypedValue
                .applyDimension(TypedValue.COMPLEX_UNIT_DIP, 80, getResources().getDisplayMetrics()) : 0;
    }

    //切换视频音频通话时，更新 view 的显示。只是更新重用的 view，并不新添加
    private void updateRemoteUserViews(int callingType) {
        int visibility = View.GONE;
        if (RequestConfig.JPUSH_CALL_VIDEO == callingType) {
            visibility = View.GONE;
        } else if (RequestConfig.JPUSH_CALL_VOICE == callingType) {
            visibility = View.VISIBLE;
        }

        for (int i = 0, size = mRemoteUserContainer.getChildCount(); i < size; i++) {

            View singleRemoteView = mRemoteUserContainer.getChildAt(i);
            singleRemoteView.findViewById(R.id.remote_user_voice_container).setVisibility(visibility);

            if (RequestConfig.JPUSH_CALL_VIDEO == callingType) {
                FrameLayout remoteVideoUser = (FrameLayout) singleRemoteView.findViewById(R.id.viewlet_remote_video_user);
                if (remoteVideoUser.getChildCount() > 0) {
                    final SurfaceView remoteView = (SurfaceView) remoteVideoUser.getChildAt(0);
                    if (remoteView != null) {
                        remoteView.setZOrderOnTop(true);
                        remoteView.setZOrderMediaOverlay(true);
                        int savedUid = (Integer) remoteVideoUser.getTag();
                        rtcEngine.setupRemoteVideo(new VideoCanvas(remoteView, VideoCanvas.RENDER_MODE_ADAPTIVE, savedUid));
                    }
                }

            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
        isUserJoined = true;
        DataConfig.isAlarmTips = true;
        DataConfig.isAgoraLook = false;
        DataConfig.isVideoOrVoice = false;
    }

    //自己离开通话频道
    private void leaveChannel() {
        rtcEngine.leaveChannel();
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    //更新码率
    public void onUpdateSessionStats(final IRtcEngineEventHandler.RtcStats stats) {
        Log.i("agoravideo", "onUpdateSessionStats");
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // 码率
                int kbs = ((stats.txBytes + stats.rxBytes - mLastTxBytes - mLastRxBytes) / 1024 / (stats.totalDuration - mLastDuration + 1));
                mLastRxBytes = stats.rxBytes;
                mLastTxBytes = stats.txBytes;
                mLastDuration = stats.totalDuration;
                Log.i("agoravideo", "kbs===" + kbs);

            }
        });
    }

    //远端视频接收解码回调
    public synchronized void onFirstRemoteVideoDecoded(final int uid, int width, int height, final int elapsed) {
        Log.i("agoravideo", "onFirstRemoteVideoDecoded");

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                View remoteUserView = mRemoteUserContainer.findViewById(Math.abs(uid));
                if (remoteUserView == null) {
                    LayoutInflater layoutInflater = getLayoutInflater();
                    View singleRemoteUser = layoutInflater.inflate(R.layout.viewlet_remote_user, null);
                    singleRemoteUser.setId(Math.abs(uid));

                    TextView username = (TextView) singleRemoteUser.findViewById(R.id.remote_user_name);
                    username.setText(String.valueOf(uid));

                    mRemoteUserContainer.addView(singleRemoteUser, new LinearLayout.LayoutParams(mRemoteUserViewWidth, mRemoteUserViewWidth));

                    remoteUserView = singleRemoteUser;
                }

                FrameLayout remoteVideoUser = (FrameLayout) remoteUserView.findViewById(R.id.viewlet_remote_video_user);
                remoteVideoUser.removeAllViews();
                remoteVideoUser.setTag(uid);

                // ensure remote video view setup
                final SurfaceView remoteView = RtcEngine.CreateRendererView(getApplicationContext());
                remoteVideoUser.addView(remoteView,
                        new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                                FrameLayout.LayoutParams.MATCH_PARENT));
                remoteView.setZOrderOnTop(true);
                remoteView.setZOrderMediaOverlay(true);

                rtcEngine.enableVideo();
                int successCode = rtcEngine.setupRemoteVideo(new VideoCanvas(
                        remoteView, VideoCanvas.RENDER_MODE_ADAPTIVE, uid));

                if (successCode < 0) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            rtcEngine.setupRemoteVideo(new VideoCanvas(remoteView,
                                    VideoCanvas.RENDER_MODE_ADAPTIVE, uid));
                            remoteView.invalidate();
                        }
                    }, 500);
                }

                if (remoteUserView != null && RequestConfig.JPUSH_CALL_VIDEO == mCallingType) {
                    remoteUserView.findViewById(R.id.remote_user_voice_container).setVisibility(View.GONE);
                } else {
                    remoteUserView.findViewById(R.id.remote_user_voice_container).setVisibility(View.VISIBLE);
                }

                TextView appNotification = (TextView) findViewById(R.id.app_notification);
                appNotification.setText("");
                setRemoteUserViewVisibility(true);
            }
        });

    }

    // 其他用户加入当前频道回调
    public synchronized void onUserJoined(final int uid, int elapsed) {
        Log.i("agoravideo", "onUserJoined");
        View existedUser = mRemoteUserContainer.findViewById(Math.abs(uid));
        if (existedUser != null) {
            return;
        }

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                View singleRemoteUser = mRemoteUserContainer.findViewById(Math.abs(uid));
                if (singleRemoteUser != null) {
                    return;
                }

                isUserJoined = true;

                LayoutInflater layoutInflater = getLayoutInflater();
                singleRemoteUser = layoutInflater.inflate(R.layout.viewlet_remote_user, null);
                singleRemoteUser.setId(Math.abs(uid));

                TextView username = (TextView) singleRemoteUser.findViewById(R.id.remote_user_name);
                username.setText(String.valueOf(uid));

                mRemoteUserContainer.addView(singleRemoteUser, new LinearLayout.LayoutParams(mRemoteUserViewWidth,
                        mRemoteUserViewWidth));

                TextView appNotification = (TextView) findViewById(R.id.app_notification);
                appNotification.setText("");
                setRemoteUserViewVisibility(true);
                rtcEngine.setRemoteRenderMode(uid, 1);
            }
        });

    }

    // 其他用户离开当前频道回调
    public void onUserOffline(final int uid) {
        Log.i("agoravideo", "onUserOffline");
        if (isFinishing()) {
            return;
        }

        if (mRemoteUserContainer == null) {
            return;
        }

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                View userViewToRemove = mRemoteUserContainer.findViewById(Math.abs(uid));
                mRemoteUserContainer.removeView(userViewToRemove);

                Log.i("agoravideo", "当前房间还剩余人数======" + mRemoteUserContainer.getChildCount());
                //当前没人视频或者通话
                if (mRemoteUserContainer.getChildCount() == 0) {
                    closeChannel();
                }
            }
        });

    }

    // 离开频道回调
    @Override
    public void onLeaveChannel(IRtcEngineEventHandler.RtcStats stats) {
        Log.i("agoravideo", "onLeaveChannel");
        try {
            super.onLeaveChannel(stats);
            finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 用户停止/重启视频回调
    public void onUserMuteVideo(final int uid, final boolean muted) {
        Log.i("agoravideo", "onUserMuteVideo");
        if (isFinishing()) {
            return;
        }

        if (mRemoteUserContainer == null) {
            return;
        }

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                View remoteView = mRemoteUserContainer.findViewById(Math.abs(uid));
                remoteView.findViewById(R.id.remote_user_voice_container)
                        .setVisibility((RequestConfig.JPUSH_CALL_VOICE == mCallingType || (RequestConfig.JPUSH_CALL_VIDEO == mCallingType && muted)) ? View.VISIBLE : View.GONE);
                remoteView.invalidate();
            }
        });

    }

    // 发生错误回调
    @Override
    public synchronized void onError(int err) {
        Log.i("agoravideo", "onError   err====" + err);
        if (isFinishing()) {//界面是否finish
            Log.i("agoravideo", "onError  isFinishing");
            return;
        }

        //不是查看的时候报异常问题
        if (!isLook) {
            if (err == 101) {
                showError("声网key异常，如果需要，要重新拨通哦");
                finish();
            } else if (err == 104) {
                showError("网络异常，如果需要，要重新拨通哦");
                finish();
            }

        }
    }

    private void showError(String content) {
        SpeechImpl.getInstance().startSpeak(RequestConfig.JPUSH_CALL_CLOSE, content);
    }

}