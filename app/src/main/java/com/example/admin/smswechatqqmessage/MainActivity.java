package com.example.admin.smswechatqqmessage;

import android.content.ComponentName;
import android.content.Intent;
import android.provider.Settings;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import com.example.admin.smswechatqqmessage.permission.CheckPermissionsActivity;
import com.example.admin.smswechatqqmessage.util.AppConstants;
import com.example.admin.smswechatqqmessage.util.SharedPreferencesUtils;
import com.example.admin.smswechatqqmessage.wechat.MessageService;

/**
 * 主页面
 * @author daxian
 */
public class MainActivity extends CheckPermissionsActivity {

    private SharedPreferencesUtils sp;
    private static final String ENABLED_NOTIFICATION_LISTENERS = "enabled_notification_listeners";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //是否开启notification服务
        openSetting();

        sp = SharedPreferencesUtils.getInstance(this,"message");
        //防止每一次进入都开启一个Service
        if (sp.get("app_state",0).toString().equals("1")){
            Intent stopIntent = new Intent(this, MessageService.class);
            startService(stopIntent);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        sp.put(AppConstants.APP_STATE,2);
    }

    /**
     * 开启服务权限
     */
    public void openSetting(){
        if (!isEnabled()) {
            Intent intent = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
            MainActivity.this.startActivity(intent);
        } else {
            Toast.makeText(MainActivity.this, "已开启服务权限", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * 判断是否已打开服务
     * @return true(打开) false(关闭)
     */
    private boolean isEnabled() {
        String pkgName = MainActivity.this.getPackageName();
        final String flat = Settings.Secure.getString(MainActivity.this.getContentResolver(),
                ENABLED_NOTIFICATION_LISTENERS);
        if (!TextUtils.isEmpty(flat)) {
            final String[] names = flat.split(":");
            for (String name : names) {
                final ComponentName cn = ComponentName.unflattenFromString(name);
                if (cn != null) {
                    if (TextUtils.equals(pkgName, cn.getPackageName())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
