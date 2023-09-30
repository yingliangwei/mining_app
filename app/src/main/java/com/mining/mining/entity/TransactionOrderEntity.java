package com.mining.mining.entity;

public class TransactionOrderEntity {
    /**
     * c2c_gem_id : 3
     * commission : 0.00000000
     * gem_balance : 0.00000000
     * gem_size : -6
     * id : 2
     * time : 2023-09-08 01:40:18
     * usdt : 1.20000000
     * usdt_balance : 0.00000000
     * user_id : 5
     */

    private String c2c_gem_id;
    private String commission;
    private String gem_balance;
    private String gem_size;
    private String id;
    private String time;
    private String usdt;
    private String usdt_balance;
    private String user_id;

    public String getC2c_gem_id() {
        return c2c_gem_id;
    }

    public void setC2c_gem_id(String c2c_gem_id) {
        this.c2c_gem_id = c2c_gem_id;
    }

    public String getCommission() {
        return commission;
    }

    public void setCommission(String commission) {
        this.commission = commission;
    }

    public String getGem_balance() {
        return gem_balance;
    }

    public void setGem_balance(String gem_balance) {
        this.gem_balance = gem_balance;
    }

    public String getGem_size() {
        return gem_size;
    }

    public void setGem_size(String gem_size) {
        this.gem_size = gem_size;
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

    public String getUsdt() {
        return usdt;
    }

    public void setUsdt(String usdt) {
        this.usdt = usdt;
    }

    public String getUsdt_balance() {
        return usdt_balance;
    }

    public void setUsdt_balance(String usdt_balance) {
        this.usdt_balance = usdt_balance;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }
}
