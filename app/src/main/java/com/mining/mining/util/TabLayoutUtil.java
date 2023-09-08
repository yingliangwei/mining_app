package com.mining.mining.util;


import android.content.res.ColorStateList;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;

import java.util.Objects;

public class TabLayoutUtil {
    private final TabLayout tabLayout;
    private boolean enableChangeSize = false;
    private int unSelectSize = 15,selectSize = 20;

    private TabLayoutUtil(TabLayout tabLayout) {
        this.tabLayout = tabLayout;
    }

    public static TabLayoutUtil build(TabLayout tabLayout){
        return new TabLayoutUtil(tabLayout);
    }

    public TabLayoutUtil enableChangeStyle() {
        this.enableChangeSize = true;
        ColorStateList colorStateList = tabLayout.getTabTextColors();
        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            assert tab != null;
            String tabStr = Objects.requireNonNull(tab.getText()).toString();
            if(tab.getCustomView() == null || !(tab.getCustomView() instanceof TextView)){
                TextView tv = new TextView(tabLayout.getContext());
                //使用默认TabItem样式时，需要添加LayoutParams，否则会出现Tab文字不居中问题
                ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(-2,-2);
                tv.setLayoutParams(params);
                tv.setTextColor(colorStateList);
                tv.setText(tabStr);
                tv.setTextSize(tab.isSelected()?selectSize:unSelectSize);
                tab.setCustomView(tv);
            }
        }
        return this;
    }

    public TabLayoutUtil setTextSizes(int selectSize,int unSelectSize) {
        this.selectSize = selectSize;
        this.unSelectSize = unSelectSize;
        return this;
    }


    public TabLayoutUtil setOnSelectedListener(OnSelectedListener onSelectedListener) {
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                String tabStr = Objects.requireNonNull(tab.getText()).toString();
                if(onSelectedListener!=null){
                    onSelectedListener.onSelected(tabStr);
                }
                if(enableChangeSize){
                    TextView tv = (TextView) tab.getCustomView();
                    assert tv != null;
                    tv.setTextSize(selectSize);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                if(enableChangeSize){
                    TextView tv = (TextView) tab.getCustomView();
                    assert tv != null;
                    tv.setTextSize(unSelectSize);
                }
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        return this;
    }

    public interface OnSelectedListener{
        void onSelected(String tabStr);
    }
}