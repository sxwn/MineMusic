package com.xiaowei.minemusic.activitys;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.xiaowei.minemusic.R;
import com.xiaowei.minemusic.helpers.UserHelper;
import com.xiaowei.minemusic.utils.UserUtils;

public class MeActivity extends BaseActivity {

    private TextView mTvUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_me);

        initView();
    }

    private void initView() {
        initNavBar(true,"个人中心",false);

        mTvUser = fd(R.id.tv_user);
        mTvUser.setText("用户名：" + UserHelper.getInstance().getPhone());
    }

    /**
     * 修改密码点击事件
     * @param view
     */
    public void onChangeClick (View view) {
        startActivity(new Intent(this,ChangePasswordActivity.class));
    }

    /**
     * 退出登录
     * @param view
     */
    public void onLogoutClick (View view) {
        UserUtils.logout(this);
    }
}
