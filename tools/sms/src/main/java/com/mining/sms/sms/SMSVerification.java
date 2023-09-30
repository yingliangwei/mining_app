package com.mining.sms.sms;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import org.json.JSONObject;

//短信发送状态
public class SMSVerification extends BroadcastReceiver  {
    private String json;

    @Override
    public void onReceive(Context context, Intent intent) {
        String id = intent.getStringExtra("id");
        Bundle extras = intent.getExtras();
        if (extras == null) {
            return;
        }
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("type", "1");
            jsonObject.put("id", id);
            jsonObject.put("code", getCode(String.valueOf(getResultCode())));
        } catch (Exception e) {
            e.fillInStackTrace();
        }
    }


    private String getCode(String resultCode) {
        if (resultCode.equals("-1")) {
            return "1";
        } else {
            return "2";
        }
    }


}
