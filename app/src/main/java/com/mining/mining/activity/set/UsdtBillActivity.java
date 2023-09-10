package com.mining.mining.activity.set;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.tabs.TabLayout;
import com.mining.mining.R;
import com.mining.mining.databinding.ActivityUsdtBillBinding;
import com.mining.mining.databinding.ItemTabBinding;
import com.mining.mining.util.StatusBarUtil;
import com.mining.mining.util.TabLayoutUtil;

import java.util.Objects;

public class UsdtBillActivity extends AppCompatActivity {
    public ActivityUsdtBillBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtil.setImmersiveStatusBar(this, true);
        binding = ActivityUsdtBillBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initTab();
        initToolbar();
    }

    private void initToolbar() {
        binding.toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void initTab() {
        binding.tab.addTab(newTab("消耗"));
        binding.tab.addTab(newTab("获得"));
    }

    private TabLayout.Tab newTab(String text) {
        TabLayout.Tab tab = binding.tab.newTab();
        tab.setText(text);
        return tab;
    }
}
