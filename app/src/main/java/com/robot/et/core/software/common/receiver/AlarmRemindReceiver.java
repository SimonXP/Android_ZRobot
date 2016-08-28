package com.robot.et.core.software.common.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.robot.et.common.DataConfig;
import com.robot.et.core.software.common.speech.SpeechImpl;
import com.robot.et.core.software.system.alarm.AlarmClock;
import com.robot.et.entity.RemindInfo;
import com.robot.et.util.AlarmRemindManager;

import java.util.ArrayList;
import java.util.List;

//闹铃接受器
public class AlarmRemindReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (!TextUtils.isEmpty(action)) {
            //只有是特定的广播才接受，做出相应   有提醒时，以时间格式发出的广播，这里只接受闹铃提醒的广播
            if (action.contains(DataConfig.ACTION_REMIND_SIGN)) {
                Log.i("alarm", "接受到提醒的广播");
                AlarmClock.getInstance().cancelOneAlarm(action);
                remindTips();
            }
        }
    }

    //闹铃提醒
    private void remindTips() {
        long minute = System.currentTimeMillis();
        List<RemindInfo> infos = AlarmRemindManager.getRemindTips(minute);
        int infoSize = infos.size();
        Log.i("alarm", "infos.size()===" + infoSize);
        if (infos != null && infoSize > 0) {
            List<String> datas = new ArrayList<String>();
            for (int i = 0; i < infoSize; i++) {
                //更新已经提醒过的内容
                RemindInfo info = infos.get(i);
                Log.i("alarm", "info.getContent()===" + info.getContent());
                datas.add(info.getContent());

                int frequency = info.getFrequency();
                if (frequency == DataConfig.alarmAllDay) {//每天
                    if (!TextUtils.isEmpty(info.getRemindMen())) {
                        //app提醒
                        Log.i("alarm", "app提醒");
                        AlarmRemindManager.deleteAppRemindTips(info.getOriginalAlarmTime());
                    } else {
                        //APP设置的闹铃
                        Log.i("alarm", "APP设置的闹铃");
                        minute += 24 * 60 * 60 * 1000;
                        AlarmRemindManager.updateRemindInfo(info, minute, DataConfig.alarmAllDay);
                        AlarmRemindManager.setMoreAlarm(minute);
                    }

                } else {//不是每天
                    if (frequency == 1) {
                        AlarmRemindManager.deleteCurrentRemindTips(minute);
                    } else {
                        minute += 24 * 60 * 60 * 1000;
                        AlarmRemindManager.updateRemindInfo(info, minute, frequency - 1);
                        AlarmRemindManager.setMoreAlarm(minute);
                    }
                }
            }

            int size = datas.size();
            Log.i("alarm", "闹铃size===" + size);
            if (datas != null && size > 0) {
                if (size > 1) {//多个闹铃
                    datas.remove(size - 1);
                    AlarmRemindManager.setAlarmDatas(datas);
                }
            }

            RemindInfo info = infos.get(infos.size() - 1);
            String remindMen = info.getRemindMen();
            String remindContent = info.getContent();
            String content = "";

            DataConfig.isAppPushRemind = false;

            if (!TextUtils.isEmpty(remindMen)) {
                //app提醒
                AlarmRemindManager.setOriginalAlarmTime(info.getOriginalAlarmTime());
                String requireAnswer = info.getRequireAnswer();
                if (!TextUtils.isEmpty(requireAnswer)) {
                    DataConfig.isAppPushRemind = true;
                    AlarmRemindManager.setRequireAnswer(requireAnswer);
                    AlarmRemindManager.setSpareType(info.getSpareType());
                    AlarmRemindManager.setSpareContent(info.getSpareContent());
                    AlarmRemindManager.setRemindMen(remindMen);

                    StringBuffer buffer = new StringBuffer(1024);
                    content = buffer.append(remindContent).append("：").append(remindMen).append(",请回答：").append(AlarmRemindManager.getRequireAnswer()).toString();

                } else {
                    content = remindContent;
                }
            } else {
                //闹铃
                content = "主人您好，您设置的" + remindContent + "提醒时间到了，不要忘记哦。";
            }

            SpeechImpl.getInstance().startSpeak(DataConfig.SPEAK_TYPE_REMIND_TIPS, content);

        }

    }

}
