package com.xiaowei.minemusic.activitys;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.blankj.utilcode.util.Utils;
import com.xiaowei.minemusic.R;
import com.xiaowei.minemusic.utils.UserUtils;
import com.xiaowei.minemusic.views.InputView;

/**
 * 登录
 * NavigationBar
 */
public class LoginActivity extends BaseActivity {

    private InputView mInputPhone,mInputPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initView();
    }

    /**
     * 初始化view
     */
    private void initView() {
        initNavBar(false,"登录",false);
        mInputPhone = fd(R.id.input_phone);
        mInputPassword = fd(R.id.input_password);
    }

    /**
     * 跳转注册页面点击事件
     * @param view
     */
    public void onRegisterClick(View view) {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }

    /**
     * 登录
     */
    public void onLoginClick(View view) {

        String phone = mInputPhone.getInputStr();
        String password = mInputPassword.getInputStr();

//        验证用户输入是否合法
        if (!UserUtils.validateLogin(this, phone, password)) {
            return;
        };

//        跳转到应用首页
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
