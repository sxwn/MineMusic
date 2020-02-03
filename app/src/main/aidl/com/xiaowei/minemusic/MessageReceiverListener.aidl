package com.xiaowei.minemusic;
import com.xiaowei.minemusic.entity.Message;

interface MessageReceiverListener {

    void onReceiverMessage(in Message message);
}
