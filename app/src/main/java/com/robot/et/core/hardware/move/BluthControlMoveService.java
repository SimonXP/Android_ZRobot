package com.robot.et.core.hardware.move;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.robot.et.common.BroadcastAction;
import com.robot.et.common.DataConfig;
import com.robot.et.common.ScriptConfig;
import com.robot.et.entity.BluthSendInfo;
import com.robot.et.entity.RobotAction;
import com.robot.et.util.BroadcastEnclosure;

public class BluthControlMoveService extends Service {
    private int i;
    private int controlNumAways;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(BroadcastAction.ACTION_CONTROL_AROUND_TOYCAR);
        filter.addAction(BroadcastAction.ACTION_CONTROL_WAVING);
        filter.addAction(BroadcastAction.ACTION_CONTROL_MOUTH_LED);
        filter.addAction(BroadcastAction.ACTION_CONTROL_TOYCAR_AWAYS);
        filter.addAction(BroadcastAction.ACTION_ROBOT_TURN_HEAD);
        registerReceiver(receiver, filter);
    }

    BroadcastReceiver receiver = new BroadcastReceiver() {

//		private String direction;
//		private String tempDigit;

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(BroadcastAction.ACTION_CONTROL_AROUND_TOYCAR)) {//控制周围小车
                Log.i("Move", "控制周围小车");
                int direction = intent.getIntExtra("direction", 0);
                int toyCarNum = intent.getIntExtra("toyCarNum", 0);
                Log.i("Move", "控制周围小车direction===" + direction);
                Log.i("Move", "toyCarNum===" + toyCarNum);
                contrlToyCarMove(direction, toyCarNum);

                if (DataConfig.isControlToyCar) {
                    if (direction == 1 || direction == 2) {
                        controlNumAways = 10;
                    } else if (direction == 3 || direction == 4) {
                        controlNumAways = 100;
                    }

                    if (direction != 5) {
                        SystemClock.sleep(200);
                        intent.setAction(BroadcastAction.ACTION_CONTROL_TOYCAR_AWAYS);
                        intent.putExtra("directionType", direction);
                        intent.putExtra("toyCarNum", toyCarNum);
                        sendBroadcast(intent);
                    } else {
                        Log.i("Move", "directionType 停止===" + direction);
                        DataConfig.controlNum = controlNumAways;
                        contrlToyCarMove(5, toyCarNum);
                    }
                }

                BroadcastEnclosure.notifySoftware(BluthControlMoveService.this);

            } else if (intent.getAction().equals(BroadcastAction.ACTION_CONTROL_WAVING)) {//举手摆手
                Log.i("Move", "举手摆手");
                String handDirection = intent.getStringExtra("handDirection");
                String handCategory = intent.getStringExtra("handCategory");
                String num = intent.getStringExtra("num");
                Log.i("Move", "handCategory===" + handCategory);
                if (!TextUtils.isEmpty(handDirection) && !TextUtils.isEmpty(handCategory)) {
                    handAction(handDirection, handCategory);
                }

                BroadcastEnclosure.notifySoftware(BluthControlMoveService.this);

            } else if (intent.getAction().equals(BroadcastAction.ACTION_CONTROL_MOUTH_LED)) {//嘴的LED灯
                Log.i("Move", "嘴的LED灯");
                String LEDState = intent.getStringExtra("LEDState");
                if (!TextUtils.isEmpty(LEDState)) {
                    controlMouthLED(LEDState);
                }
            } else if (intent.getAction().equals(BroadcastAction.ACTION_CONTROL_TOYCAR_AWAYS)) {//语音不停的控制小车
                Log.i("Move", "语音不停的控制小车");
                int direction = intent.getIntExtra("directionType", 0);
                int toyCarNum = intent.getIntExtra("toyCarNum", 0);
                DataConfig.controlNum++;
                if (DataConfig.controlNum < controlNumAways) {
                    BroadcastEnclosure.controlToyCarMove(BluthControlMoveService.this, direction, toyCarNum);
                }

            } else if (intent.getAction().equals(BroadcastAction.ACTION_ROBOT_TURN_HEAD)) {//控制头转
                Log.i("Move", "控制头转");
                int directionValue = intent.getIntExtra("direction", DataConfig.TURN_HEAD_ABOUT);
                String angleValue = intent.getStringExtra("angle");
                if (!TextUtils.isEmpty(angleValue)) {
                    controlHeadTurn(directionValue, angleValue);
                }
            }


        }
    };

    //控制小车
    private void contrlToyCarMove(int directionType, int toyCarNum) {
        if (directionType != 0) {
            Log.i("Move", "控制机器人周围玩具toyCarNum===" + toyCarNum);
            RobotAction action = new RobotAction();
            action.setCategory("go");
            switch (directionType) {
                case 1:
                    Log.i("Move", "玩具控制 向前");
                    action.setAction("forward");
                    break;
                case 2:
                    Log.i("Move", "玩具控制 向后");
                    action.setAction("backward");
                    break;
                case 3:
                    Log.i("Move", "玩具控制 向左");
                    action.setAction("turnLeft");
                    break;
                case 4:
                    Log.i("Move", "玩具控制 向右");
                    action.setAction("turnRight");
                    break;
                case 5:
                    Log.i("Move", "玩具控制 停止");
                    action.setAction("stop");
                    break;
                default:
                    break;
            }
            action.setCarNum(toyCarNum);
            String json = JSON.toJSONString(action);
            sendMoveAction(json);
        }
    }

    //控制摆臂
    private void handAction(String handDirection, String handCategory) {
        BluthSendInfo info = new BluthSendInfo();
        info.setcG("Hand");
        if (TextUtils.equals(handDirection, ScriptConfig.HAND_UP)) {
            info.setaT("up");
        } else if (TextUtils.equals(handDirection, ScriptConfig.HAND_DOWN)) {
            info.setaT("down");
        } else if (TextUtils.equals(handDirection, ScriptConfig.HAND_WAVING)) {
            info.setaT("waving");
        } else if (TextUtils.equals(handDirection, ScriptConfig.HAND_STOP)) {
            info.setaT("stop");
        }

        if (TextUtils.equals(handCategory, ScriptConfig.HAND_LEFT)) {
            info.setSide("L");
        } else if (TextUtils.equals(handCategory, ScriptConfig.HAND_RIGHT)) {
            info.setSide("R");
        } else if (TextUtils.equals(handCategory, ScriptConfig.HAND_TWO)) {
            info.setSide("LR");
        }
        String json = JSON.toJSONString(info);
        sendMoveAction(json);
    }

    //控制嘴的LED
    private void controlMouthLED(String LEDState) {
        BluthSendInfo info = new BluthSendInfo();
        info.setcG("DP");
        if (TextUtils.equals(LEDState, ScriptConfig.LED_ON)) {
            info.setaT("ON");
        } else if (TextUtils.equals(LEDState, ScriptConfig.LED_OFF)) {
            info.setaT("OFF");
        } else if (TextUtils.equals(LEDState, ScriptConfig.LED_BLINK)) {
            info.setaT("blink");
        }
        String json = JSON.toJSONString(info);
        sendMoveAction(json);
    }

    //控制头转向
    private void controlHeadTurn(int directionValue, String angleValue) {
        BluthSendInfo info = new BluthSendInfo();
        info.setcG("DIS");
        if (directionValue == DataConfig.TURN_HEAD_ABOUT) {
            info.setaT("HZ");
        } else if (directionValue == DataConfig.TURN_HEAD_UP_DOWN) {
            info.setaT("VT");
        }
        info.setaG(angleValue);
        String json = JSON.toJSONString(info);
        sendMoveAction(json);
    }

    private void sendMoveAction(String result) {
        Log.i("Move", "json===" + result);
        if (!TextUtils.isEmpty(result)) {
            byte[] content = result.getBytes();
            byte[] end = new byte[]{0x0a};//结束符
            byte[] realcontent = byteMerger(content, end);
            Intent intent = new Intent();
            intent.setAction(BroadcastAction.ACTION_MOVE_TO_BLUTH);
            intent.putExtra("actioncontent", realcontent);
            sendBroadcast(intent);
        }
    }

    private byte[] byteMerger(byte[] first, byte[] second) {
        byte[] content = new byte[first.length + second.length];
        System.arraycopy(first, 0, content, 0, first.length);
        System.arraycopy(second, 0, content, first.length, second.length);
        return content;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }

}
