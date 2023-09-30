package com.mining.util;

public class MessageEvent {
    private final String message;
    /**
     * 1=刷新宝石余额
     * 2=切换到主页面
     * 3=刷新usdt
     */
    private final int w;

    public MessageEvent(int w, String message) {
        this.message = message;
        this.w = w;
    }

    public int getW() {
        return w;
    }

    public String getMessage() {
        return message;
    }
}
