package com.mining.mining.entity;

public class NewsEntity {
    /**
     * id : 1
     * isNew : 0
     * name : 测试公告
     * time : 2023-10-05 09:26:24
     */

    private String id;
    private int isNew;
    private String name;
    private String time;
    private String title;

    public String json;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getIsNew() {
        return isNew;
    }

    public void setIsNew(int isNew) {
        this.isNew = isNew;
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
}
