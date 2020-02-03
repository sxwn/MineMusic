package com.xiaowei.minemusic;

interface IServiceManager {
    IBinder getService(String serviceName);
}