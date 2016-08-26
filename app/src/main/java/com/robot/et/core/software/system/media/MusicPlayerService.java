package com.robot.et.core.software.system.media;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.audiofx.Visualizer;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;

import com.robot.et.R;
import com.robot.et.common.BroadcastAction;
import com.robot.et.common.DataConfig;
import com.robot.et.core.software.common.network.HttpManager;
import com.robot.et.core.software.common.push.netty.NettyClientHandler;
import com.robot.et.core.software.common.script.ScriptHandler;
import com.robot.et.core.software.common.view.EmotionManager;
import com.robot.et.core.software.common.view.SpectrumManager;
import com.robot.et.core.software.common.view.ViewCommon;
import com.robot.et.util.MusicManager;

import java.io.IOException;

public class MusicPlayerService extends Service {

    // 媒体播放器对象
    private MediaPlayer mediaPlayer;
    private Intent intent;
    private Visualizer mVisualizer;//频谱器

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i("music", "MusicPlayerService onCreate()");
        mediaPlayer = new MediaPlayer();
        //实例化Visualizer，参数SessionId可以通过MediaPlayer的对象获得
        mVisualizer = new Visualizer(mediaPlayer.getAudioSessionId());
        //采样 - 参数内必须是2的位数 - 如64,128,256,512,1024
        mVisualizer.setCaptureSize(Visualizer.getCaptureSizeRange()[1]);
        SpectrumManager.setVisualizer(mVisualizer);

        intent = new Intent();

        //设置音乐播放完成时的监听器
        mediaPlayer.setOnCompletionListener(new OnCompletionListener() {

            @Override
            public void onCompletion(MediaPlayer arg0) {
                Log.i("music", "音乐播放完成");
                DataConfig.isPlayMusic = false;
                //播放的是APP推送来的歌曲，继续播放下一首
                if (DataConfig.isJpushPlayMusic) {
                    playAppLower();
                    return;
                }

                SpectrumManager.hideSpectrum();
                ViewCommon.initView();
                EmotionManager.showEmotion(R.mipmap.emotion_normal);

                intent.setAction(BroadcastAction.ACTION_PLAY_MUSIC_END);
                sendBroadcast(intent);
            }
        });

        IntentFilter filter = new IntentFilter();
        filter.addAction(BroadcastAction.ACTION_PLAY_MUSIC_START);
        filter.addAction(BroadcastAction.ACTION_STOP_MUSIC);
        registerReceiver(receiver, filter);

    }

    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(BroadcastAction.ACTION_PLAY_MUSIC_START)) {//音乐开始播放
                Log.i("music", "onReceive   音乐开始播放");
                String musicUrl = intent.getStringExtra("musicUrl");
                play(musicUrl);
            } else if (intent.getAction().equals(BroadcastAction.ACTION_STOP_MUSIC)) {//停止音乐播放
                stopPlay();
            }
        }
    };

    //播放APP推送来的下一首
    private void playAppLower() {
        String musicSrc = MusicManager.getLowerMusicSrc(MusicManager.getCurrentMediaType(), MusicManager.getCurrentPlayName() + ".mp3");
        Log.i("music", "MusicPlayerService musicSrc ===" + musicSrc);
        MusicManager.setCurrentPlayName(MusicManager.getMusicNameNoMp3(musicSrc));
        HttpManager.pushMediaState(MusicManager.getCurrentMediaName(), "open", MusicManager.getCurrentPlayName(), new NettyClientHandler(this));
        play(musicSrc);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    private void musicSrcNotExit() {
        intent.setAction(BroadcastAction.ACTION_SPEAK);
        intent.putExtra("type", DataConfig.SPEAK_TYPE_CHAT);
        intent.putExtra("content", DataConfig.MUSIC_NOT_EXIT);
        sendBroadcast(intent);
    }

    //开始播放
    private void play(String musicSrc) {
        if (!TextUtils.isEmpty(musicSrc)) {
            try {
                mediaPlayer.reset();// 把各项参数恢复到初始状态
                mediaPlayer.setDataSource(musicSrc);
                // 进行缓冲
                mediaPlayer.prepare();
                mediaPlayer.setOnPreparedListener(new PreparedListener());
            } catch (IllegalStateException e) {
                //报异常提示文件不存在
                musicSrcNotExit();
            } catch (IOException e) {
                //报异常提示文件不存在
                musicSrcNotExit();
            }

        } else {
            //提示文件不存在
            musicSrcNotExit();
        }
    }

    //停止播放
    private void stopPlay() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }
    }

    //实现一个OnPrepareLister接口,当音乐准备好的时候开始播放
    private final class PreparedListener implements OnPreparedListener {
        @Override
        public void onPrepared(MediaPlayer mp) {
            Log.i("music", "音乐开始播放");
            DataConfig.isPlayMusic = true;
            ViewCommon.initView();
            SpectrumManager.showSpectrum();

            mediaPlayer.start(); // 开始播放

            if (DataConfig.isJpushPlayMusic) {//来自app音乐
                new ScriptHandler().scriptPlayMusic(MusicPlayerService.this, true);
            }

        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mVisualizer.release();
        if (mediaPlayer != null) {
            stopPlay();
            mediaPlayer.release();
            mediaPlayer = null;
        }
        unregisterReceiver(receiver);
    }

}
