package com.mining.mining.entity;

import com.alibaba.fastjson2.JSONObject;

public class WithdrawalEntity {
    /**
     * address : 0
     * id : 1
     * time : 2023-10-17 15:16:32
     * type : 0
     * usdt : 0.00000000
     * user_id : 12
     */

    private String address;
    private String id;
    private String time;
    private String type;
    private String usdt;
    private String user_id;
    private JSONObject _user;
    private String text;

    public void setText(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setUser(JSONObject user) {
        this._user = user;
    }

    public JSONObject getUser() {
        return _user;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUsdt() {
        return usdt;
    }

    public void setUsdt(String usdt) {
        this.usdt = usdt;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }
}
