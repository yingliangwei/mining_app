package com.mining.sms.sms;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.provider.Telephony;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;
import android.util.Log;


//来了短信会通知
public class SmsReceiver extends BroadcastReceiver {
    private Context context;
    private String text;

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        SmsMessage[] smsMessages = Telephony.Sms.Intents.getMessagesFromIntent(intent);
        if (smsMessages == null) {
            return;
        }
        String senderNumber = smsMessages[0].getOriginatingAddress();
        String messages = getSmsMessages(smsMessages);
        String number = getNumber(context);
        if (number == null) {
            String TAG = "SmsReceiver";
            Log.e(TAG, "number null");
            return;
        }
    }


    //获取短信
    private String getSmsMessages(SmsMessage[] smsMessages) {
        // 组装短信内容
        StringBuilder text = new StringBuilder();
        for (SmsMessage smsMessage : smsMessages) {
            text.append(smsMessage.getMessageBody());
        }
        return text.toString();
    }

    @SuppressLint("HardwareIds")
    private String getNumber(Context context) {
        //获取手机号码，有可能获取不到
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (context.checkSelfPermission(Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED && context.checkSelfPermission(Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            return null;
        }
        return tm.getLine1Number();
    }
}
