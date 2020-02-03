package com.xiaowei.minemusic.views;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.IBinder;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.bumptech.glide.Glide;
import com.xiaowei.minemusic.R;
import com.xiaowei.minemusic.helpers.MediaPlayerHelp;
import com.xiaowei.minemusic.models.MusicModel;
import com.xiaowei.minemusic.service.MusicService;

public class PlayMusicView extends FrameLayout {

    private Context mContext;
    private Intent mServiceIntent;
    private MusicService.MusicBind mMusicBind;
    private MusicModel mMusicModel;

    private boolean isPlaying, isBindService;
    private View mView;
    private FrameLayout mFlPlayMusic;
    private ImageView mIvNeedle, mIvIcon, mIvPlay;

    private Animation mPlayMusicAnim, mPlayNeedleAnim, mStopNeedleAnim;

    public PlayMusicView(@NonNull Context context) {
        super(context);
        init(context);
    }

    public PlayMusicView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public PlayMusicView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public PlayMusicView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init (Context context) {
//        MediaPlayer
        mContext = context;

        mView = LayoutInflater.from(mContext).inflate(R.layout.play_music, this, false);
        mFlPlayMusic = mView.findViewById(R.id.fl_play_music);
        mFlPlayMusic.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                trigger();
            }
        });
        mIvIcon = mView.findViewById(R.id.iv_icon);
        mIvNeedle = mView.findViewById(R.id.iv_needle);
        mIvPlay = mView.findViewById(R.id.iv_play);

        /**
         * 1、定义所需要执行的动画
         *         1.1 光盘转动的动画
         *         1.2 指针指向光盘的动画
         *         1.3 指针离开光盘的动画
         * 2、startAnimation
         */
        mPlayMusicAnim = AnimationUtils.loadAnimation(mContext, R.anim.play_music_anim);
        mPlayNeedleAnim = AnimationUtils.loadAnimation(mContext, R.anim.play_needle_anim);
        mStopNeedleAnim = AnimationUtils.loadAnimation(mContext, R.anim.stop_needle_anim);

        addView(mView);
    }

    /**
     * 切换播放状态
     */
    public void trigger () {
        if (isPlaying) {
            stopMusic();
        } else {
            playMusic();
        }
    }

    /**
     * 播放音乐
     */
    public void playMusic () {
        isPlaying = true;
        mIvPlay.setVisibility(GONE);
        mFlPlayMusic.startAnimation(mPlayMusicAnim);
        mIvNeedle.startAnimation(mPlayNeedleAnim);

        startMusicService();
    }

    /**
     * 停止播放
     */
    public void stopMusic () {
        isPlaying = false;
        mIvPlay.setVisibility(VISIBLE);
        mFlPlayMusic.clearAnimation();
        mIvNeedle.startAnimation(mStopNeedleAnim);

        if (mMusicBind != null)
            mMusicBind.stopMusic();
    }

    /**
     * 设置光盘中显示的音乐封面图片
     */
    public void setMusicIcon () {
        Glide.with(mContext)
                .load(mMusicModel.getPoster())
                .into(mIvIcon);
    }

    public void setMusic (MusicModel musicModel) {
        mMusicModel = musicModel;
        setMusicIcon();
    }

    /**
     * 启动音乐服务
     */
    private void startMusicService () {
//        启动Service
        if (mServiceIntent == null) {
            mServiceIntent = new Intent(mContext, MusicService.class);
            mContext.startService(mServiceIntent);
        } else {
            mMusicBind.playMusic();
        }

//        绑定  Service,首先判断Service 未绑定的话,绑定服务
        if (!isBindService) {
            isBindService = true;
            mContext.bindService(mServiceIntent, coon, Context.BIND_AUTO_CREATE);
        }
    }

    /**
     * 解除绑定
     */
    public void destroy () {
//        如果已经绑定了服务,解除绑定
        if (isBindService) {
            isBindService = false;
            mContext.unbindService(coon);
        }
    }

    ServiceConnection coon = new ServiceConnection () {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mMusicBind = (MusicService.MusicBind) service;
            mMusicBind.setMusic(mMusicModel);
            mMusicBind.playMusic();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };
}
