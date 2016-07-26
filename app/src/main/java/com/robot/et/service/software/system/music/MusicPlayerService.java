package com.robot.et.service.software.system.music;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;

import com.robot.et.common.BroadcastAction;
import com.robot.et.common.DataConfig;

import java.io.IOException;

public class MusicPlayerService extends Service {

    // 媒体播放器对象
    private MediaPlayer mediaPlayer;
    private Intent intent;

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i("music", "MusicPlayerService onCreate()");
        mediaPlayer = new MediaPlayer();
        intent = new Intent();

        //设置音乐播放完成时的监听器
        mediaPlayer.setOnCompletionListener(new OnCompletionListener() {

            @Override
            public void onCompletion(MediaPlayer arg0) {
                Log.i("music", "音乐播放完成");
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

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    private void musicSrcNotExit() {
        intent.setAction(BroadcastAction.ACTION_START_SPEAK);
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
            mediaPlayer.start(); // 开始播放
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            stopPlay();
            mediaPlayer.release();
            mediaPlayer = null;
        }
        unregisterReceiver(receiver);
    }

}
