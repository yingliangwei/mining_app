package com.mining.superbull.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.alibaba.fastjson2.JSONObject;

public class SharedUtil {
    private final SharedPreferences sharedPreferences;

    public SharedUtil(Context context) {
        sharedPreferences = context.getSharedPreferences("user", Context.MODE_PRIVATE);
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
