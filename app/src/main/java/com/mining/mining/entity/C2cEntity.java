package com.mining.mining.entity;

public class C2cEntity {

    /**
     * article : 10
     * id : 3
     * usdt : 0.20
     * user_id : 1
     */

    private String name;
    private String article;
    private String id;
    private String usdt;
    private String time;
    private String user_id;
    private int is_authentication;

    public void setTime(String time) {
        this.time = time;
    }

    public String getTime() {
        return time;
    }

    public int getIs_authentication() {
        return is_authentication;
    }

    public void setIs_authentication(int is_authentication) {
        this.is_authentication = is_authentication;
    }

    public String getArticle() {
        return article;
    }

    public void setArticle(String article) {
        this.article = article;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
