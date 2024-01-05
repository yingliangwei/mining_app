package com.mining.mining.entity;

public class OrderManageEntity {
    private String c2c_gem_id;
    private String gem;
    private String id;
    private Integer is_authentication;
    private String name;
    private String number;
    private String time;
    private String user_id;
    private String premium;
    private String usdt;

    public String getC2cGemId() {
        return c2c_gem_id;
    }

    public void setC2cGemId(String c2cGemId) {
        this.c2c_gem_id = c2cGemId;
    }

    public String getGem() {
        return gem;
    }

    public void setGem(String gem) {
        this.gem = gem;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
        //notifyPropertyChanged(BR.id);
    }

    //@Bindable
    public Integer getIsAuthentication() {
        return is_authentication;
    }

    public void setIsAuthentication(Integer isAuthentication) {
        this.is_authentication = isAuthentication;
        // notifyPropertyChanged(BR.isAuthentication);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getUserId() {
        return user_id;
    }

    public void setUserId(String userId) {
        this.user_id = userId;
    }

    public String getPremium() {
        return premium;
    }

    public String getUsdt() {
        return usdt;
    }
}
