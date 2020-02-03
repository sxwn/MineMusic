package com.xiaowei.minemusic;
import com.xiaowei.minemusic.entity.Message;
import com.xiaowei.minemusic.MessageReceiverListener;
// 消息服务
interface IMessageService {

    void sendMessage(inout Message message);

    void registerMessageReceiverListener(MessageReceiverListener messageReceiverListener);

    void unRegisterMessageReceiverListener(MessageReceiverListener messageReceiverListener);
}
