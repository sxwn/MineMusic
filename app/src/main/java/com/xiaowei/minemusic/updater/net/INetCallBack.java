package com.xiaowei.minemusic.updater.net;

public interface INetCallBack {

    void success(String response);

    void failed(Throwable throwable);

}


