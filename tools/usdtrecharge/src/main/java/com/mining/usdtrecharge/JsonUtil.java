package com.mining.usdtrecharge;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;

import org.java_websocket.util.Base64;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class JsonUtil {

    /**
     * 订阅k线交易
     *
     * @param instId  币名
     * @param channel 时间
     * @return
     */
    public static String getSubscribe(String instId, String channel) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("op", "subscribe");

        JSONArray args = new JSONArray();
        JSONObject args1 = new JSONObject();
        args1.put("channel", channel);
        args1.put("instId", instId.toUpperCase());

        args.add(args1);
        jsonObject.put("args", args);
        return jsonObject.toString();
    }

    /**
     * @param instId id
     * @param side   方向
     * @param sz     张数
     * @return
     */
    public static String place_an_order(String instId, String side, String sz) {
        JSONObject args = new JSONObject();
        //订单方向，buy sell
        args.put("side", side);
        args.put("instId", instId.toUpperCase());
        //交易模式
        //保证金模式 isolated：逐仓 cross： 全仓
        //非保证金模式 cash：现金
        args.put("tdMode", "cross");
        args.put("ordType", "market");
        args.put("clOrdId", "1211");
        args.put("sz", sz);
        JSONArray jsonArray = new JSONArray();
        jsonArray.add(args);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("id", "1512");
        jsonObject.put("op", "order");
        jsonObject.put("args", jsonArray);
        System.out.println(jsonObject);
        return jsonObject.toString();
    }

    /**
     * 用户信息
     *
     * @return
     * @throws UnsupportedEncodingException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     */
    public static JSONObject getUser() throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeyException {
        String time = String.valueOf(System.currentTimeMillis() / 1000);
        JSONArray jsonArray = new JSONArray();
        JSONObject args = new JSONObject();
        args.put("apiKey", "cc7bfdbb-b436-471c-bd8d-73e69abed6ac");
        args.put("passphrase", "QQ2002qq.");
        args.put("timestamp", time);
        args.put("sign", JsonUtil.HmacSHA256(time, "/users/self/verify"));
        jsonArray.add(args);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("op", "login");
        jsonObject.put("args", jsonArray);
        return jsonObject;
    }

    public static JSONObject getRecharge() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("op", "subscribe");
        JSONArray jsonArray = new JSONArray();
        JSONObject args = new JSONObject();
        args.put("channel", "deposit-info");
        jsonArray.add(args);
        jsonObject.put("args", jsonArray);
        return jsonObject;
    }


    public static String HmacSHA256(String timestamp, String requestPath) throws NoSuchAlgorithmException, UnsupportedEncodingException, InvalidKeyException {
        String secret = "A9EE8A2A8641F4EFC2654BC0CB62376F";
        String message = timestamp + "GET" + requestPath;
        Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        sha256_HMAC.init(secretKey);
        byte[] hash = sha256_HMAC.doFinal(message.getBytes(StandardCharsets.UTF_8));
        return Base64.encodeBytes(hash);
    }

}
