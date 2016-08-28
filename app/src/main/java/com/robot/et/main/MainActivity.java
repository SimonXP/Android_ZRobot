package com.robot.et.main;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.robot.et.R;
import com.robot.et.core.hardware.move.ControlMoveService;
import com.robot.et.core.hardware.wakeup.WakeUpServices;
import com.robot.et.core.software.iflytek.IflySpeakService;
import com.robot.et.core.software.iflytek.IflyTextUnderstanderService;
import com.robot.et.core.software.iflytek.IflyVoiceToTextService;
import com.robot.et.core.software.netty.NettyService;
import com.robot.et.core.software.system.music.MusicPlayerService;
import com.robot.et.core.software.turing.TuRingService;
import com.robot.et.core.software.window.MsgReceiverService;

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
    }

}
