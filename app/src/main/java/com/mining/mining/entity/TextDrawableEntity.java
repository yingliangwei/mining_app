package com.mining.mining.entity;

import android.graphics.drawable.Drawable;

public class TextDrawableEntity {
    public String name;
    public Drawable drawable;
    public int i = 0;

    public TextDrawableEntity(String name, Drawable drawable) {
        this.name = name;
        this.drawable = drawable;
    }

    public TextDrawableEntity(String name, Drawable drawable, int i) {
        this.name = name;
        this.i = i;
        this.drawable = drawable;
    }
}
