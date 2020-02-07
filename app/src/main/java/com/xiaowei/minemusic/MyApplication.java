package com.xiaowei.minemusic;

import android.app.Application;
import android.content.Context;

import androidx.multidex.MultiDex;

import com.blankj.utilcode.util.Utils;
import com.xiaowei.minemusic.helpers.RealmHelp;

import io.realm.Realm;

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        
        Utils.init(this);
        Realm.init(this);

        RealmHelp.magration();
    }

}
