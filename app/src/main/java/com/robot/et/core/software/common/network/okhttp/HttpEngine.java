package com.robot.et.core.software.common.network.okhttp;

import android.content.Context;
import android.text.TextUtils;

import com.robot.et.core.software.common.network.okhttp.cookie.PersistentCookieStore;
import com.squareup.okhttp.CacheControl;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.Headers;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;

import java.io.File;
import java.io.IOException;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.util.concurrent.TimeUnit;

public class HttpEngine {

    private static HttpEngine mInstance;

    private OkHttpClient mOkHttpClient;

    private Context mContext;

    public static final MediaType JSON = MediaType.parse("application/json;charset=utf-8");

    private static int timeOut = 40000;

    private HttpEngine() {
        mOkHttpClient = new OkHttpClient();
        mOkHttpClient.setConnectTimeout(timeOut, TimeUnit.MILLISECONDS);
        mOkHttpClient.setReadTimeout(timeOut, TimeUnit.MILLISECONDS);
        mOkHttpClient.setFollowRedirects(true);
    }

    public void initContext(Context context) {
        if (null == mContext) {
            mContext = context;
        }
        mOkHttpClient.setCookieHandler(new CookieManager(
                new PersistentCookieStore(mContext.getApplicationContext()), CookiePolicy.ACCEPT_ALL));
    }

    public static HttpEngine getInstance() {
        if (mInstance == null) {
            synchronized (HttpEngine.class) {
                if (mInstance == null) {
                    mInstance = new HttpEngine();
                }
            }
        }
        return mInstance;
    }

    //url 传入完整的url链接   jsonData   json格式
    public Request createRequest(String url, String jsonData) {
        if (TextUtils.isEmpty(jsonData)) {
            return null;
        }
        Request request;
        RequestBody body = RequestBody.create(JSON, jsonData);
        Request.Builder builder = new Request.Builder().cacheControl(CacheControl.FORCE_NETWORK).url(url).post(body);
        request = builder.build();
        return request;
    }

    //url 传入完整的url链接
    public Request createRequest(String url) {
        Request request = new Request.Builder().url(url).build();
        return request;
    }

    //url 传入完整的url链接   params参数
    public Request createRequest(String url, Param... params) {
        if (params == null) {
            params = new Param[0];
        }
        FormEncodingBuilder builder = new FormEncodingBuilder();
        for (Param param : params) {
            builder.add(param.key, param.value);
        }
        RequestBody requestBody = builder.build();
        return new Request.Builder().url(url).post(requestBody).build();
    }

    public Call createRequestCall(Request request) {
        if (null == mOkHttpClient || null == request) {
            return null;
        }
        return mOkHttpClient.newCall(request);
    }

    public Request createRequest(String url, File[] files, String[] fileKeys,
                                 Param... params) throws IOException {
        return buildMultipartFormRequest(url, files, fileKeys, params);
    }

    private Request buildMultipartFormRequest(String url, File[] files,
                                              String[] fileKeys, Param[] params) {
        params = validateParam(params);

        MultipartBuilder builder = new MultipartBuilder().type(MultipartBuilder.FORM);

        for (Param param : params) {
            builder.addPart(
                    Headers.of("Content-Disposition", "form-data; name=\"" + param.key + "\""),
                    RequestBody.create(null, param.value));
        }
        if (files != null) {
            RequestBody fileBody = null;
            for (int i = 0; i < files.length; i++) {
                File file = files[i];
                String fileName = file.getName();
                fileBody = RequestBody.create(MediaType.parse(guessMimeType(fileName)), file);
                // 根据文件名设置contentType
                builder.addPart(Headers.of("Content-Disposition", "form-data; name=\""
                        + fileKeys[i] + "\"; filename=\"" + fileName + "\""), fileBody);
            }
        }

        RequestBody requestBody = builder.build();
        return new Request.Builder().url(url).post(requestBody).build();
    }

    private String guessMimeType(String path) {
        FileNameMap fileNameMap = URLConnection.getFileNameMap();
        String contentTypeFor = fileNameMap.getContentTypeFor(path);
        if (contentTypeFor == null) {
            contentTypeFor = "application/octet-stream";
        }
        return contentTypeFor;
    }

    private Param[] validateParam(Param[] params) {
        if (params == null)
            return new Param[0];
        else
            return params;
    }

    public static class Param {
        public Param() {
        }

        public Param(String key, String value) {
            this.key = key;
            this.value = value;
        }

        String key;
        String value;
    }
}
