package com.xframe.widget;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.widget.ImageView;

import java.util.Random;

/**
 * @Description 随机色
 * @Package Fallenangel.AngelCompat.widget
 * @Author Angel
 * @Date 02-13-2022 周日 18:32
 */
public class RandomColorImageView extends ImageView {
    public RandomColorImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // 随机颜色
        Random random = new Random();
        int ranColor = 0xff000000 | random.nextInt(0x00ffffff);
        setImageDrawable(new ColorDrawable(ranColor));
        setBackgroundColor(ranColor);
    }
}
