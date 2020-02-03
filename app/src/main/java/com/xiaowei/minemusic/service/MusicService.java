package com.xiaowei.minemusic.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;

import com.xiaowei.minemusic.R;
import com.xiaowei.minemusic.activitys.WelcomeActivity;
import com.xiaowei.minemusic.helpers.MediaPlayerHelp;
import com.xiaowei.minemusic.models.MusicModel;

/**
 * 1、通过Service 连接 PlayMusicView 和 MediaPlayHelper
 * 2、PlayMusicView --  通过Service
 *      1、播放音乐、暂停音乐
 *      2、启动、绑定、解除绑定Service
 * 3、MediaPlayHelper -- Service
 *      1、播放音乐、暂停音乐
 *      2、监听音乐播放完成,  停止 Service
 */
public class MusicService extends Service {

//    不可为0
    public static final int NOTIFICATION_ID = 1;

    private MediaPlayerHelp mMediaPlayerHelp;
    private MusicModel mMusicModel;

    public MusicService() {
    }

    public class MusicBind extends Binder {
        /**
         * 设置音乐 (MusicModel)
         */
        public void setMusic (MusicModel musicModel) {
            mMusicModel = musicModel;
            startForeground();
        }

        /**
         * 播放音乐
         */
        public void playMusic () {
            /**
             * 1、判断当前音乐是否是已经在播放的音乐
             * 2、如果当前的音乐时是已经在播放的音乐的话,那么就直接执行start方法
             * 3、如果当前播放的音乐不是需要播放的音乐的话,那么就调用setPath方法
             */
            if (mMediaPlayerHelp.getPath() != null
                    && mMediaPlayerHelp.getPath().equals(mMusicModel.getPath())) {
                mMediaPlayerHelp.start();
            }else{
                mMediaPlayerHelp.setPath(mMusicModel.getPath());
                mMediaPlayerHelp.setOnMediaPlayerHelperListener(new MediaPlayerHelp.OnMediaPlayerHelperListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        mMediaPlayerHelp.start();
                    }

                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        stopSelf();
                    }
                });
            }
        }

        /**
         * 暂停播放
         */
        public void stopMusic () {
            mMediaPlayerHelp.pause();
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new MusicBind();
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mMediaPlayerHelp = MediaPlayerHelp.getInstance(this);
    }

    /**
     * 系统默认不允许不可见的后台服务播放音乐,希望后台服务播放音乐,则必须设置后台服务是可见的
     * Notification
     */
    /**
     * 设置服务在前台可见
     */
    private void startForeground () {

        /**
         * 通知栏点击跳转的Intent
         */
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, WelcomeActivity.class), PendingIntent.FLAG_CANCEL_CURRENT);

        /**
         * 创建Notification
         */
        Notification notification = new Notification.Builder(this)
                .setContentTitle(mMusicModel.getName())
                .setContentText(mMusicModel.getAuthor())
                .setSmallIcon(R.mipmap.logo_xb)
                .setContentIntent(pendingIntent)
                .build();

        /**
         * 设置 notification 在前台展示
          */
        startForeground(NOTIFICATION_ID, notification);
    }
}
