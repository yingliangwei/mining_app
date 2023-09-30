package com.mining.press.entity;

public class PressEntity {
    /**
     * bill_k : 0
     * id : 10
     * k : 0
     * stone : 0
     */

    private String bill_k = "选择卡";
    private String id = "期数";
    private String k = "K卡";
    private String stone = "矿石";
    private String stone_x = "我的收获";

    public PressEntity() {

    }

    public void setStone_x(String stone_x) {
        this.stone_x = stone_x;
    }

    public String getStone_x() {
        return stone_x;
    }

    public PressEntity(String bill_k, String id, String k, String stone) {
        this.bill_k = bill_k;
        this.id = id;
        this.k = k;
        this.stone = stone;
    }

    public String getBill_k() {
        return bill_k;
    }

    public void setBill_k(String bill_k) {
        this.bill_k = bill_k;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getK() {
        return k;
    }

    public void setK(String k) {
        this.k = k;
    }

    public String getStone() {
        return stone;
    }

    public void setStone(String stone) {
        this.stone = stone;
    }
}
