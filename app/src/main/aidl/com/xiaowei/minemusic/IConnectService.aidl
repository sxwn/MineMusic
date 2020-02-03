// IConnectService.aidl
package com.xiaowei.minemusic;

// 连接服务
interface IConnectService {

    oneway void connect();

    void disconnect();

    boolean isConnected();
}
