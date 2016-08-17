package com.robot.et.core.hardware.wakeup;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

import com.robot.et.common.BroadcastAction;
import com.robot.et.util.BroadcastEnclosure;
import com.robot.et.util.ShellUtils;

import java.util.ArrayList;
import java.util.List;

public class WakeUpServices extends Service {

    private int voiceFd;
    private int faceFd;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        IntentFilter filter = new IntentFilter();
        filter.addAction(BroadcastAction.ACTION_WAKE_UP_RESET);
        registerReceiver(receiver, filter);

        faceFd = WakeUp.faceWakeUpInit();
        Log.i("wakeup", "face faceFd==" + faceFd);
        faceWakeUp();

        openI2C();
        voiceFd = WakeUp.wakeUpInit();
        Log.i("wakeup", "voice voiceFd==" + voiceFd);
        voiceWakeUp();

    }

    //打开I2C，一般的手机上面会出现错误
    private void openI2C() {
        List<String> commnandList = new ArrayList<String>();
        commnandList.add("chmod 777 /sys/class/gpio");
        commnandList.add("chmod 777 /sys/class/gpio/export");
        commnandList.add("echo 68 > /sys/class/gpio/export");
        commnandList.add("chmod 777 /sys/class/gpio/gpio68");
        commnandList.add("chmod 777 /sys/class/gpio/gpio68/direction");
        commnandList.add("chmod 777 /sys/class/gpio/gpio68/edge");
        commnandList.add("chmod 777 /sys/class/gpio/gpio68/value");
        commnandList.add("echo 72 > /sys/class/gpio/export");
        commnandList.add("chmod 777 /sys/class/gpio/gpio72");
        commnandList.add("chmod 777 /sys/class/gpio/gpio72/direction");
        commnandList.add("chmod 777 /sys/class/gpio/gpio72/edge");
        commnandList.add("chmod 777 /sys/class/gpio/gpio72/value");
        commnandList.add("setenforce 0");
        ShellUtils.execCommand(commnandList, true);
    }

    //语言唤醒
    private void voiceWakeUp() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    if (voiceFd > 0) {
                        int wakeUpState = WakeUp.getWakeUpState();
//					Log.i("wakeup", "wakeUpState:" + wakeUpState);
                        if (wakeUpState == 1) {
                            int degree = WakeUp.getWakeUpDegree();
                            Log.i("wakeup", "degree:" + degree);
                            WakeUp.setGainDirection(0);// 设置麦克0为主麦
                            Intent intent = new Intent();
                            intent.setAction(BroadcastAction.ACTION_WAKE_UP_OR_INTERRUPT);
                            intent.putExtra("degree", degree);
                            sendBroadcast(intent);
                        } else {
//						 Log.i("wakeup", "no wakeUp");
                        }
                    } else {
//					 Log.i("wakeup", "未打开I2C");
                    }
                }
            }
        }).start();
    }

    //人脸唤醒
    private void faceWakeUp() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    if (faceFd > 0) {
                        int faceWakeUpState = WakeUp.getFaceWakeUpState();
                        if (faceWakeUpState == 1) {
                            //有人影进入范围
                            Log.i("wakeup", "检测到人影");
                            BroadcastEnclosure.openFaceRecognise(WakeUpServices.this, false);
                        } else {
                            //没有人影进入范围
//                            Log.i("wakeup", "未检测到人影");
                        }
                    } else {
//                        Log.e("wakeup", "faceWakeUp初始化失败");
                    }
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    BroadcastReceiver receiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(BroadcastAction.ACTION_WAKE_UP_RESET)) {
                Log.i("wakeup", "唤醒重置");
                int i = WakeUp.wakeUpReset();
            }
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        // 此处不要忘记关闭线程，暂时放在这里.2016-06-08
        Log.i("wakeup", "WakeUpServices onDestroy()");
        unregisterReceiver(receiver);
    }
}
