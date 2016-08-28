package com.robot.et.core.software.video.agora;

import android.app.Activity;

import io.agora.rtc.IRtcEngineEventHandler;
import io.agora.rtc.IRtcEngineEventHandler.RtcStats;

public class BaseEngineEventHandlerActivity extends Activity {

	// 加入频道回调
	public void onJoinChannelSuccess(String channel, int uid, int elapsed) {
	}

	// 重新加入频道回调
	public void onRejoinChannelSuccess(String channel, int uid, int elapsed) {
	}

	// 发生错误回调
	public void onError(int err) {
	}

	// 摄像头启用回调
	public void onCameraReady() {
	}

	// 声音质量回调
	public void onAudioQuality(int uid, int quality, short delay, short lost) {
	}

	public void onAudioTransportQuality(int uid, short delay, short lost) {
	}

	public void onVideoTransportQuality(int uid, short delay, short lost) {
	}

	// 离开频道回调
	public void onLeaveChannel(IRtcEngineEventHandler.RtcStats stats) {
	}

	//更新码率
	public void onUpdateSessionStats(IRtcEngineEventHandler.RtcStats stats) {
	}

	public void onRecap(byte[] recap) {
	}

	// 说话声音音量提示回调
	public void onAudioVolumeIndication(IRtcEngineEventHandler.AudioVolumeInfo[] speakers, int totalVolume) {
	}

	// 网络质量报告回调
	public void onNetworkQuality(int quality) {
	}

	// 其他用户加入当前频道回调
	public void onUserJoined(int uid, int elapsed) {
	}

	// 其他用户离开当前频道回调
	public void onUserOffline(int uid) {
	}

	// 用户静音回调
	public void onUserMuteAudio(int uid, boolean muted) {
	}

	// 用户停止/重启视频回调
	public void onUserMuteVideo(int uid, boolean muted) {
	}

	public void onAudioRecorderException(int nLastTimeStamp) {
	}

	// 远端视频统计回调
	public void onRemoteVideoStat(int uid, int frameCount, int delay,int receivedBytes) {
	}

	// 本地视频统计回调
	public void onLocalVideoStat(int sentBytes, int sentFrames) {
	}

	// 远端视频显示回调
	public void onFirstRemoteVideoFrame(int uid, int width, int height,int elapsed) {
	}

	// 本地视频显示回调
	public void onFirstLocalVideoFrame(int width, int height, int elapsed) {
	}

	// 远端视频接收解码回调
	public void onFirstRemoteVideoDecoded(int uid, int width, int height,int elapsed) {
	}

	// 连接丢失回调
	public void onConnectionLost() {
	}

	// 连接中断回调
	public void onConnectionInterrupted() {
	}

	public void onMediaEngineEvent(int code) {
	}

	// Rtc Engine统计数据回调
	public void onRtcStats(RtcStats stats) {
	}

}
