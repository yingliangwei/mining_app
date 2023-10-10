package com.mining.mining.entity;

public class MiningEntity {

    /**
     * day : 30
     * day_gem : 0.24000000
     * id : 1
     * is_permanent : 1
     * is_usdt : 1
     * mining_gem : 50.00000000
     * mining_size : 5
     * moon_gem : 0.00000000
     * name : 新手宝石矿池
     * tab_id : 1
     */

    private String day;
    private String day_gem;
    private String id;
    private String is_permanent;
    private String is_usdt;
    private String mining_gem;
    private String mining_size;
    private String moon_gem;
    private String name;
    private String tab_id;
    private String _mining;
    private int isMining;
    private int isCard;

    public int getIsCard() {
        return isCard;
    }

    public void setIsCard(int isCard) {
        this.isCard = isCard;
    }

    public int getIsMining() {
        return isMining;
    }

    public void setIsMining(int isMining) {
        this.isMining = isMining;
    }

    public String get_mining() {
        return _mining;
    }

    public void set_mining(String _mining) {
        this._mining = _mining;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
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

    public String getIs_permanent() {
        return is_permanent;
    }

    public void setIs_permanent(String is_permanent) {
        this.is_permanent = is_permanent;
    }

    public String getIs_usdt() {
        return is_usdt;
    }

    public void setIs_usdt(String is_usdt) {
        this.is_usdt = is_usdt;
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

    public String getMoon_gem() {
        return moon_gem;
    }

    public void setMoon_gem(String moon_gem) {
        this.moon_gem = moon_gem;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTab_id() {
        return tab_id;
    }

    public void setTab_id(String tab_id) {
        this.tab_id = tab_id;
    }
}
