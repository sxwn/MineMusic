package com.xiaowei.minemusic.updater.net;

import java.io.File;

public interface INetManager {

    void get(String url, INetCallBack callBack, Object tag);

    void download(String url, File targetFile, INetDownLoadCallBack callBack, Object tag);

    void cacel(Object tag);
}
