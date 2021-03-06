package com.xiaowei.minemusic.activitys;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.xiaowei.minemusic.R;
import com.xiaowei.minemusic.utils.UserUtils;

import java.util.Timer;
import java.util.TimerTask;

/**
 * 欢迎页面
 * 1、延迟3s
 * 2、跳转页面
 */
public class WelcomeActivity extends BaseActivity {

    private Timer mTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        init();
    }

    /**
     * 初始化
     */
    private void init() {
        final boolean isLogin = UserUtils.validateUserLogin(this);
        mTimer = new Timer();
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (isLogin) {
                    toMain();
                } else {
                    toLogin();
                }
            }
        }, 3000);
    }

    /**
     * 跳转到LoginActivity
     */
    private void toLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * 跳转到MainActivity
     */
    private void toMain() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mTimer != null)
            mTimer.cancel();
    }
}
