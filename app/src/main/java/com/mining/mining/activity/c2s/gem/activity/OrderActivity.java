package com.mining.mining.activity.c2s.gem.activity;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.mining.mining.activity.c2s.gem.activity.order.OrderManagePager;
import com.mining.mining.adapter.PagerAdapter;
import com.mining.mining.adapter.RecyclerAdapter;
import com.mining.mining.databinding.ActivityOrderBinding;
import com.mining.util.StatusBarUtil;

import java.util.ArrayList;
import java.util.List;

public class OrderActivity extends AppCompatActivity implements TabLayout.OnTabSelectedListener {
    private ActivityOrderBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtil.setImmersive(this, true);
        binding = ActivityOrderBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initToolbar();
        initTab();
        initPager();
    }

    private void initPager() {
        List<RecyclerAdapter> recyclerAdapters = new ArrayList<>();
        recyclerAdapters.add(new OrderManagePager(this, 11));
        recyclerAdapters.add(new OrderManagePager(this, 12));
        binding.pager.setAdapter(new PagerAdapter(recyclerAdapters));
        binding.pager.setOffscreenPageLimit(recyclerAdapters.size());
        binding.pager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                TabLayout.Tab tab = binding.tab.getTabAt(position);
                if (tab == null) {
                    return;
                }
                tab.select();
            }
        });
    }

    private void initTab() {
        binding.tab.addTab(binding.tab.newTab().setText("出售").setId(0));
        binding.tab.addTab(binding.tab.newTab().setText("回收").setId(1));
        binding.tab.addOnTabSelectedListener(this);
    }


    private void initToolbar() {
        binding.toolbar.setNavigationOnClickListener(v -> finish());
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        binding.pager.setCurrentItem(tab.getId());
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {
    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {
    }
}
