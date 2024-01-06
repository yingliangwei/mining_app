package com.xframe.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.Arrays;

public class LineChart extends View {

    private int width;
    private int height;
    private float[] yValues = {0, 0};
    private final Paint paint = new Paint();

    public LineChart(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        // 计算图表的宽度和高度
        width = getMeasuredWidth();
        height = getMeasuredHeight();
    }


    public void setValues(float[] values) {
        this.yValues = values;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        width = getMeasuredWidth();
        height = getMeasuredHeight();
        int padding = 10;
        // 计算每个数据点的宽度
        float xScale = (float) (width - 2 * padding) / (yValues.length - 1);

        // 找出yValues数组中的最大值和最小值
        float minValue = Float.MAX_VALUE;
        float maxValue = Float.MIN_VALUE;
        for (float value : yValues) {
            if (value < minValue) {
                minValue = value;
            }
            if (value > maxValue) {
                maxValue = value;
            }
        }

        // 绘制折线
        paint.setColor(Color.WHITE);
        paint.setStrokeWidth(4);
        paint.setStyle(Paint.Style.STROKE);
        for (int i = 0; i < yValues.length - 1; i++) {
            float startX = padding + i * xScale;
            float startY = height - ((yValues[i] - minValue) / (maxValue - minValue)) * (height - 2 * padding);
            float endY = height - ((yValues[i + 1] - minValue) / (maxValue - minValue)) * (height - 2 * padding);
            float endX = padding + (i + 1) * xScale;
            canvas.drawLine(startX, startY, endX, endY, paint);
        }
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        // 设置视图的大小
        int width = MeasureSpec.getSize(widthMeasureSpec);
        setMeasuredDimension(width, 150);
    }
}
