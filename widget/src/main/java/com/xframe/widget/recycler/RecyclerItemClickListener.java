package com.xframe.widget.recycler;

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

public class RecyclerItemClickListener implements RecyclerView.OnItemTouchListener {

    private final GestureDetector mGestureDetector;

    /**
     * 被选择的view
     */
    private View selectView;
    /**
     * 被选择view的position
     */
    private int selectPosition;

    public RecyclerItemClickListener(Context context, final OnItemClickListener.Normal mListener) {

        mGestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            /**
             * 点击
             */
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                if (selectView != null && mListener != null) {
                    mListener.onItemClick(selectView, selectPosition);
                    return true;
                }
                return super.onSingleTapUp(e);
            }

            /**
             * 长按
             */
            @Override
            public void onLongPress(MotionEvent e) {
                if (selectView != null && mListener != null) {
                    mListener.onItemLongClick(selectView, selectPosition);
                }
            }

        });
    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
        View childView = rv.findChildViewUnder(e.getX(), e.getY());
        if (childView == null) {
            return false;
        }
        selectView = childView;
        selectPosition = rv.getChildAdapterPosition(childView);
        /**
         * 交给手势控制类来处理
         */
        return mGestureDetector.onTouchEvent(e);
    }

    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent e) {

    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

    }

    public interface OnItemClickListener {

        interface Normal {

            default void onItemClick(View view, int position) {

            }

            default void onItemLongClick(View view, int position) {

            }

            class Builder implements Normal {

                @Override
                public void onItemClick(View view, int position) {

                }

                @Override
                public void onItemLongClick(View view, int position) {

                }
            }
        }


    }
}