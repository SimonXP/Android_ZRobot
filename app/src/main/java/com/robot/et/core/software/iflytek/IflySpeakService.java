package com.robot.et.core.software.iflytek;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SynthesizerListener;
import com.robot.et.common.DataConfig;

import org.greenrobot.eventbus.Subscribe;

public class IflySpeakService extends Service{
	// 语音合成对象
	private SpeechSynthesizer mTts;

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		Log.i("ifly", "科大讯飞语音合成执行onCreate()方法");
		// 初始化合成对象（第二个参数传值为null的时候，为在线合成）
		mTts = SpeechSynthesizer.createSynthesizer(this, mTtsInitListener);
		// 清空参数
		mTts.setParameter(SpeechConstant.PARAMS, null);
		// 根据合成引擎设置相应参数
		mTts.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD);
		// 设置合成发音人
		mTts.setParameter(SpeechConstant.VOICE_NAME, DataConfig.DEFAULT_SPEAK_MEN);
		// 设置合成语速
		mTts.setParameter(SpeechConstant.SPEED, "60");
		// 设置合成音调
		mTts.setParameter(SpeechConstant.PITCH, "50");
		// 设置合成音量
		mTts.setParameter(SpeechConstant.VOLUME, "50");
		// 设置播放器音频流类型
		mTts.setParameter(SpeechConstant.STREAM_TYPE, "3");
		// 设置播放合成音频打断音乐播放，默认为true
		mTts.setParameter(SpeechConstant.KEY_REQUEST_FOCUS, "true");
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return super.onStartCommand(intent, flags, startId);
	}
	//说话
	@Subscribe
	private void speakContent (String content) {
		 mTts.startSpeaking(content, mTtsListener);
	}
	
	// 初始化监听
	private InitListener mTtsInitListener = new InitListener() {
		@Override
		public void onInit(int code) {
			if (code != ErrorCode.SUCCESS) {
				Log.e("ifly","语音合成初始化失败");
				// 初始化失败,错误码
			} else {
				// 初始化成功，之后可以调用startSpeaking方法
				// 注：有的开发者在onCreate方法中创建完合成对象之后马上就调用startSpeaking进行合成，
				// 正确的做法是将onCreate中的startSpeaking调用移至这里
			}
		}
	};
	
	// 合成回调监听
	private SynthesizerListener mTtsListener = new SynthesizerListener() {
		// 开始播放
		@Override
		public void onSpeakBegin() {
			Log.i("ifly", "IflySpeakService  onSpeakBegin()");
		}
		// 暂停播放
		@Override
		public void onSpeakPaused() {
		}
		// 继续播放
		@Override
		public void onSpeakResumed() {
		}
		// 合成进度
		@Override
		public void onBufferProgress(int percent, int beginPos, int endPos, String info) {
		}
		// 播放进度
		@Override
		public void onSpeakProgress(int percent, int beginPos, int endPos) {

		}
		@Override
		public void onCompleted(SpeechError error) {
			if (error == null) {
				Log.i("ifly", "语音合成完成");
			} else {
				Log.e("ifly", "onCompleted  error=" + error.getPlainDescription(true));
			}
		}

		@Override
		public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {
			// 以下代码用于获取与云端的会话id，当业务出错时将会话id提供给技术支持人员，可用于查询会话日志，定位出错原因
			// 若使用本地能力，会话id为null
			// if (SpeechEvent.EVENT_SESSION_ID == eventType) {
			// String sid = obj.getString(SpeechEvent.KEY_EVENT_SESSION_ID);
			// Log.d(TAG, "session id =" + sid);
			// }
		}
	};

	private void stopSpeak () {
		if(mTts.isSpeaking()){
			mTts.stopSpeaking();
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		stopSpeak();
		// 退出时释放连接
		mTts.destroy();
	}
}
