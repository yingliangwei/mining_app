package com.xframe.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;

import java.util.Random;

public class MinerView extends ImageView {
    Animation animation1 = new TranslateAnimation(0f, 50f, 0f, 0f);// 第一个参数表示 X 轴的移动距离，第二个参数和第三个参数表示 Y 轴的移动距离

    public MinerView(Context context) {
        super(context);
        setImageDrawable(context.getDrawable(R.mipmap.miner));
        initView(context);
    }

    public MinerView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        setImageDrawable(context.getDrawable(R.mipmap.miner));
        initView(context);
    }


    private void initView(Context context) {
        // 获取屏幕的宽度和高度
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        int screenWidth = displayMetrics.widthPixels;
        int screenHeight = 400;
        // 生成随机数，用于确定 x 坐标
        int x = new Random().nextInt(screenWidth);
        // 生成随机数，用于确定 y 坐标
        int y = new Random().nextInt(screenHeight);
        // 生成随机数，用于确定 z 坐标
        int z = new Random().nextInt();
        setX(x);
        setY(y);
        setZ(z);

        animation1.setDuration(1000); // 动画持续时间，以毫秒为单位
        animation1.setRepeatCount(Animation.INFINITE); // 无限重复
        startAnimation(animation1);
    }

    public void stop() {
        animation1.cancel();
    }
}
