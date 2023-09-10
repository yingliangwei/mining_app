package com.xframe.network;

public class SocketOk implements OnData {
    private String str;
    private OnData onData;

    public SocketOk(String str, OnData onData) {
        this.str = str;
        this.onData = onData;
        SocketManage.init(this);
    }

    @Override
    public void connect(SocketManage socketManage) {
        socketManage.print(str);
    }

    @Override
    public void handle(String ds) {
        onData.handle(ds);
    }
}
