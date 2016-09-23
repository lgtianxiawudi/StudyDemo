package com.example.ligang.studydemo.main.ui;

import android.app.Application;

import com.example.ligang.commonlibrary.util.LogUtil;
import com.example.ligang.studydemo.BuildConfig;

/**
 * Created by ligang967 on 16/9/23.
 */

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        LogUtil.setEnvIsdebug(BuildConfig.IS_DEBUG);
    }
}
