package com.robot.et.core.software.iflytek;

import android.app.Service;
import android.content.Intent;
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
import com.robot.et.core.software.iflytek.event.SpeechRecognizeResultEvent;
import com.robot.et.core.software.iflytek.util.ResultParse;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

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
		EventBus.getDefault().register(this);
		Log.i("ifly", "科大讯飞语义理解执行onCreate()方法");
		mTextUnderstander = TextUnderstander.createTextUnderstander(this,textUnderstanderListener);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return super.onStartCommand(intent, flags, startId);
	}


	//开始文本理解
	@Subscribe(threadMode = ThreadMode.MAIN)
	private void startTextUnderstander(SpeechRecognizeResultEvent event) {
		if (mTextUnderstander.isUnderstanding()) {
			Log.i("ifly", "文本理取消");
			mTextUnderstander.cancel();
		}
		mTextUnderstander.understandText(event.result, textListener);
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
			Log.i("ifly", "文本理解onResult=======>"+result);
			String content=result.getResultString();
			Log.i("ifly","content=========>"+content);
//			EventBus.getDefault().post(new MessageEvent("Hello everyone!"));
		}

		@Override
		public void onError(SpeechError error) {
			// 文本语义不能使用回调错误码14002，请确认您下载sdk时是否勾选语义场景和私有语义的发布
			Log.i("ifly", "文本理解onError Code==" + error.getErrorCode());
		}
	};


	@Override
	public void onDestroy() {
		super.onDestroy();
		EventBus.getDefault().unregister(this);
		if (mTextUnderstander.isUnderstanding()) {
			mTextUnderstander.cancel();
		}
		mTextUnderstander.destroy();
	}
}
