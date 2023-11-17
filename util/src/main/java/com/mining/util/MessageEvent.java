package com.mining.util;

public class MessageEvent {
    private final String message;
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
