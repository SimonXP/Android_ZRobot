package com.robot.et.core.software.system.alarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.robot.et.core.software.common.receiver.AlarmRemindReceiver;
import com.robot.et.main.CustomApplication;

import java.util.Calendar;

//聊天监听器  设置与取消的对象必须要保持一致，否则无法取消
public class AlarmClock {
    public static AlarmClock instance = null;
    private final Context context;
    private PendingIntent pendingIntent;
    private AlarmManager am;

    private AlarmClock() {
        context = CustomApplication.getInstance().getApplicationContext();
    }

    public static AlarmClock getInstance() {
        if (instance == null) {
            synchronized (AlarmClock.class) {
                if (instance == null) {
                    instance = new AlarmClock();
                }
            }
        }
        return instance;
    }

    private void getPendIntent(String action) {
        Intent intent = new Intent();
        intent.setAction(action);
        intent.addFlags(Intent.FLAG_RECEIVER_FOREGROUND);
        pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
        am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
    }

    //开启监听
    public void startTimer(String action) {
        getPendIntent(action);
        am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 2 * 1000, pendingIntent);
    }

    //取消监听
    public void cancelTimer(String action) {
        getPendIntent(action);
        am.cancel(pendingIntent);
    }

    //设置一次性的闹钟
    public void setOneAlarm(String action, Calendar calendar) {
        getOneAlarmPendIntent(action);
        am.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
    }

    //取消一次性的闹钟
    public void cancelOneAlarm(String action) {
        getOneAlarmPendIntent(action);
        am.cancel(pendingIntent);
    }

    private void getOneAlarmPendIntent(String action) {
        Intent intent = new Intent(context, AlarmRemindReceiver.class);
        intent.setAction(action);
        pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
        am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
    }

}
