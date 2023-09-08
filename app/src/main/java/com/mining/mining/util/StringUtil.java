package com.mining.mining.util;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;

public class StringUtil {
    public static boolean isJSON(String json) {
        try {
            new JSONObject(json);
            // 字符串是JSON格式
            return true;
        } catch (JSONException e) {
            // 字符串不是JSON格式
            return false;
        }
    }

    public static String getStringStart(String str) {
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (!Character.isWhitespace(c)) {
                return String.valueOf(c);
            }
        }
        return "n";
    }
}
