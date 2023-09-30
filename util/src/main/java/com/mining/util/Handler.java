package com.mining.util;

import android.os.Looper;
import android.os.Message;

public class Handler extends android.os.Handler implements OnHandler {
    private OnHandler handler;

    public Handler(OnHandler handler) {
        this.handler = handler;
    }

    public Handler(Looper looper, OnHandler onHandler) {
        super(looper);
        this.handler = onHandler;
    }

    public Handler(Looper looper) {
        super(looper);
    }

    public void sendString(String str) {
        Message message = new Message();
        message.obj = str;
        sendMessage(0, str);
    }


    public void sendMessage(int w, String str) {
        Message message = new Message();
        message.obj = str;
        message.what = w;
        sendMessage(message);
    }

    @Override
    public void handleMessage(Message msg) {
        if (msg.obj instanceof String) {
            handleMessage((String) msg.obj);
            handleMessage(msg.what, (String) msg.obj);
        }
    }

    @Override
    public void handleMessage(String str) {
        if (handler != null) {
            handler.handleMessage(str);
        }
    }

    @Override
    public void handleMessage(int w, String str) {
        if (handler != null) {
            handler.handleMessage(w, str);
        }
    }
}
