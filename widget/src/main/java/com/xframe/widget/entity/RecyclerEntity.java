package com.xframe.widget.entity;

import androidx.annotation.IdRes;

public class RecyclerEntity {
    public String name;
    @IdRes
    public int drawable;
    public String src_url;
    public String text;

    public RecyclerEntity(String name, int drawable, String text, String src_url) {
        this.name = name;
        this.drawable = drawable;
        this.text = text;
        this.src_url = src_url;
    }
}
