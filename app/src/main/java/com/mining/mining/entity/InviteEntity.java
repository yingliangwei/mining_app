package com.mining.mining.entity;

public class InviteEntity {
    /**
     * code : 0
     * id : 1
     * invite_id : 4
     * name : 0d13eecbf0749bba5503a0301b400656
     * time : 2023-09-11 19:00:07
     * user_id : 5
     */

    private String code;
    private String id;
    private String invite_id;
    private String name;
    private String time;
    private String user_id;
    private String gem;

    public void setGem(String gem) {
        this.gem = gem;
    }


    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getInvite_id() {
        return invite_id;
    }

    public void setInvite_id(String invite_id) {
        this.invite_id = invite_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getGem() {
        return gem;
    }
}
