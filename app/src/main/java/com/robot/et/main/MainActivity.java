package com.robot.et.main;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;

import com.robot.et.R;
import com.robot.et.core.hardware.move.ControlMoveService;
import com.robot.et.core.hardware.wakeup.WakeUpServices;
import com.robot.et.core.software.common.receiver.MsgReceiverService;
import com.robot.et.core.software.common.push.netty.NettyService;
import com.robot.et.core.software.system.media.MusicPlayerService;
import com.robot.et.core.software.video.agora.AgoraService;
import com.robot.et.core.software.voice.iflytek.IflySpeakService;
import com.robot.et.core.software.voice.iflytek.IflyTextUnderstanderService;
import com.robot.et.core.software.voice.iflytek.IflyVoiceToTextService;
import com.robot.et.core.software.voice.turing.TuRingService;
import com.robot.et.util.SharedPreferencesKeys;
import com.robot.et.util.SharedPreferencesUtils;

import org.ros.android.RosActivity;
import org.ros.node.NodeMainExecutor;

import java.net.URI;

public class MainActivity extends RosActivity {

    public MainActivity(){
        super("XRobot","Xrobot", URI.create("http://192.168.3.1:11311"));//本体的ROS IP和端口
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // 保持屏幕常亮
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        //记录城市、区域位置
        SharedPreferencesUtils share = SharedPreferencesUtils.getInstance();
        share.putString(SharedPreferencesKeys.CITY_KEY, "上海市");
        share.putString(SharedPreferencesKeys.AREA_KEY, "浦东新区");
        share.commitValue();

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i("main", "onResume()");
        initService();
    }

    private void initService() {
        //netty
        startService(new Intent(this, NettyService.class));
        //语音听写
        startService(new Intent(this, IflyVoiceToTextService.class));
        //文本理解
        startService(new Intent(this, IflyTextUnderstanderService.class));
        //图灵
        startService(new Intent(this, TuRingService.class));
        //音乐
        startService(new Intent(this, MusicPlayerService.class));
        //唤醒
        startService(new Intent(this, WakeUpServices.class));
        //接受发来的消息
        startService(new Intent(this, MsgReceiverService.class));
        //语音合成
        startService(new Intent(this, IflySpeakService.class));
        //控制动
        startService(new Intent(this, ControlMoveService.class));
        //agora
        startService(new Intent(this, AgoraService.class));
    }

    @Override
    protected void init(NodeMainExecutor nodeMainExecutor) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        
        destoryService();
    }

    private void destoryService() {
        stopService(new Intent(this, IflyVoiceToTextService.class));
        stopService(new Intent(this, IflySpeakService.class));
        stopService(new Intent(this, IflyTextUnderstanderService.class));
        stopService(new Intent(this, TuRingService.class));
        stopService(new Intent(this, MusicPlayerService.class));
        stopService(new Intent(this, WakeUpServices.class));
        stopService(new Intent(this, MsgReceiverService.class));
        stopService(new Intent(this, NettyService.class));
        stopService(new Intent(this, ControlMoveService.class));
        stopService(new Intent(this, AgoraService.class));
    }

}
