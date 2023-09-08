package com.xframe.widget;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.viewpager2.widget.ViewPager2;

import com.xframe.widget.entity.TextImageEntity;

import java.util.List;

public class BottomNavigationView extends LinearLayout implements View.OnClickListener {
    private List<TextImageEntity> text;
    private ViewPager2 viewPager2;
    private final Context context;

    public BottomNavigationView(Context context) {
        super(context);
        this.context = context;
        setOrientation(HORIZONTAL);
        setBackgroundColor(Color.WHITE);
    }

    public BottomNavigationView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        setOrientation(HORIZONTAL);
        setBackgroundColor(Color.WHITE);
    }


    private void initTable() {
        for (int i = 0; i < text.size(); i++) {
            LinearLayout linearLayout = new LinearLayout(context);
            linearLayout.setClickable(true);
            linearLayout.setGravity(Gravity.CENTER);
            LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            layoutParams.weight = 1;
            linearLayout.setLayoutParams(layoutParams);
            View view = LayoutInflater.from(context).inflate(R.layout.item_bottom_tab, new LinearLayout(context));
            setTabLeText(view, i);
            view.setId(i);
            view.setOnClickListener(this);
            linearLayout.addView(view);
            addView(linearLayout);
        }
    }

    private void setTabLeText(View binding, int position) {
        TextImageEntity entity = text.get(position);
        TextView textView = binding.findViewById(R.id.text);
        textView.setText(entity.text);
        ImageView imageView = binding.findViewById(R.id.image);
        imageView.setImageDrawable(entity.drawable);
    }

    public void CurrentItem(int id) {
        for (int i = 0; i < text.size(); i++) {
            LinearLayout linearLayout = findViewById(i);
            TextView textView = linearLayout.findViewById(R.id.text);
            if (i == id) {
                textView.setTextColor(context.getColor(R.color.purple_200));
            } else {
                TypedValue tv = new TypedValue();
                context.getTheme().resolveAttribute(android.R.attr.textColorSecondary, tv, true);
                int holyColor = context.getColor(tv.resourceId);
                textView.setTextColor(holyColor);
            }
        }
    }

    public void setViewPager2(List<TextImageEntity> text, ViewPager2 viewPager2) {
        this.text = text;
        this.viewPager2 = viewPager2;
        initTable();
        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                CurrentItem(position);
            }
        });
    }

    @Override
    public void onClick(View v) {
        //menu点击事件
        viewPager2.setCurrentItem(v.getId());
        CurrentItem(v.getId());
    }
}
