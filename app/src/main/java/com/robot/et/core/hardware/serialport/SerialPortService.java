package com.robot.et.core.hardware.serialport;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

import com.robot.et.common.BroadcastAction;
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
		IntentFilter filter = new IntentFilter();
		filter.addAction(BroadcastAction.ACTION_MOVE_TO_SERIALPORT);
		registerReceiver(receiver, filter);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return super.onStartCommand(intent, flags, startId);
	}

	BroadcastReceiver receiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(BroadcastAction.ACTION_MOVE_TO_SERIALPORT)) {
				Log.i("SerialPort", "接收串口Filter");
				byte[] content = intent.getByteArrayExtra("actioncontent");
				instance.sendBuffer(content);
			}
		}
	};


	@Override
	public void onDataReceive(byte[] buffer, int size) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		unregisterReceiver(receiver);
	}
}
