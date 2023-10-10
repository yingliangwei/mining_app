package com.mining.mining.widget;


import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.view.ActionProvider;


public class BadgeActionProvider extends ActionProvider {
    private final Context context;
    private ImageView mIvIcon;
    private TextView mTvBadge;
    // 用来记录是哪个View的点击，这样外部可以用一个Listener接受多个menu的点击。
    private int clickWhat;
    private OnClickListener onClickListener;

    public BadgeActionProvider(Context context) {
        super(context);
        this.context = context;
    }

    @Override
    public View onCreateActionView() {
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(110, 110);
        LinearLayout layout = new LinearLayout(context);
        layout.setLayoutParams(new ViewGroup.LayoutParams(110, 110));
        layout.setRight(5);
        layout.setLeft(5);
        View view = LayoutInflater.from(context).inflate(com.xframe.widget.R.layout.menu_badge_provider, layout, false);

        view.setLayoutParams(layoutParams);
        mIvIcon = (ImageView) view.findViewById(com.xframe.widget.R.id.iv_icon);
        mTvBadge = (TextView) view.findViewById(com.xframe.widget.R.id.tv_badge);
        mTvBadge.setVisibility(View.GONE);
        view.setOnClickListener(onViewClickListener);
        return view;
    }

    public void setIcon(Drawable drawable) {
        mIvIcon.setImageDrawable(drawable);
    }

    public void setNewsVisibility(int size) {
        mTvBadge.setVisibility(size);
    }

    public void setNewsSize(int size) {
        if (size == 0) {
            mTvBadge.setVisibility(View.GONE);
            return;
        }
        mTvBadge.setVisibility(View.VISIBLE);
        if (size >= 11) {
            StringBuilder builder = new StringBuilder();
            builder.append(size);
            builder.append("+");
            mTvBadge.setText(builder);
        } else {
            mTvBadge.setText(String.valueOf(size));
        }
    }

    // 点击处理。
    private final View.OnClickListener onViewClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (onClickListener != null)
                onClickListener.onClick(clickWhat);
        }
    };

    // 外部设置监听。
    public void setOnClickListener(int what, OnClickListener onClickListener) {
        this.clickWhat = what;
        this.onClickListener = onClickListener;
    }

    public interface OnClickListener {
        void onClick(int what);
    }
}