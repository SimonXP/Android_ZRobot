package com.robot.et.service.software.iflytek;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.TextUnderstander;
import com.iflytek.cloud.TextUnderstanderListener;
import com.iflytek.cloud.UnderstanderResult;
import com.robot.et.common.BroadcastAction;
import com.robot.et.service.software.iflytek.util.ResultParse;

//科大讯飞文本语义理解
public class IflyTextUnderstanderService extends Service{

	private TextUnderstander mTextUnderstander;

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		Log.i("ifly", "科大讯飞语义理解执行onCreate()方法");
		mTextUnderstander = TextUnderstander.createTextUnderstander(this,textUnderstanderListener);
		//
		IntentFilter filter = new IntentFilter();
		filter.addAction(BroadcastAction.ACTION_START_LISTEN);
		filter.addAction(BroadcastAction.ACTION_STOP_LISTEN);
		registerReceiver(receiver, filter);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return super.onStartCommand(intent, flags, startId);
	}

	BroadcastReceiver receiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(BroadcastAction.ACTION_START_UNDERSTAND)) {//开始听
				String content=intent.getStringExtra("result");
				startTextUnderstander(content);
			} else if (intent.getAction().equals(BroadcastAction.ACTION_STOP_UNDERSTAND)) {//停止听
				stopTextUnderstander();
			}
		}
	};


	//开始文本理解
	private void startTextUnderstander(String content) {
		if (mTextUnderstander.isUnderstanding()) {
			Log.i("ifly", "文本理取消");
			mTextUnderstander.cancel();
		}
		mTextUnderstander.understandText(content, textListener);
	}
	//取消文本理解
	private void stopTextUnderstander(){
		mTextUnderstander.cancel();
	}

	private InitListener textUnderstanderListener = new InitListener() {

		@Override
		public void onInit(int code) {
			if (code != ErrorCode.SUCCESS) {
				Log.i("ifly", "文本理解初始化失败,错误码code==" + code);
			}
		}
	};

	private TextUnderstanderListener textListener = new TextUnderstanderListener() {

		@Override
		public void onResult(UnderstanderResult result) {
			Log.i("ifly", "文本理解onResult");
			Message message = handler.obtainMessage();
			message.obj = result;
			handler.sendMessage(message);
		}

		@Override
		public void onError(SpeechError error) {
			// 文本语义不能使用回调错误码14002，请确认您下载sdk时是否勾选语义场景和私有语义的发布
			Log.i("ifly", "文本理解onError Code==" + error.getErrorCode());
		}
	};

	Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			UnderstanderResult result = (UnderstanderResult) msg.obj;
			Log.i("ifly", "文本理解onResult  result===" + result);
			if (null != result) {
				String text = result.getResultString();
				Log.i("ifly", "文本理解text===" + text);
				if (!TextUtils.isEmpty(text)) {
					ResultParse.printResult()
//					notifyStartSpeak(text);
				} else {
					//请求图灵
				}
			} else {
				//请求图灵
				Log.i("ifly", "文本理解不正确");
			}
		};
	};

	private void notifyStartSpeak(String content){
		Intent intent=new Intent();
		intent.setAction(BroadcastAction.ACTION_START_SPEAK);
		intent.putExtra("result",content);
		sendBroadcast(intent);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (mTextUnderstander.isUnderstanding()) {
			mTextUnderstander.cancel();
		}
		mTextUnderstander.destroy();
		unregisterReceiver(receiver);
	}
}
