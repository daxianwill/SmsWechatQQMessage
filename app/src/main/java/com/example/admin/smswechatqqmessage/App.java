package com.example.admin.smswechatqqmessage;

import android.app.Application;
import com.example.admin.smswechatqqmessage.util.SharedPreferencesUtils;

public class App extends Application {
    private SharedPreferencesUtils sp;
    @Override
    public void onCreate() {
        super.onCreate();
        sp = SharedPreferencesUtils.getInstance(getApplicationContext(),"message");
        //防止每一次进入都开启一个Service
        sp.put("app_state",1);
    }
}
