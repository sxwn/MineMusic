package com.xiaowei.minemusic.service;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Messenger;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.xiaowei.minemusic.IConnectService;
import com.xiaowei.minemusic.IMessageService;
import com.xiaowei.minemusic.IServiceManager;
import com.xiaowei.minemusic.MessageReceiverListener;
import com.xiaowei.minemusic.entity.Message;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 管理和提供子进程的连接和消息服务
 */
public class RemoteService extends Service {

    private boolean isConnected = false;
    private Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull android.os.Message msg) {
            super.handleMessage(msg);
            Messenger clientMessenger = msg.replyTo;
            Bundle bundle = msg.getData();
            //防止出现序列化异常的问题
            bundle.setClassLoader(Message.class.getClassLoader());
            Message message = bundle.getParcelable("message");
            Toast.makeText(RemoteService.this,message.getContent(),Toast.LENGTH_SHORT).show();
            try {
                Message reply = new Message();
                reply.setContent("messge reply from remote");
                android.os.Message data = new android.os.Message();
                data.replyTo = clientMessenger;
                bundle = new Bundle();
                bundle.putParcelable("message",reply);
                data.setData(bundle);
                clientMessenger.send(data);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    };
    private RemoteCallbackList<MessageReceiverListener> messageReceiverListenerRemoteCallbackList = new RemoteCallbackList<>();
    private ScheduledThreadPoolExecutor scheduledThreadPoolExecutor;
    private ScheduledFuture scheduledFuture;
    private Messenger messenger = new Messenger(handler);

    private IConnectService connectService = new IConnectService.Stub() {
        @Override
        public void connect() throws RemoteException {
            try {
                Thread.sleep(5000);
                isConnected = true;
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(RemoteService.this,"connect",Toast.LENGTH_SHORT).show();
                    }
                });
                //5s定时任务
                scheduledFuture = scheduledThreadPoolExecutor.scheduleAtFixedRate(new Runnable() {
                    @Override
                    public void run() {
                        int size = messageReceiverListenerRemoteCallbackList.beginBroadcast();
                        for (int i = 0;i<size;i++){
                            Message message = new Message();
                            message.setContent("this message from remote");
                            try {
                                messageReceiverListenerRemoteCallbackList.getBroadcastItem(i).onReceiverMessage(message);
                            } catch (RemoteException e) {
                                e.printStackTrace();
                            }
                        }
                        messageReceiverListenerRemoteCallbackList.finishBroadcast();
                    }
                },5000,5000, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void disconnect() throws RemoteException {
            isConnected = false;
            scheduledFuture.cancel(true);
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(RemoteService.this,"disconnect",Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        public boolean isConnected() throws RemoteException {
            return isConnected;
        }
    };

    private IMessageService messageService = new IMessageService.Stub() {
        @Override
        public void sendMessage(final Message message) throws RemoteException {
            Log.d(RemoteService.class.getSimpleName(),String.valueOf(message.getContent()));
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(RemoteService.this,message.getContent(),Toast.LENGTH_SHORT).show();
                }
            });
            if (isConnected){
                message.setSendStatus(true);
            }else {
                message.setSendStatus(false);
            }
        }

        @Override
        public void registerMessageReceiverListener(MessageReceiverListener messageReceiverListener) throws RemoteException {
            if (messageReceiverListener != null) {
                messageReceiverListenerRemoteCallbackList.register(messageReceiverListener);
            }
        }

        @Override
        public void unRegisterMessageReceiverListener(MessageReceiverListener messageReceiverListener) throws RemoteException {
            if (messageReceiverListener != null) {
                messageReceiverListenerRemoteCallbackList.unregister(messageReceiverListener);
            }
        }
    };

    private IServiceManager serviceManager = new IServiceManager.Stub() {
        @Override
        public IBinder getService(String serviceName) throws RemoteException {
            if  (IConnectService.class.getSimpleName().equals(serviceName)){
                return connectService.asBinder();
            } else if (IMessageService.class.getSimpleName().equals(serviceName)){
                return messageService.asBinder();
            } else if (Messenger.class.getSimpleName().equals(serviceName)){
                return messenger.getBinder();
            } else {
                return null;
            }
        }
    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return serviceManager.asBinder();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(1);
    }
}
