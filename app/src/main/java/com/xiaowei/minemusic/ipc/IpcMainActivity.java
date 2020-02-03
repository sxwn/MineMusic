package com.xiaowei.minemusic.ipc;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.xiaowei.minemusic.IConnectService;
import com.xiaowei.minemusic.IMessageService;
import com.xiaowei.minemusic.IServiceManager;
import com.xiaowei.minemusic.MessageReceiverListener;
import com.xiaowei.minemusic.R;
import com.xiaowei.minemusic.service.RemoteService;
import com.xiaowei.minemusic.entity.Message;

public class IpcMainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button buttonConnect;
    private Button buttonDisConnect;
    private Button buttonIsConnected;
    private Button buttonSendMessage;
    private Button buttonRegisterLisener;
    private Button buttonUnRegisterLisener;
    private Button buttonSendByMessenger;
    private IConnectService connectServiceProxy;
    private IMessageService messageServiceProxy;
    private IServiceManager serviceManagerProxy;
    private Messenger messengerProxy;
    private Handler handler = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(@NonNull android.os.Message msg) {
            super.handleMessage(msg);
            Bundle bundle = msg.getData();
            //防止出现序列化异常的问题
            bundle.setClassLoader(Message.class.getClassLoader());
            final Message message = bundle.getParcelable("message");
            //延迟3s打印
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(IpcMainActivity.this,message.getContent(),Toast.LENGTH_SHORT).show();
                }
            },3000);
        }
    };
    private Messenger clientMessenger = new Messenger(handler);

    private MessageReceiverListener messageReceiverListener = new MessageReceiverListener.Stub() {
        @Override
        public void onReceiverMessage(final Message message) throws RemoteException {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(IpcMainActivity.this,message.getContent(),Toast.LENGTH_SHORT).show();
                }
            });
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ipc_main);

        buttonConnect = findViewById(R.id.btn_connect);
        buttonDisConnect = findViewById(R.id.btn_disconnect);
        buttonIsConnected = findViewById(R.id.btn_is_connected);
        buttonSendMessage = findViewById(R.id.btn_send_message);
        buttonRegisterLisener = findViewById(R.id.btn_register_listener);
        buttonUnRegisterLisener = findViewById(R.id.btn_unregister_listener);
        buttonSendByMessenger = findViewById(R.id.btn_messenger);

        buttonConnect.setOnClickListener(this);
        buttonDisConnect.setOnClickListener(this);
        buttonIsConnected.setOnClickListener(this);
        buttonSendMessage.setOnClickListener(this);
        buttonRegisterLisener.setOnClickListener(this);
        buttonUnRegisterLisener.setOnClickListener(this);
        buttonSendByMessenger.setOnClickListener(this);

        Intent intent = new Intent(this,RemoteService.class);

        //BIND_AUTO_CREATE;自动建立
        bindService(intent, new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder iBinder) {
                try {
                    serviceManagerProxy = IServiceManager.Stub.asInterface(iBinder);
                    connectServiceProxy = IConnectService.Stub.asInterface(serviceManagerProxy.getService(IConnectService.class.getSimpleName()));
                    messageServiceProxy = IMessageService.Stub.asInterface(serviceManagerProxy.getService(IMessageService.class.getSimpleName()));
                    messengerProxy = new Messenger(serviceManagerProxy.getService(Messenger.class.getSimpleName()));
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {

            }
        }, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_connect:
                try {
                    //IConnectService中的oneway关键字,不阻塞主线程
                    connectServiceProxy.connect();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.btn_disconnect:
                try {
                    connectServiceProxy.disconnect();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.btn_is_connected:
                try {
                    boolean isConnected = connectServiceProxy.isConnected();
                    Toast.makeText(IpcMainActivity.this,String.valueOf(isConnected),Toast.LENGTH_LONG).show();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.btn_send_message:
                try {
                    Message message = new Message();
                    message.setContent("message is from main");
                    messageServiceProxy.sendMessage(message);
                    Log.d(IpcMainActivity.class.getSimpleName(),String.valueOf(message.isSendStatus()));
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.btn_register_listener:
                try {
                    messageServiceProxy.registerMessageReceiverListener(messageReceiverListener);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.btn_unregister_listener:
                try {
                    messageServiceProxy.unRegisterMessageReceiverListener(messageReceiverListener);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.btn_messenger:
                try {
                    Message message = new Message();
                    message.setContent("send message from main by messenger");
                    android.os.Message data = new android.os.Message();
                    data.replyTo = clientMessenger;
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("message",message);
                    data.setData(bundle);
                    messengerProxy.send(data);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                break;
        }
    }
}
