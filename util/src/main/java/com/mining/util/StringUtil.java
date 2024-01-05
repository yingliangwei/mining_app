package com.mining.util;

import org.json.JSONException;
import org.json.JSONObject;

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

    public static boolean isPowerOf20(int num) {
        return num % 20 == 0;
    }

    public static String toRe(String usdt) {
        if (usdt == null || usdt.equals("0")) {
            return "0";
        }
        if (!usdt.contains(".")) {
            return usdt;
        }
        String cleanedNumStr = usdt.replaceAll("0+$", "");
        if (cleanedNumStr.endsWith(".")) {
            cleanedNumStr = cleanedNumStr.substring(0, cleanedNumStr.length() - 1);
        }
        return cleanedNumStr;
    }

    public static String getStringStart(String str) {
        if (str == null) {
            return "n";
        }
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (!Character.isWhitespace(c)) {
                return String.valueOf(c);
            }
        }
        return "n";
    }
}
