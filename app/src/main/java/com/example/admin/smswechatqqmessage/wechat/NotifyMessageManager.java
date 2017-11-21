package com.example.admin.smswechatqqmessage.wechat;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.widget.Toast;

/**
 * 状态栏收到信息 管理类
 * 信息有：qq、微信、短信、来电等
 * Created by admin on 2017/6/12.
 */

public class NotifyMessageManager {

    private static final String ENABLED_NOTIFICATION_LISTENERS = "enabled_notification_listeners";//能够监听状态栏
    private static final String MESSAGE_BROADCAST="SEND_WX_BROADCAST";
    private static final String QQ="com.tencent.mobileqq";//qq信息
    private static final String WX="com.tencent.mm";//微信信息
    //public static final String MMS="com.android.mms";//短信信息
    private static final String CALL="com.android.incallui";//来电信息
    private Context mContext;

    public NotifyMessageManager(Context context) {
        this.mContext = context;
        registBroadCast();
    }

    /**
     * 注册广播
     */
    public void registBroadCast() {
        IntentFilter messageFilter=new IntentFilter(MESSAGE_BROADCAST);
        mContext.registerReceiver(broadcastNotifyMessage,messageFilter);
    }

    /**
     * 注销广播
     */
    public void unRegistBroadcast(){
        mContext.unregisterReceiver(broadcastNotifyMessage);
    }

    /**
     * 打开服务权限
     */
    public void openSetting(){
        if (!isEnabled()) {
            Intent intent = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
            mContext.startActivity(intent);
        } else {
            Toast.makeText(mContext, "已开启服务权限", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * 判断Notify权限是否打开
     * @return true->Notify权限打开
     */
    private boolean isEnabled() {
        String pkgName = mContext.getPackageName();
        final String flat = Settings.Secure.getString(mContext.getContentResolver(),
                ENABLED_NOTIFICATION_LISTENERS);
        if (!TextUtils.isEmpty(flat)) {
            final String[] names = flat.split(":");
            for (int i = 0; i < names.length; i++) {
                final ComponentName cn = ComponentName.unflattenFromString(names[i]);
                if (cn != null) {
                    if (TextUtils.equals(pkgName, cn.getPackageName())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * NotificationListenerService开关
     */
    public void toggleNotificationListenerService() {
        PackageManager pm = mContext.getPackageManager();
        pm.setComponentEnabledSetting(
                new ComponentName(mContext,NotifyService.class),
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
        pm.setComponentEnabledSetting(
                new ComponentName(mContext,NotifyService.class),
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
    }

    /**
     * 自定义广播接收者，发送信息
     */
    private BroadcastReceiver broadcastNotifyMessage =new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle=intent.getExtras();
            String pachageName=bundle.getString("packageName");
            String message = bundle.getString("message");
            switch (pachageName){
                case WX:
                    Toast.makeText(mContext,"微信信息："+message,Toast.LENGTH_SHORT).show();
                    break;
                case QQ:
                    Toast.makeText(mContext,"QQ信息："+message  ,Toast.LENGTH_SHORT).show();
                    break;
                case CALL:
                    Toast.makeText(mContext,"电话信息："+message,Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };
}
