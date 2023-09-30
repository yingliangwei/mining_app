package com.mining.mining.entity;

public class MiningEntity {
    /**
     * day : 30
     * day_gem : 21.00000000
     * id : 2
     * mining : {}
     * mining_gem : 500.00000000
     * mining_size : 1
     * moon_gem : 630.00000000
     * tab_id : 0
     */

    private String day;
    private String day_gem;
    private String id;
    private String mining_gem;
    private String mining_size;
    private String moon_gem;
    private String tab_id;
    private String _mining;

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

    public String getMining() {
        return _mining;
    }

    public void setMining(String mining) {
        this._mining = mining;
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

    public String getTab_id() {
        return tab_id;
    }

    public void setTab_id(String tab_id) {
        this.tab_id = tab_id;
    }
}
