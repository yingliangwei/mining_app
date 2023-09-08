package com.xframe.widget.recycler;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class LinearDividerDecoration extends RecyclerView.ItemDecoration {
    private int mDividerHeight = 40;
    private int mDividerColor = 0xbfbfbf;
    private Paint mPaint;
    private int mOrientation;

    public LinearDividerDecoration(@RecyclerView.Orientation int orientation) {
        this.mOrientation = orientation;
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(mDividerColor);
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        if (mOrientation == LinearLayoutManager.VERTICAL) {
            outRect.set(0, 0, 0, mDividerHeight);
        } else {
            outRect.set(0, 0, mDividerHeight, 0);
        }
    }

    @Override
    public void onDraw(@NonNull Canvas canvas, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        canvas.save();
        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; ++i) {
            View childAt = parent.getChildAt(i);
            int left = 0;
            int right = parent.getWidth();
            int top = childAt.getBottom();
            int bottom = childAt.getBottom() + mDividerHeight;
            canvas.drawRect(left, top, right, bottom, mPaint);
        }
        canvas.restore();
    }

}
