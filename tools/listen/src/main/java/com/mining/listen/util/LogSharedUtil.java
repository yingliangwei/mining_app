package com.mining.listen.util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.alibaba.fastjson2.JSONObject;

import java.util.Map;

public class LogSharedUtil {
    private final SharedPreferences sharedPreferences;
    private final Activity context;

    public LogSharedUtil(Activity context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences("log", Context.MODE_PRIVATE);
    }

    public Map<?, ?> getAll() {
        return sharedPreferences.getAll();
    }

    public void put(String time, String json) {
        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.putString(time, json);
        edit.apply();
    }

    public void put(String time, String json, String text) {
        JSONObject jsonObject = JSONObject.parseObject(json);
        jsonObject.put("text", text);
        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.putString(time, jsonObject.toString());
        edit.apply();
    }
}
