package com.robot.et.core.hardware.serialport;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.robot.et.core.hardware.serialport.SerialPortUtil.OnDataReceiveListener;

public class SerialPortService extends Service implements OnDataReceiveListener {

	private static SerialPortUtil instance;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		Log.i("SerialPort", "串口实例化");
		instance = SerialPortUtil.getInstance();
		instance.setOnDataReceiveListener(this);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDataReceive(byte[] buffer, int size) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
	}
}
