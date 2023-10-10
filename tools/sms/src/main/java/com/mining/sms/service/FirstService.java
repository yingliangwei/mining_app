package com.mining.sms.service;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.telephony.SmsManager;

import androidx.annotation.Nullable;

import com.mining.sms.LongLink.OnData;
import com.mining.sms.LongLink.SocketManage;
import com.mining.sms.util.Notification;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

import java.util.List;

public class FirstService extends Service implements OnData {
    private SocketManage socketManage;

    private final CountDownTimer countDownTimer = new CountDownTimer(Long.MAX_VALUE, 15_000) {
        @Override
        public void onTick(long millisUntilFinished) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    if (socketManage == null) {
                        return;
                    }
                    socketManage.print("ping");
                }
            }).start();
        }

        @Override
        public void onFinish() {
            countDownTimer.start();
        }
    };

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Notification.sendSimpleNotify(this);
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        EventBus.getDefault().post("Service 启动成功");
        initSocket();
        countDownTimer.start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        countDownTimer.cancel();
    }


    private void initSocket() {
        socketManage = new SocketManage();
        socketManage.setData(this);
        socketManage.start();
    }

    @Override
    public void connect(SocketManage socketManage) {
        EventBus.getDefault().post("连接成功");
    }

    @Override
    public void handle(String ds) {
        if (ds.equals("pong")) {
            EventBus.getDefault().post("pong");
            return;
        }
        try {
            JSONObject jsonObject = new JSONObject(ds);
            int type = jsonObject.getInt("type");
            if (type == 0) {
                //心跳
                return;
            }
            if (type == 1) {
                EventBus.getDefault().post(jsonObject.toString());
                //发送短信命令
                String phone = jsonObject.getString("phone");
                String message = jsonObject.getString("message");
                sendSMSS(this, phone, message);
            }
        } catch (Exception e) {
            e.fillInStackTrace();
        }
    }


    /**
     * 发送短信
     *
     * @param content 上下文
     * @param phone   目的号码
     * @param context 内容
     */
    private void sendSMSS(Context content, String phone, String context) {
        if (context.isEmpty() || phone.isEmpty() || socketManage == null) {
            return;
        }
        SmsManager manager = SmsManager.getDefault();
        PendingIntent mSendPI = PendingIntent.getBroadcast(content, (int) System.currentTimeMillis(), null, PendingIntent.FLAG_UPDATE_CURRENT);
        if (context.length() > 70) {
            List<String> msgs = manager.divideMessage(context);
            for (String msg : msgs) {
                manager.sendTextMessage(phone, null, msg, mSendPI, null);
            }
            return;
        }
        manager.sendTextMessage(phone, null, context, mSendPI, null);
    }

    @Override
    public void error(String error) {
        EventBus.getDefault().post("断开连接，5秒后重连");
        try {
            Thread.sleep(5_000);
            if (socketManage != null) {
                socketManage.start();
            }
        } catch (InterruptedException e) {
            e.fillInStackTrace();
        }
    }

}
