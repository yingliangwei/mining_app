package com.mining.mining.entity;

public class MessageEvent {
    public int w;
    public String message;

    public String aClass;

    public MessageEvent(int w) {
        setW(w);
    }

    public MessageEvent(int w, String message) {
        setW(w);
        setMessage(message);
    }

    public MessageEvent(int w, Class<?> message) {
        setW(w);
        setaClass(message);
    }

    public MessageEvent(Class<?> message) {
        setaClass(message);
    }

    public MessageEvent(int w, Class<?> aClass, String message) {
        setW(w);
        setMessage(message);
        setaClass(aClass);
    }


    public boolean isClass(Class<?> aClass) {
        System.out.println(aClass.getName());
        return aClass.getName().equals(aClass.getName());
    }

    public void setaClass(Class<?> aClass) {
        this.aClass = aClass.getName();
    }

    public String getaClass() {
        return aClass;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setW(int w) {
        this.w = w;
    }

    public int getW() {
        return w;
    }
}
