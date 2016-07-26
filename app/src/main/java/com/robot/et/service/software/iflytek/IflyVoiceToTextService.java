package com.robot.et.service.software.iflytek;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.LexiconListener;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.robot.et.common.BroadcastAction;
import com.robot.et.common.DataConfig;
import com.robot.et.service.software.iflytek.util.ResultParse;
import com.robot.et.util.FileUtils;
import com.robot.et.util.Utilities;

import java.util.HashMap;
import java.util.LinkedHashMap;

public class IflyVoiceToTextService extends Service{
	// 语音听写对象
	private SpeechRecognizer mIat;
	// 用HashMap存储听写结果
	private HashMap<String, String> mIatResults = new LinkedHashMap<String, String>();

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		Log.i("ifly", "科大讯飞语音听写执行onCreate()方法");
		// 初始化SpeechRecognizer对象
		mIat = SpeechRecognizer.createRecognizer(this, mTtsInitListener);
		uploadUserThesaurus();//上传词表
		setParam();
		beginListen();
		//注册开始听和停止听的过滤器
		IntentFilter filter = new IntentFilter();
		filter.addAction(BroadcastAction.ACTION_START_LISTEN);
		filter.addAction(BroadcastAction.ACTION_STOP_LISTEN);
		registerReceiver(receiver, filter);
	}

	BroadcastReceiver receiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(BroadcastAction.ACTION_START_LISTEN)) {//开始听
				beginListen();
			} else if (intent.getAction().equals(BroadcastAction.ACTION_STOP_LISTEN)) {//停止听
				stopListen();
			}
		}
	};

	//参数设置
	public void setParam(){
		String language=DataConfig.DEFAULT_SPEAK_MEN;
		//设置参数
		mIat.setParameter(SpeechConstant.PARAMS, null);
		// 设置听写引擎
		mIat.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD);
		// 设置返回结果格式
		mIat.setParameter(SpeechConstant.RESULT_TYPE, "json");
		if (language.equals("en_us")) {
			// 设置语言
			mIat.setParameter(SpeechConstant.LANGUAGE, "en_us");
		} else {
			// 设置语言
			mIat.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
			// 设置语言区域
			mIat.setParameter(SpeechConstant.ACCENT, language);
		}
		// 设置语音前端点:静音超时时间，即用户多长时间不说话则当做超时处理
		mIat.setParameter(SpeechConstant.VAD_BOS, "4000");

		// 设置语音后端点:后端点静音检测时间，即用户停止说话多长时间内即认为不再输入， 自动停止录音
		mIat.setParameter(SpeechConstant.VAD_EOS, "1000");

		// 设置标点符号,设置为"0"返回结果无标点,设置为"1"返回结果有标点
		mIat.setParameter(SpeechConstant.ASR_PTT, "0");
	}

	//开始听
	private void beginListen() {
		// 清空数据
		mIatResults.clear();
		mIat.startListening(mRecognizerListener);
	}

	//初始化监听器
	private InitListener mTtsInitListener = new InitListener() {
		@Override
		public void onInit(int code) {
			if (code != ErrorCode.SUCCESS) {
				//初始化失败,错误码
				Log.e("ifly", "初始化监听器失败" );
			} else {
				// 初始化成功，之后可以调用startSpeaking方法
				// 注：有的开发者在onCreate方法中创建完合成对象之后马上就调用startSpeaking进行合成，
				// 正确的做法是将onCreate中的startSpeaking调用移至这里
			}
		}
	};
	
	 //听写监听器
	private RecognizerListener mRecognizerListener = new RecognizerListener() {

		@Override
		public void onBeginOfSpeech() {
			// 此回调表示：sdk内部录音机已经准备好了，用户可以开始语音输入
			Log.i("ifly", "onBeginOfSpeech()" );
		}

		@Override
		public void onError(SpeechError error) {
			Log.e("ifly", "onError:"+error.getPlainDescription(true));
			// 错误码：10118(您没有说话)，可能是录音机权限被禁，需要提示用户打开应用的录音权限。
			// 如果使用本地功能（语记）需要提示用户开启语记的录音权限。
			stopListen();
			beginListen();
		}

		@Override
		public void onEndOfSpeech() {
			// 此回调表示：检测到了语音的尾端点，已经进入识别过程，不再接受语音输入
			Log.i("ifly", "结束说话 " );
		}

		@Override
		public void onResult(RecognizerResult results, boolean isLast) {
			String result = ResultParse.printResult(results,mIatResults);
			if(isLast){
				Log.i("ifly", "内容：" + result);
				Utilities.showToast("内容：" + result,IflyVoiceToTextService.this);
				stopListen();
				if (!TextUtils.isEmpty(result)) {
					if (result.length() == 1) {
						return;
					}else {
						notifyStartSpeak(result);
					}
				} else {
					beginListen();
				}
			}
		}

		@Override
		public void onVolumeChanged(int volume, byte[] data) {
			Log.i("ifly", "当前正在说话，音量大小： volume==" + volume );
		}

		@Override
		public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {
			Log.i("ifly", "onEvent" );
			// 以下代码用于获取与云端的会话id，当业务出错时将会话id提供给技术支持人员，可用于查询会话日志，定位出错原因
			// 若使用本地能力，会话id为null
			// if (SpeechEvent.EVENT_SESSION_ID == eventType) {
			// String sid = obj.getString(SpeechEvent.KEY_EVENT_SESSION_ID);
			// Log.d(TAG, "session id =" + sid);
			// }
		}
	};
	
	//上传词表监听器
	private LexiconListener mLexiconListener = new LexiconListener() {

		@Override
		public void onLexiconUpdated(String lexiconId, SpeechError error) {
			if (error != null) {
				Log.i("ifly", "上传用户词表失败（onLexiconUpdated）：" + error.toString() );
			} else {
				Log.i("ifly", "上传用户词表成功" );
			}
		}
	};
	
	//上传词表
	private void uploadUserThesaurus(){
		String contents = FileUtils.readFile(this,"userwords","utf-8");
		// 指定引擎类型
		mIat.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD);
		// 置编码类型
		mIat.setParameter(SpeechConstant.TEXT_ENCODING, "utf-8");
		mIat.updateLexicon("userword", contents, mLexiconListener);
	}

	//通知Robot理解文字内容
	private void notifyStartSpeak(String result){
		Intent intent=new Intent();
		intent.setAction(BroadcastAction.ACTION_START_UNDERSTAND);
		intent.putExtra("result",result);
		sendBroadcast(intent);
	}

	//关闭听
	private void stopListen () {
		if(mIat.isListening()){
			mIat.cancel();
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		stopListen();
		mIat.destroy();
		unregisterReceiver(receiver);
	}
}
