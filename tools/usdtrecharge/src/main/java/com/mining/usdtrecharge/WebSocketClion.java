package com.mining.usdtrecharge;

import android.os.Handler;
import android.os.Message;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.xframe.network.OnData;
import com.xframe.network.SocketOk;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Timer;
import java.util.TimerTask;

public class WebSocketClion extends WebSocketClient implements OnData {
    private final Handler handler;

    public WebSocketClion(URI serverUri, Handler handler) {
        super(serverUri);
        this.handler = handler;
        TimerTask runnable = new TimerTask() {
            @Override
            public void run() {
                if (isOpen()) {
                    send("ping");
                }
            }
        };
        new Timer().schedule(runnable, 0, 10_000);
    }

    @Override
    public void onOpen(ServerHandshake handshake) {
        try {
            send(JsonUtil.getUser().toString());
        } catch (UnsupportedEncodingException | NoSuchAlgorithmException | InvalidKeyException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onMessage(String message) {
        if (message.equals("pong")) {
            return;
        }
        JSONObject jsonObject = JSONObject.parseObject(message);
        if (jsonObject == null) {
            return;
        }
        printf(message);
        String event = jsonObject.getString("event");
        if (event == null) {
            JSONObject arg = jsonObject.getJSONObject("arg");
            if (arg != null) {
                JSONArray data = jsonObject.getJSONArray("data");
                JSONObject _data = data.getJSONObject(0);
                String channel = arg.getString("channel");
                String uid = arg.getString("uid");
                _data.put("user_uid", uid);
                _data.put("type",5);
                _data.put("code",1);
                new SocketOk(_data.toString(), this);
            }
            return;
        }
        if (event.equals("login")) {
            send(JsonUtil.getRecharge().toString());
        }
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        //  printf("close code=" + code + " reason=" + reason + " remote=" + remote);
        // printf("close connect");
        new Thread(this::reconnect).start();
    }

    @Override
    public void onError(Exception ex) {
        //printf("error" + ex.getMessage());
    }

    private void printf(String text) {
        Message message = new Message();
        message.obj = text;
        message.what = 0;
        handler.sendMessage(message);
    }


    @Override
    public void handle(String ds) {
        printf(ds);
    }
}
