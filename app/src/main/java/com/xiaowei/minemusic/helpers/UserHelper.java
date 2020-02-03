package com.xiaowei.minemusic.helpers;

import com.xiaowei.minemusic.models.UserModel;

/**
 * 1、用户登录
 *      1、当用户登录时，使用SharedPreference保存用户的用户标记
 *      2、利用全局单例类UserHelper保存登录用户信息
 *          1、用户登录之后
 *          2、用户重新打开应用程序时,检测SharedPreference中是否存在用户登录标记
 *          ，如果存在则为UserHelper进行赋值，并且进入主页。如果不存在，则进入登录页面。
 * 2、用户退出
 *      1、删除掉SharedPreference保存用户的用户标记，退回登录页面
 */
public class UserHelper {

    private static UserHelper instance;

    private UserHelper () {}

    public static UserHelper getInstance() {
        if (instance == null) {
            synchronized (UserHelper.class) {
                if (instance == null) {
                    instance = new UserHelper();
                }
            }
        }
        return instance;
    }

    private String phone;

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
