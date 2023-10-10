package com.mining.mining.entity;

public class MiningDataEntity {
    /**
     * day_gem : 24.00000000
     * id : 2
     * mining_gem : 0.00000000
     * mining_size : 0
     * mining_usdt : 0.00000000
     * pit_id : 1
     * superposition : 1
     * time : 2023-09-17 22:33:59
     * type : 1
     * user_id : 6
     * mining_remaining: 0
     */

    private String day_gem;
    private String id;
    private String mining_gem;
    private String mining_size;
    private String mining_usdt;
    private String pit_id;
    private String superposition;
    private String time;
    private String is_permanent;
    private String user_id;
    private String isSuperposition;
    private int mining_remaining;
    private String is_usdt;

    public String getIs_usdt() {
        return is_usdt;
    }

    public void setIs_usdt(String is_usdt) {
        this.is_usdt = is_usdt;
    }

    public int getMining_remaining() {
        return mining_remaining;
    }

    public void setMining_remaining(int mining_remaining) {
        this.mining_remaining = mining_remaining;
    }


    public String getIsSuperposition() {
        return isSuperposition;
    }

    public void setIsSuperposition(String isSuperposition) {
        this.isSuperposition = isSuperposition;
    }

    public String getDay_gem() {
        return day_gem;
    }

    public void setDay_gem(String day_gem) {
        this.day_gem = day_gem;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMining_gem() {
        return mining_gem;
    }

    public void setMining_gem(String mining_gem) {
        this.mining_gem = mining_gem;
    }

    public String getMining_size() {
        return mining_size;
    }

    public void setMining_size(String mining_size) {
        this.mining_size = mining_size;
    }

    public String getMining_usdt() {
        return mining_usdt;
    }

    public void setMining_usdt(String mining_usdt) {
        this.mining_usdt = mining_usdt;
    }

    public String getPit_id() {
        return pit_id;
    }

    public void setPit_id(String pit_id) {
        this.pit_id = pit_id;
    }

    public String getSuperposition() {
        return superposition;
    }

    public void setSuperposition(String superposition) {
        this.superposition = superposition;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getIs_permanent() {
        return is_permanent;
    }

    public void setIs_permanent(String type) {
        this.is_permanent = type;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }
}
