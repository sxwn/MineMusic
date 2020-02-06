package com.xiaowei.minemusic.updater.net;

import android.os.Handler;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class OkHttpNetManager implements INetManager {

    private static OkHttpClient sOkHttpClient;

    private static Handler sHandler = new Handler();

    static {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        sOkHttpClient = builder.connectTimeout(15, TimeUnit.SECONDS)
                .build();
//        https：自签名的  Okhttp握手的错误
//        builder.sslSocketFactory()
    }

    @Override
    public void get(String url, INetCallBack callBack) {
//     requestbuilder  --->Request --->Call --->execute/enqueue
        Request.Builder builder = new Request.Builder();
        Request request = builder.url(url).get().build();
        Call call = sOkHttpClient.newCall(request);
//        Response execute = call.execute();
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("weip", e.getMessage() + e.getLocalizedMessage());
//                非 UI线程
                sHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        callBack.failed(e);
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    String string = response.body().string();
                    sHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            callBack.success(string);
                        }
                    });
                } catch (Throwable e) {
                    e.printStackTrace();
                    callBack.failed(e);
                }
            }
        });
    }

    @Override
    public void download(String url, File targetFile, INetDownLoadCallBack callBack) {

        if (!targetFile.exists()) {
            targetFile.getParentFile().mkdirs();
        }

        Request.Builder builder = new Request.Builder();
        Request request = builder.url(url).get().build();
        Call call = sOkHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                sHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        callBack.failed(e);
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

//                文件保存
                InputStream is = null;
                OutputStream os = null;

                try {
                    long totalLen = response.body().contentLength();

                    is = response.body().byteStream();
                    os = new FileOutputStream(targetFile);

//                输入流往输出流写东西
                    byte[] buffer = new byte[8 * 1024];
                    int curLen = 0;

                    int bufferLen = 0;

                    while ((bufferLen = is.read(buffer)) != -1) {
                        os.write(buffer, 0, bufferLen);
                        os.flush();
                        curLen += bufferLen;

                        final long finalCurLen = curLen;
                        sHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                callBack.progress((int) (finalCurLen * 1.0f / totalLen * 100));
                            }
                        });
                    }

                    try {
                        targetFile.setExecutable(true, false);
                        targetFile.setReadable(true, false);
                        targetFile.setWritable(true, false);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    sHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            callBack.success(targetFile);
                        }
                    });

                } catch (final Throwable e) {
                    e.printStackTrace();
                    sHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            callBack.failed(e);
                        }
                    });
                } finally {
                    if (is!= null) {
                        is.close();
                    }
                    if (os != null) {
                        os.close();
                    }
                }
            }
        });
    }
}
