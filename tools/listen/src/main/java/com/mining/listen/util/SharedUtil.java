package com.mining.listen.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.alibaba.fastjson2.JSONObject;
import com.mining.listen.activity.LoginActivity;

public class SharedUtil {
    private final SharedPreferences sharedPreferences;
    private final Activity context;

    public SharedUtil(Activity context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences("user", Context.MODE_PRIVATE);
    }

    public boolean isLogin() {
        String id = sharedPreferences.getString("id", null);
        String _key = sharedPreferences.getString("_key", null);
        return id != null && _key != null;
    }

    public void clear() {
        sharedPreferences.edit().clear().apply();
        context.startActivity(new Intent(context, LoginActivity.class));
        context.finish();
    }

    public JSONObject getLogin() {
        String id = sharedPreferences.getString("id", null);
        String _key = sharedPreferences.getString("_key", null);
        if (id == null || _key == null) {
            return null;
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("id", id);
        jsonObject.put("_key", _key);
        return jsonObject;
    }

    public JSONObject getLogin(int type, int code) {
        String id = sharedPreferences.getString("id", null);
        String _key = sharedPreferences.getString("_key", null);
        if (id == null || _key == null) {
            context.startActivity(new Intent(context, LoginActivity.class));
            context.finish();
            return null;
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("id", id);
        jsonObject.put("type", type);
        jsonObject.put("code", code);
        jsonObject.put("_key", _key);
        return jsonObject;
    }

    public JSONObject getLogin(int type, int code, int start, int end) {
        String id = sharedPreferences.getString("id", null);
        String _key = sharedPreferences.getString("_key", null);
        if (id == null || _key == null) {
            return null;
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("id", id);
        jsonObject.put("type", type);
        jsonObject.put("code", code);
        jsonObject.put("_key", _key);
        jsonObject.put("start", start);
        jsonObject.put("end", end);
        return jsonObject;
    }
}
