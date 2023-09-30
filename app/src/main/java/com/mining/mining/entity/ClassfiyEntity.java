package com.mining.mining.entity;

public class ClassfiyEntity {
    private String context;
    private String key;

    public void setKey(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public ClassfiyEntity(String context) {
        this.context = context;
    }

    public ClassfiyEntity(String context, String key) {
        this.context = context;
        this.key = key;
    }


    public void setContext(String context) {
        this.context = context;
    }

    public String getMainName() {
        return context;
    }
}
