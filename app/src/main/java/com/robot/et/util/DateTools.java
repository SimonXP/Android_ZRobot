package com.robot.et.util;

import android.annotation.SuppressLint;
import android.text.TextUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by houdeming on 2016/8/1.
 */
public class DateTools {

    // 得到当前的小时
    @SuppressLint("SimpleDateFormat")
    public static int getCurrentHour(long currentMinute) {
        int tempHour = 0;
        Date date = new Date(currentMinute);
        SimpleDateFormat format = new SimpleDateFormat("HH");
        String dateTime = format.format(date);
        if (!TextUtils.isEmpty(dateTime)) {
            tempHour = Integer.parseInt(dateTime);
        }
        return tempHour;
    }

    // 得到当前的日期yyyy-MM-dd
    @SuppressLint("SimpleDateFormat")
    public static String getCurrentDate(long currentMinute) {
        Date date = new Date(currentMinute);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String dateTime = format.format(date);
        return dateTime;
    }

    // 得到当前的时间 HH:mm:ss
    @SuppressLint("SimpleDateFormat")
    public static String getCurrentTime(long currentMinute) {
        Date date = new Date(currentMinute);
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
        String time = format.format(date);
        return time;
    }

    // 获取long性的时间 HH:mm
    @SuppressLint("SimpleDateFormat")
    public static long getlongTime(String dateString) {
        long temp = 0;
        try {
            Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(dateString);
            temp = date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return temp;
    }

    // 得到当前的详细时间 yyyy-MM-dd HH:mm:ss
    @SuppressLint("SimpleDateFormat")
    public static String getCurrenTimeDetail(long currentMinute) {
        Date date = new Date(currentMinute);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateTime = format.format(date);
        return dateTime;
    }

    // 得到当前的分钟 currentMinute====6 当10分钟之内时，系统默认读的是一位数字
    @SuppressLint("SimpleDateFormat")
    public static int getCurrentMinute(long currentMinute) {
        int tempMinute = 0;
        Date date = new Date(currentMinute);
        SimpleDateFormat format = new SimpleDateFormat("mm");
        String hourTime = format.format(date);
        if (!TextUtils.isEmpty(hourTime)) {
            tempMinute = Integer.parseInt(hourTime);
        }
        return tempMinute;
    }

    // 根据日期、时间获取Calendar   data:2016-05-08 time:09:05:00
    public static Calendar getCalendar(String data, String time) {
        Calendar calendar = Calendar.getInstance();
        String datas[] = getDatas(data);
        String times[] = getTimes(time);

        if (datas != null && datas.length > 0) {
            // 当前年
            calendar.set(Calendar.YEAR, Integer.parseInt(datas[0]));
            // 当前月，从0开始 【0-11】
            calendar.set(Calendar.MONTH, Integer.parseInt(datas[1]) - 1);
            // 当前日
            calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(datas[2]));
        }

        if (times != null && times.length > 0) {
            // 当前小时
            calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(times[0]));
            // 当前分钟
            calendar.set(Calendar.MINUTE, Integer.parseInt(times[1]));
            // 当前秒
            calendar.set(Calendar.SECOND, 0);
        }
        return calendar;
    }

    // 得到日期的数组 data:2016-05-08
    private static String[] getDatas(String data) {
        String[] datas = null;
        if (!TextUtils.isEmpty(data)) {
            datas = data.split("-");
            if (datas != null && datas.length > 0) {
                if (datas[1].startsWith("0")) {// 月
                    datas[1] = datas[1].substring(1, datas[1].length());
                }

                if (datas[2].startsWith("0")) {// 日
                    datas[2] = datas[2].substring(1, datas[2].length());
                }
            }
        }
        return datas;
    }

    // 得到时间的数组 time:09:05:00
    private static String[] getTimes(String time) {
        String[] times = null;
        if (!TextUtils.isEmpty(time)) {
            times = time.split("\\:");
            if (time != null && times.length > 0) {
                if (times[0].startsWith("0")) {// 时
                    times[0] = times[0].substring(1, times[0].length());
                }

                if (times[1].startsWith("0")) {// 分
                    times[1] = times[1].substring(1, times[1].length());
                }
            }
        }
        return times;
    }

    // 获取2位数的分钟   当10分钟之内时，系统默认读的是一位数字
    public static String get2DigitMinute(long minute) {
        String minuteTwo = "";
        // currentMinute====6 当10分钟之内时，系统默认读的是一位数字
        int currentMinute = getCurrentMinute(minute);
        String tempMinute = String.valueOf(currentMinute);
        if (tempMinute.length() == 1) {
            minuteTwo = "0" + tempMinute;
        } else {
            minuteTwo = tempMinute;
        }
        return minuteTwo;
    }

}
