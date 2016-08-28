package com.robot.et.util;

import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import com.robot.et.common.DataConfig;
import com.robot.et.common.RequestConfig;

import java.io.File;

/**
 * Created by houdeming on 2016/7/25.
 */
public class MusicManager {
    private final static String TAG = "netty";

    // 获取播放音频的路径
    public static String getDetailMusicSrc(int fileType, String musicName) {
        String fileSrc = getMusicFile(fileType);
        fileSrc += File.separator + musicName + ".mp3";
        return fileSrc;
    }

    // 获取下一首mp3文件的路径
    public static String getLowerMusicSrc(int fileType, String currentMusicName) {
        String fileSrc = getMusicFile(fileType);
        Log.i(TAG, "playcontrol  fileSrc===" + fileSrc);
        Log.i(TAG, "playcontrol  currentMusicName===" + currentMusicName);
        File file = new File(fileSrc);
        String[] names = file.list();
        if (names != null && names.length > 0) {
            for (int i = 0; i < names.length; i++) {
                String name = names[i];
                if (TextUtils.equals(name, currentMusicName)) {
                    if (i == names.length - 1) {
                        return fileSrc += File.separator + names[0];
                    }
                    return fileSrc += File.separator + names[i + 1];
                }
            }
        }

        return null;
    }

    //获取歌曲路径文件
    private static String getMusicFile(int fileType) {
        String fileSrc = Environment.getExternalStorageDirectory()
                .getAbsolutePath() + File.separator + "robot" + File.separator;

        switch (fileType) {
            case RequestConfig.JPUSH_MUSIC:
                fileSrc += "音乐";
                break;
            case RequestConfig.JPUSH_STORY:
                fileSrc += "故事";
                break;
            case RequestConfig.JPUSH_SYNCHRONOUS_CLASSROOM:
                fileSrc += "同步课堂";
                break;
            case RequestConfig.JPUSH_THOUSANDS_WHY:
                fileSrc += "十万个为什么";
                break;
            case RequestConfig.JPUSH_ENCYCLOPEDIAS:
                fileSrc += "百科";
                break;

            default:
                break;
        }

        return fileSrc;
    }

    //获取音乐的名字不带.mp3
    public static String getMusicNameNoMp3(String content) {
        if (!TextUtils.isEmpty(content)) {
            int start = content.lastIndexOf("/");
            int end = content.indexOf(".mp3");
            return content.substring(start + 1, end);
        }
        return "";
    }

    // 获取要播放音乐的话语
    public static String getMusicSpeakContent(int srcType, int mediaType, String result) {
        String content = "";
        String playPrompt = "开始播放";
        if (srcType == DataConfig.MUSIC_SRC_FROM_OTHER) {//第三方
            if (!TextUtils.isEmpty(result)) {
                if (result.contains(DataConfig.MUSIC_SPLITE)) {
                    String[] datas = result.split(DataConfig.MUSIC_SPLITE);
                    if (datas != null && datas.length > 0) {
                        //歌手+歌名 + 歌曲src
                        setMusicSrc(datas[2]);
                        content = "好的，" + playPrompt + datas[0] + "：" + datas[1];
                    }
                }
            }
        } else {//app推送
            String musicSrc = getDetailMusicSrc(mediaType, result);
            Log.i(TAG, "musicSrc===" + musicSrc);
            setMusicSrc(musicSrc);

            switch (mediaType) {
                case RequestConfig.JPUSH_MUSIC:
                    content = playPrompt + result;
                    break;
                case RequestConfig.JPUSH_STORY:
                    content = playPrompt + result;
                    break;
                case RequestConfig.JPUSH_SYNCHRONOUS_CLASSROOM:
                    content = playPrompt + result;
                    break;
                case RequestConfig.JPUSH_THOUSANDS_WHY:
                    content = playPrompt + result;
                    break;
                case RequestConfig.JPUSH_ENCYCLOPEDIAS:
                    content = playPrompt + result;
                    break;

                default:
                    break;
            }
        }
        return content;
    }

    //当前播放的媒体类型
    private static int currentMediaType;
    //当前播放的媒体名字
    private static String currentMediaName;
    //当前播放的歌名
    private static String currentPlayName;
    //音乐src
    private static String musicSrc;

    public static String getMusicSrc() {
        return musicSrc;
    }

    public static void setMusicSrc(String musicSrc) {
        MusicManager.musicSrc = musicSrc;
    }

    public static void setCurrentMediaType(int mediaType) {
        currentMediaType = mediaType;
    }

    public static int getCurrentMediaType() {
        return currentMediaType;
    }

    public static void setCurrentPlayName(String playName) {
        currentPlayName = playName;
    }

    public static String getCurrentPlayName() {
        return currentPlayName;
    }

    public static void setCurrentMediaName(String mediaName) {
        currentMediaName = mediaName;
    }

    public static String getCurrentMediaName() {
        return currentMediaName;
    }

}
