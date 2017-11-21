package com.example.admin.smswechatqqmessage.wechat;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.example.admin.smswechatqqmessage.MainActivity;
import com.example.admin.smswechatqqmessage.R;

/**
 * 短信、微信、QQ监听服务
 * @author daxian
 */

public class MessageService extends Service {

    private static final String TAG = MessageService.class.getSimpleName();
    private final static int FOREGROUND_ID = 1000;

    private Context mContext;
    private Uri smsInbox = Uri.parse("content://sms/");

    public MessageService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        this.mContext = this;
        initSmsObserver();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG,"onStartCommand");
        setNotification();
        NotifyMessageManager comeMessageManager = new NotifyMessageManager(getApplicationContext());
        comeMessageManager.toggleNotificationListenerService();
        comeMessageManager.openSetting();
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * 初始化短信observer
     */
    private void initSmsObserver(){
        SmsObserver smsObserver;
        smsObserver = new SmsObserver(new Handler());
        getContentResolver().registerContentObserver(smsInbox, true,
                smsObserver);
    }

    /**
     * 短信观察者
     */
    public class SmsObserver extends ContentObserver {
        private SmsObserver(Handler handler) {
            super(handler);
        }
        @Override
        public void onChange (boolean selfChange) {
            super.onChange(selfChange);
            onChange(selfChange, null);
        }
        @Override
        public void onChange(boolean selfChange, Uri uri) {
            getUri(uri);
        }
    }

    /**
     * 短信接收消息（从数据库获取）
     * @param uri 短信地址
     *
     * URI主要有：
     * content://sms/             所有短信
     * content://sms/inbox        收件箱
     * content://sms/sent         已发送
     * content://sms/draft        草稿
     * content://sms/outbox       发件箱
     * content://sms/failed       发送失败
     * content://sms/queued       待发送列表
     *
     *
     */
    @SuppressLint("ShowToast")
    public synchronized void  getUri(Uri uri) {

        /*
         * 适配某些较旧的设备，可能只会触发onChange(boolean selfChange)方法，没有传回uri参数，
         * 此时只能通过"content://sms/inbox"来查询短信
         */
        System.out.println("uri======="+uri+"=================================================================================");
        if (uri == null) {
            uri = Uri.parse("content://sms/inbox");
        }

        try {
            String[] projection = new String[] { "_id", "address", "person", "body", "date", "type" };

            Cursor cursor = getContentResolver().query(
                    Uri.parse("content://sms/"), projection, null,
                    null,
                    "date desc");
            assert cursor != null;
            System.out.println("cursor==========="+cursor.getCount());
            if(cursor.getCount()>0){
                //不遍历只拿当前最新的一条短信 moveToFirst() moveToNext()
                if(cursor.moveToFirst()){
                    String smsId = cursor.getString(cursor.getColumnIndex("_id"));
                    String address =cursor.getString( cursor.getColumnIndex("address"));
                    String person = cursor.getString(cursor.getColumnIndex("person"));
                    String body = cursor.getString(cursor.getColumnIndex("body"));
                    String date = cursor.getString(cursor.getColumnIndex("date"));
                    String type = cursor.getString(cursor.getColumnIndex("type"));
                    //发送短信 type为2
                    if ("2".equals(type)){
                        Toast.makeText(getApplicationContext(),"发送短信"+"smsId="+smsId+"\naddress="+address+ "\nperson="+person+"\nbody=" +body+
                                "\ndate="+date+"\ntype="+type,Toast.LENGTH_SHORT);
                    }else if (("content://sms/inbox-insert").equals(uri.toString())){
                        //接收短信 接收短信必有 URI=content://sms/inbox-insert
                        Toast.makeText(getApplicationContext(),"接收短信"+"smsId="+smsId+"\naddress="+address+ "\nperson="+person+"\nbody=" +body+
                                "\ndate="+date+"\ntype="+type,Toast.LENGTH_SHORT);
                    }
                    if (!cursor.isClosed()){
                        cursor.close();
                    }
                }
            }else{
                System.out.println("no result");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //===========================================================微信、短信监听===================================================
    /**
     * 设置状态栏
     */
    private void setNotification(){
        NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setContentTitle("短信、微信、QQ监听");
        builder.setContentText("监听服务开启");
        builder.setWhen(System.currentTimeMillis());
        Intent activityIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 1, activityIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);
        Notification notification = builder.build();
        startForeground(FOREGROUND_ID, notification);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
