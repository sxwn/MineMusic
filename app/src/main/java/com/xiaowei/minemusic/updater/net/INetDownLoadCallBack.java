package com.xiaowei.minemusic.updater.net;

import java.io.File;

public interface INetDownLoadCallBack {

    void success(File apkFile);

    void progress(int progress);

    void failed(Throwable throwable);

}
