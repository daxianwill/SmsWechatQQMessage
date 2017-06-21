package com.example.admin.smswechatqqmessage.wechat;

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
import com.example.admin.smswechatqqmessage.util.SharedPreferencesUtils;

/**
 * 短信、微信、QQ监听服务
 */

public class MessageService extends Service {

    private static final String TAG = MessageService.class.getSimpleName();//TAG
    private final static int FOREGROUND_ID = 1000;

    private Context mContext;
    private NotifyMessageManager comeMessageManager;
    private Uri SMS_INBOX = Uri.parse("content://sms/");
    private Cursor cursor;//查询数据库，光标
    private int firstSms = 0;
    private int secondSms = 0;
    private String smsIdFirst;
    private String smsIdSecond;
    private SharedPreferencesUtils sp;

    @Override
    public void onCreate() {
        super.onCreate();
        this.mContext = this;
        sp = SharedPreferencesUtils.getInstance(getApplicationContext(),"message");
        initSmsObserver();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG,"onStartCommand");
        setNotification();
        comeMessageManager=new NotifyMessageManager(getApplicationContext());
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
        getContentResolver().registerContentObserver(SMS_INBOX, true,
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
            /**
             * 适配某些较旧的设备，可能只会触发onChange(boolean selfChange)方法，没有传回uri参数，
             * 此时只能通过"content://sms/inbox"来查询接收短信
             * 此时只能通过"content://sms/sent"来查询发送短信
             */
            getReceiveSms(uri);
            getSendSms(uri);
        }
    }

    /**
     * 短信接收消息（从数据库获取）
     * @param uri
     */
    public void getReceiveSms(Uri uri) {
        if (uri == null) {
            uri = Uri.parse("content://sms/inbox");
        }
        if (uri.toString().equals("content://sms/raw")) {
            return;
        }
        try {
            cursor = getContentResolver().query(
                    Uri.parse("content://sms/inbox"), null, null,
                    null,
                    "date desc");
            if(cursor!=null){
                if(cursor.moveToFirst()){//不遍历只拿当前最新的一条短信 moveToFirst() moveToNext()
                    String smsId = cursor.getString(cursor.getColumnIndex("_id"));
                    System.out.println("短信的Id===="+smsId);
                    if (sp.get("smsInfo","").toString()==null){
                        smsIdFirst = sp.get("smsInfo","").toString();
                    }
                    if (!smsId.equals(smsIdFirst)){
                        if (firstSms == 0){
                            smsIdFirst = smsId;
                            sp.put("smsId",smsIdFirst);
                            firstSms++;
                        }else{
                            System.out.println("smsId inbox=="+smsId);
                            smsIdFirst = smsId;
                            sp.put("smsId",smsIdFirst);
                            String type = cursor.getString(cursor.getColumnIndex("type"));//状态 1==接受短信 2==发送短信
                            String id = cursor.getString(cursor.getColumnIndex("thread_id"));//id 短信Id
                            String address = cursor.getString(cursor.getColumnIndex("address"));// 短信号码
                            String body = cursor.getString(cursor.getColumnIndex("body"));
                            cursor.close();
                            firstSms++;
                            System.out.println("type=="+type);
                            System.out.println(body);
                            // 已经有了sms 做任何你想做的事情
                            Toast.makeText(getApplicationContext(),"接收短信："+body,Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }else{
                Log.e(TAG, "error: cursor == null");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            if(cursor!=null){cursor.close();}
        }
    }

    /**
     * 短息发送消息（从数据库获取）
     * @param uri
     */
    public void getSendSms(Uri uri){
        if (uri == null) {
            uri = Uri.parse("content://sms/sent");
        }
        if (uri.toString().equals("content://sms/raw")) {
            return;
        }
        try {
            cursor = getContentResolver().query(
                    Uri.parse("content://sms/sent"), null, null,
                    null,
                    "date desc");
            if(cursor!=null){
                if(cursor.moveToFirst()){//不遍历只拿当前最新的一条短信 moveToFirst() moveToNext()
                    String smsId = cursor.getString(cursor.getColumnIndex("_id"));
                    if (!smsId.equals(smsIdSecond)){
                        if (secondSms == 0){
                            smsIdSecond = smsId;
                            secondSms++;
                        }else{
                            System.out.println("smsId sent=="+smsId);
                            smsIdSecond = smsId;
                            String type = cursor.getString(cursor.getColumnIndex("type"));;//状态 1==接受短信 2==发送短信
                            String id = cursor.getString(cursor.getColumnIndex("thread_id"));//id 短信Id
                            String address = cursor.getString(cursor.getColumnIndex("address"));// 短信号码
                            String body = cursor.getString(cursor.getColumnIndex("body"));
                            cursor.close();
                            System.out.println("type=="+type);
                            System.out.println(body);
                            // 已经有了sms 做任何你想做的事情
                            secondSms++;
                            Toast.makeText(getApplicationContext(),"发送短信："+body,Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }else{
                Log.e(TAG, "error: cursor == null");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            if(cursor!=null){cursor.close();}
        }
    }

    //===========================================================微信、短信监听=======================================
    /**
     * 设置状态栏
     */
    private void setNotification(){
        NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setContentTitle("短信、微信、QQ监听");
        builder.setContentText("这个是显服务");
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
