package com.mining.mining.util;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class ApiUtil {
    public static String getIp() {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("https://www.ip.cn/api/index?ip&type=0")
                .build();
        try {
            Response response = client.newCall(request).execute();
            ResponseBody body = response.body();
            if (body != null) {
                return body.string();
            }
        } catch (IOException e) {
            e.fillInStackTrace();
        }
        return "";
    }
}
