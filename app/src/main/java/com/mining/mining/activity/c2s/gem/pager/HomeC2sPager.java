package com.mining.mining.activity.c2s.gem.pager;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.mining.mining.R;
import com.mining.mining.activity.c2s.gem.activity.OrderManagementActivity;
import com.mining.mining.activity.c2s.gem.activity.PledgeActivity;
import com.mining.mining.adapter.PagerAdapter;
import com.mining.mining.adapter.RecyclerAdapter;
import com.mining.mining.databinding.PagerHomeC2sBinding;
import com.mining.mining.pager.holder.ViewHolder;

import java.util.ArrayList;
import java.util.List;

public class HomeC2sPager extends RecyclerAdapter implements TabLayout.OnTabSelectedListener, Toolbar.OnMenuItemClickListener {
    private PagerHomeC2sBinding binding;
    private final Activity activity;
    private final List<RecyclerAdapter> recyclerAdapters = new ArrayList<>();

    public HomeC2sPager(Activity context) {
        super(context);
        this.activity = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = PagerHomeC2sBinding.inflate(LayoutInflater.from(getContext()), parent, false);
        return new ViewHolder(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        initToolbar();
        initTab();
        initPager();
    }

    private void initToolbar() {
        binding.toolbar.setOnMenuItemClickListener(this);
        binding.toolbar.setNavigationOnClickListener(v -> activity.finish());
    }


    private void initPager() {
        recyclerAdapters.add(new C2cGemPager(getContext(), 1));
        recyclerAdapters.add(new C2cGemPager(getContext(), 4));
        PagerAdapter pagerAdapter = new PagerAdapter(recyclerAdapters);
        binding.pager.setAdapter(pagerAdapter);
        binding.pager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                TabLayout.Tab tab = binding.tab.getTabAt(position);
                if (tab == null) {
                    return;
                }
                tab.select();
            }
        });
    }

    private void initTab() {
        binding.tab.addTab(binding.tab.newTab().setText("购买").setId(0));
        binding.tab.addTab(binding.tab.newTab().setText("出售").setId(1));
        binding.tab.addOnTabSelectedListener(this);
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

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        if (item.getItemId() == R.id.v) {
            Intent intent = new Intent(getContext(), PledgeActivity.class);
            intent.putExtra("data_type", "1");
            getContext().startActivity(intent);
        } else if (item.getItemId() == R.id.Order) {
            Intent intent = new Intent(getContext(), OrderManagementActivity.class);
            getContext().startActivity(intent);
        }
        return false;
    }
}
