package com.robot.et.core.hardware.wakeup;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.robot.et.util.ShellUtils;
import com.robot.et.util.ShellUtils.CommandResult;

import java.util.ArrayList;
import java.util.List;

public class WakeUpServices extends Service {

	private int fd;
	private int wakeUpState;
	private int degree;
	private GetDegreeThread mGetDegreeThread;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		//打开I2C，一般的手机上面会出现错误
		openI2C();
		fd = WakeUp.open("", 0);
		Log.i("wakeup", "fd:" + fd);

		mGetDegreeThread = new GetDegreeThread();
		mGetDegreeThread.start();
	}
	
	private void openI2C() {
		List<String> commnandList = new ArrayList<String>();
		commnandList.add("chmod 777 /sys/class/gpio");
		commnandList.add("chmod 777 /sys/class/gpio/export");
		commnandList.add("echo 68 > /sys/class/gpio/export");
		commnandList.add("chmod 777 /sys/class/gpio/gpio68");
		commnandList.add("chmod 777 /sys/class/gpio/gpio68/direction");
		commnandList.add("chmod 777 /sys/class/gpio/gpio68/edge");
		commnandList.add("chmod 777 /sys/class/gpio/gpio68/value");
		commnandList.add("setenforce 0");
		CommandResult result = ShellUtils.execCommand(commnandList, true);
	}
	

	private class GetDegreeThread extends Thread {

		@Override
		public void run() {
			while (true) {
				if (fd > 0) {
					wakeUpState = WakeUp.getWakeUpState(fd);
//					Log.i("nowakeup", "wakeUpState:" + wakeUpState);
					if (wakeUpState == 1) {
						degree = WakeUp.getWakeUpDegree();
						Log.i("wakeup", "degree:" + degree);
						WakeUp.setGainDirection(0);// 设置麦克0为主麦

					} else {
						// Log.i("wakeup", "no wakeUp");
					}
				} else {
					// Log.i("wakeup", "未打开I2C");
				}
			}
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		// 此处不要忘记关闭线程，暂时放在这里.2016-06-08
	}
}
