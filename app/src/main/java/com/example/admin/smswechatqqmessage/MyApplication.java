package com.example.admin.smswechatqqmessage;

import android.app.Application;

import com.example.admin.smswechatqqmessage.util.AppConstants;
import com.example.admin.smswechatqqmessage.util.SharedPreferencesUtils;

/**
 * @author admin
 */
public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        SharedPreferencesUtils sp = SharedPreferencesUtils.getInstance(getApplicationContext(), "message");
        //防止每一次进入都开启一个Service
        sp.put(AppConstants.APP_STATE,1);
    }
}
