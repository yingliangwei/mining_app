package com.mining.mining.activity.c2s;

import android.content.Intent;
import android.hardware.TriggerEventListener;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toolbar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.mining.mining.R;
import com.mining.mining.activity.TransactionOrderActivity;
import com.mining.mining.adapter.PagerAdapter;
import com.mining.mining.adapter.RecyclerAdapter;
import com.mining.mining.databinding.ActivityC2sBinding;
import com.mining.mining.pager.c2c.C2cGemPager;
import com.mining.mining.pager.c2c.ItemC2CPager;
import com.mining.mining.util.StatusBarUtil;

import java.util.ArrayList;
import java.util.List;

public class C2CActivity extends AppCompatActivity implements Toolbar.OnMenuItemClickListener {
    private ActivityC2sBinding binding;
    private final List<RecyclerAdapter> recyclerAdapters = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtil.setImmersiveStatusBar(this, true);
        binding = ActivityC2sBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initToolbar();
        initTab();
        initPager();
    }

    private void initPager() {
        recyclerAdapters.add(new C2cGemPager(this, 1));
        recyclerAdapters.add(new C2cGemPager(this, 4));
        PagerAdapter pagerAdapter = new PagerAdapter(recyclerAdapters);
        binding.pager.setAdapter(pagerAdapter);
        binding.pager.setOffscreenPageLimit(recyclerAdapters.size());
    }

    private void initTab() {
        binding.tab.addTab(binding.tab.newTab().setText("购买"));
        binding.tab.addTab(binding.tab.newTab().setText("出售"));
    }

    private void initToolbar() {
        binding.toolbar.setOnMenuItemClickListener(this::onMenuItemClick);
        binding.toolbar.setNavigationOnClickListener(v -> finish());
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        if (item.getItemId() == R.id.Order) {
            startActivity(new Intent(this, TransactionOrderActivity.class));
        }
        return false;
    }
}
