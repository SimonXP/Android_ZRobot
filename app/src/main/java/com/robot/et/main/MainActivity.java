package com.robot.et.main;

import android.content.Intent;
import android.os.Bundle;

import com.robot.et.R;
import com.robot.et.service.software.iflytek.IflySpeakService;
import com.robot.et.service.software.iflytek.IflyTextUnderstanderService;
import com.robot.et.service.software.iflytek.IflyVoiceToTextService;
import com.robot.et.service.software.system.music.MusicPlayerService;
import com.robot.et.service.software.turing.TuRingService;

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

        initService();
    }

    private void initService() {
        //语音听写
        startService(new Intent(this, IflyVoiceToTextService.class));
        //语音合成
        startService(new Intent(this, IflySpeakService.class));
//        //文本理解
//        startService(new Intent(this, IflyTextUnderstanderService.class));
//        //图灵
//        startService(new Intent(this, TuRingService.class));
//        //音乐
//        startService(new Intent(this, MusicPlayerService.class));
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
//        stopService(new Intent(this, IflySpeakService.class));
//        stopService(new Intent(this, IflyTextUnderstanderService.class));
//        stopService(new Intent(this, TuRingService.class));
//        stopService(new Intent(this, MusicPlayerService.class));
    }

}
