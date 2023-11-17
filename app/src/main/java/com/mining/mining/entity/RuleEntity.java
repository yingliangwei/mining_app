package com.mining.mining.entity;

public class RuleEntity {
    /**
     * id : 1
     * message : 新手矿池，是通过填写邀请码渠道自动获得该矿池，通过实名制以后即可挖矿
     * mining_tab_id : 1
     * time : 2023-10-11 08:22:04
     * title : 新手矿池获得渠道
     * is_title : 0
     */

    private String id;
    private String message;
    private String mining_tab_id;
    private String time;
    private String title;
    private String is_title;

    public void setIs_title(String is_title) {
        this.is_title = is_title;
    }

    public String getIs_title() {
        return is_title;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMining_tab_id() {
        return mining_tab_id;
    }

    public void setMining_tab_id(String mining_tab_id) {
        this.mining_tab_id = mining_tab_id;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
