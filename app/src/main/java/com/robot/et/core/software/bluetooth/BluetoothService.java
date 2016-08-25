package com.robot.et.core.software.bluetooth;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.robot.et.common.BroadcastAction;

/**
 * Created by houdeming on 2016/8/25.
 */
public class BluetoothService extends Service {
    //robot2    20:16:06:20:65:84
    //autorobot3    98:D3:31:B0:C6:48
    private final String BLUE_ADDRESS = "98:D3:31:B0:C6:48";
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothChatService mChatService;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i("bluth", "BluetoothService  onCreate()");
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter != null) {
            if (!mBluetoothAdapter.isEnabled()) {
                mBluetoothAdapter.enable();
            }
            mChatService = new BluetoothChatService(this, mHandler);
            if (mChatService.getState() == BluetoothChatService.STATE_NONE) {
                mChatService.start();
            }
            connectBluth();
        }

        IntentFilter filter = new IntentFilter();
        filter.addAction(BroadcastAction.ACTION_MOVE_TO_BLUTH);
        registerReceiver(receiver, filter);
    }

    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(BroadcastAction.ACTION_MOVE_TO_BLUTH)) {
                Log.i("bluth", "发送蓝牙数据");
                byte[] content = intent.getByteArrayExtra("actioncontent");
                if (content != null && content.length > 0) {
                    if (mChatService != null) {
                        mChatService.write(content);
                    }
                }
            }
        }
    };

    //缓存数据
    private static StringBuffer buffer = new StringBuffer(1024);

    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case BluetoothConfig.MESSAGE_STATE_CHANGE:// 吐司
                    Log.i("bluth", "MESSAGE_STATE_CHANGE: " + msg.arg1);
                    switch (msg.arg1) {
                        case BluetoothChatService.STATE_CONNECTED:// 连接蓝牙
                            Log.i("bluth", "STATE_CONNECTED");
                            break;
                        case BluetoothChatService.STATE_CONNECTING:// 正在连接
                            Log.i("bluth", "STATE_CONNECTING");
                            break;
                        case BluetoothChatService.STATE_LISTEN:// 蓝牙列表
                        case BluetoothChatService.STATE_NONE:// 没有蓝牙数据
                            Log.i("bluth", "STATE_NONE");
                            break;
                    }
                    break;
                case BluetoothConfig.MESSAGE_WRITE:// 写数据
                    byte[] writeBuf = (byte[]) msg.obj;
                    // construct a string from the buffer
                    String writeMessage = new String(writeBuf);
                    Log.i("bluth", "MESSAGE_WRITE writeMessage===" + writeMessage);
                    break;
                case BluetoothConfig.MESSAGE_READ:// 读数据
                    byte[] readBuf = (byte[]) msg.obj;
                    // construct a string from the valid bytes in the buffer
                    String readMessage = new String(readBuf, 0, msg.arg1);

                    buffer.append(readMessage);
                    String buf = buffer.toString();
                    Log.i("bluth", "MESSAGE_READ buf.length()===" + buf.length());
                    Log.i("bluth", "MESSAGE_READ buf===" + buf);

                    handString(buf);

                    break;
                case BluetoothConfig.MESSAGE_DEVICE_NAME:// 设备的名字
                    // save the connected device's name
                    String mConnectedDeviceName = msg.getData().getString(BluetoothConfig.DEVICE_NAME);
                    Log.i("bluth", "MESSAGE_DEVICE_NAME mConnectedDeviceName===" + mConnectedDeviceName);

                    break;
                case BluetoothConfig.MESSAGE_TOAST:// 蓝牙断开要重新连接
                    Log.i("bluth", "蓝牙断开要重新连接===" + msg.getData().getString(BluetoothConfig.TOAST));
                    connectBluth();
                    break;
            }
        }
    };

    //处理发送来的字符串，可能丢帧
    private void handString(String str) {
        String begin = "{";
        String end = "}";
        if (!TextUtils.isEmpty(str)) {
            if (str.contains(begin) && str.contains(end)) {
                int start = str.indexOf(begin);
                int stop = str.lastIndexOf(end);
                if (stop > start) {
                    String result = str.substring(start, stop + 1);
                    Log.i("bluth", "MESSAGE_READ result===" + result);
                    if (!TextUtils.isEmpty(str)) {
                        buffer.delete(start, stop + 1);
                        Log.i("bluth", "MESSAGE_READ start===" + start);
                        if (start != 0) {
                            buffer.delete(0, start);
                        }
                    }
                }
            }
        }
    }

    //连接蓝牙
    private void connectBluth() {
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(BLUE_ADDRESS);
        mChatService.connect(device, true);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mChatService != null) {
            mChatService.stop();
        }
        unregisterReceiver(receiver);
    }

}
