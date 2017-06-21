package com.example.admin.smswechatqqmessage.wechat;

import android.content.Intent;
import android.os.Bundle;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

/**
 * 状态栏信息监听服务类
 */

public class NotifyService extends NotificationListenerService {

    public static final String SEND_WX_BROADCAST="SEND_WX_BROADCAST";
    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
    }
    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn, RankingMap rankingMap) {
        Log.e("AAA", "=2==onNotificationPosted   ID :"
                + sbn.getId() + "\t"
                + sbn.getNotification().tickerText + "\t"
                + sbn.getPackageName());
        Intent intent=new Intent();
        intent.setAction(SEND_WX_BROADCAST);
        Bundle bundle=new Bundle();
        if (!sbn.getPackageName().isEmpty()){
            bundle.putString("packageName",sbn.getPackageName());
        }
        if (sbn.getNotification().tickerText != null){
            bundle.putString("message",sbn.getNotification().tickerText.toString());
        }
        intent.putExtras(bundle);
        this.sendBroadcast(intent);

    }
    @Override
    public void onNotificationRemoved(StatusBarNotification sbn, RankingMap rankingMap) {
    }
}
