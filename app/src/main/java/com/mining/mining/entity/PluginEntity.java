package com.mining.mining.entity;

public class PluginEntity {
    /**
     * download :
     * download_size : 10
     * id : 1
     * image : https://img2.baidu.com/it/u=1259944612,3555294969&fm=253&fmt=auto&app=138&f=JPEG?w=500&h=500
     * name : 压卡牌3
     * tab_id : 1
     * time : 2023-09-13 15:38:24
     * version : 1
     */

    private String download;
    private String download_size;
    private String id;
    private String image;
    private String name;
    private String tab_id;
    private String time;
    private String version;

    private String json;

    public void setJson(String json) {
        this.json = json;
    }

    public String getJson() {
        return json;
    }

    public String getDownload() {
        return download;
    }

    public void setDownload(String download) {
        this.download = download;
    }

    public String getDownload_size() {
        return download_size;
    }

    public void setDownload_size(String download_size) {
        this.download_size = download_size;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
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

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
