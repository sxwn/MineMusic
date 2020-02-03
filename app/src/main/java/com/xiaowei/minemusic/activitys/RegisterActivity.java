package com.xiaowei.minemusic.activitys;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.xiaowei.minemusic.R;
import com.xiaowei.minemusic.utils.UserUtils;
import com.xiaowei.minemusic.views.InputView;

public class RegisterActivity extends BaseActivity {

    private InputView mInputPhone, mInputPassword, mInputPasswordConfirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initView();
    }

    /**
     * 初始化view
     */
    private void initView() {
        initNavBar(true,"注册",false);

        mInputPhone = fd(R.id.input_phone);
        mInputPassword = fd(R.id.input_password);
        mInputPasswordConfirm = fd(R.id.input_password_confirm);
    }

    /**
     * 注册按钮点击事件
     * 1、用户输入合法性验证
     *       1.1 用户输入的手机号是不是合法的
     *       1.2 用户是否已经输入密码和确定密码,并且这两次输入的密码是否相同
     *       1.3 用户输入的手机号是否已经被注册
     * 2、保存用户输入的手机号和密码[MD5加密密码](数据库)
     * @param view
     */
    public void onRegisterClick (View view) {

        String phone = mInputPhone.getInputStr();
        String password = mInputPassword.getInputStr();
        String passwordConfirm = mInputPasswordConfirm.getInputStr();

        boolean result = UserUtils.regiserUser(this, phone, password, passwordConfirm);

        if (!result) return;

//      后退到登录页面之中
        onBackPressed();
    }
}
