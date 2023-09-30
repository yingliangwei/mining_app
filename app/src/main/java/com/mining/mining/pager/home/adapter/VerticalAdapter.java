package com.mining.mining.pager.home.adapter;

import com.mining.mining.entity.ClassfiyEntity;

import java.util.List;

import q.rorbin.verticaltablayout.adapter.SimpleTabAdapter;
import q.rorbin.verticaltablayout.widget.QTabView;
import q.rorbin.verticaltablayout.widget.TabView;

public class VerticalAdapter extends SimpleTabAdapter {
    List<ClassfiyEntity> menus;
    public VerticalAdapter(List<ClassfiyEntity> menus) {
        this.menus = menus;
    }
    @Override
    public int getCount() {
        return menus.size();
    }
    @Override
    public TabView.TabBadge getBadge(int position) {
        return null;
    }
    @Override
    public TabView.TabIcon getIcon(int position) {
        return  null;
    }
    @Override
    public TabView.TabTitle getTitle(int position) {
        ClassfiyEntity classfiy = menus.get(position);
        //自定义Tab选择器的字体大小颜色
        return new QTabView.TabTitle.Builder()
                .setTextColor(0xFFffffff,0xFF2A2323)
                .setTextSize(14)
                .setContent(classfiy.getMainName())
                .build();
    }
    @Override
    public int getBackground(int position) {
        return -1;
    }
}
