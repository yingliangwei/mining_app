package com.mining.listen.network;


import com.alibaba.fastjson2.JSONObject;
import com.xframe.network.OnData;
import com.xframe.network.SocketManage;

public class SocketOk implements OnData {
    private String str;
    private OnData onData;

    public SocketOk(String str, OnData onData, int position) {
        this.str = str;
        this.onData = onData;
        SocketManage.init(this, position);
    }

    @Override
    public void connect(SocketManage socketManage) {
        JSONObject jsonObject = JSONObject.parseObject(str);
        jsonObject.put("position", socketManage.getPosition());
        socketManage.print(jsonObject.toString());
    }

    @Override
    public void handle(String ds) {
        onData.handle(ds);
    }
}
