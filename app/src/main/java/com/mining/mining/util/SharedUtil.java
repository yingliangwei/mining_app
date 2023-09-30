package com.mining.mining.util;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONException;
import org.json.JSONObject;

public class SharedUtil {
    private final SharedPreferences sharedPreferences;
    private Context context;

    public SharedUtil(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences("user", Context.MODE_PRIVATE);
    }
    public JSONObject getLogin() throws JSONException {
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

    public JSONObject getLogin(int type, int code) throws JSONException {
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

    public JSONObject getLogin(int type, int code, int start, int end) throws JSONException {
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
