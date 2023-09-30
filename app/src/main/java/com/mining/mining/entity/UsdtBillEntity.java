package com.mining.mining.entity;

public class UsdtBillEntity {
    /**
     * balance : 0.00000000
     * commission : 0.00000000
     * id : 1
     * name : 充值 USDT
     * name_x : 充值
     * time : 2023-09-12 10:26:12
     * usdt : 1.00000000
     * user_id : 4
     */

    private String balance;
    private String commission;
    private String id;
    private String name;
    private String name_x;
    private String time;
    private String usdt;
    private String user_id;

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    public String getCommission() {
        return commission;
    }

    public void setCommission(String commission) {
        this.commission = commission;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName_x() {
        return name_x;
    }

    public void setName_x(String name_x) {
        this.name_x = name_x;
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

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }
}
