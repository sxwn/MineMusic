package com.xiaowei.minemusic.updater;

import com.xiaowei.minemusic.updater.net.INetManager;
import com.xiaowei.minemusic.updater.net.OkHttpNetManager;

/**
 * App升级
 */
public class AppUpdater {

    private static AppUpdater sInstance = new AppUpdater();

    //网络请求,下载的能力
    //okhttp volley httpclient retrofit httpurlconn
    private INetManager mNetManager = new OkHttpNetManager();

//    public void setNetManager(INetManager manager) {
//        mNetManager = manager;
//    }

    public INetManager getNetManager () {
        return mNetManager;
    }

    public static AppUpdater getInstance () {
        return sInstance;
    }
}
