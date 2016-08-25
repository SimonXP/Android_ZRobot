package com.robot.et.util;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by Tony on 2016/7/26.
 */
public class FileUtils {
    private static final String TAG = "file";

    //读取asset目录下文件
    public static String readFile(Context context, String file, String code) {
        if (null == context) {
            Log.e(TAG, "context is null");
            return "";
        } else {
            AssetManager am = context.getAssets();
            int len = 0;
            byte[] buf = null;
            String result = "";
            try {
                InputStream in = am.open(file);
                len = in.available();
                buf = new byte[len];
                in.read(buf, 0, len);
                result = new String(buf, code);
                in.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return result;
        }
    }

    //获取路径
    public static String getFilePath(String fileName) {
        String fileSrc = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + fileName;
        return fileSrc;
    }

    //创建文件
    public static void createFile(String fileName) {
        File file = new File(getFilePath(fileName));
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                Log.i("hdm", "createFile  IOException");
            }
        }
    }

    //删除文件
    public static void deleteFile(String fileName) {
        File file = new File(getFilePath(fileName));
        if (file.exists()) {
            file.delete();
        }
    }

    //写数据到文件中
    public static void writeToFile(byte[] daats, String fileName) {
        FileOutputStream stream;
        createFile(fileName);
        try {
            stream = new FileOutputStream(new File(getFilePath(fileName)));
            stream.write(daats);
            Log.i(TAG, "write  succcess");
            stream.close();
        } catch (FileNotFoundException e) {
            Log.i(TAG, "write  FileNotFoundException");
            e.printStackTrace();
        } catch (IOException e) {
            Log.i(TAG, "write  FileNotFoundException");
            e.printStackTrace();
        }
    }

    public static void saveFilePath(Bitmap bitmap, String fileName) {
        OutputStream stream = null;
        try {
            createFile(fileName);
            stream = new FileOutputStream(new File(getFilePath(fileName)));
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            stream.flush();
            stream.close();
            Log.i(TAG, "saveFilePath  success");
        } catch (FileNotFoundException e) {
            Log.i(TAG, "saveFilePath  IOException");
        } catch (IOException e) {
            Log.i(TAG, "saveFilePath  IOException");
        }
    }

    public static File[] getFiles(String filePath) {
        File[] files = null;
        if (!TextUtils.isEmpty(filePath)) {
            File file = new File(filePath);
            files = new File[]{file};
        }
        return files;
    }

    public static Bitmap Bytes2Bimap(byte[] data) {
        Bitmap bitmap = null;
        if (data != null && data.length != 0) {
            bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
        }
        return bitmap;
    }

}
