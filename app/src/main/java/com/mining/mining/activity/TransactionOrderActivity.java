package com.mining.mining.activity;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.mining.mining.databinding.ActivityTransactionOrderBinding;
import com.mining.mining.util.StatusBarUtil;

public class TransactionOrderActivity extends AppCompatActivity {
    private ActivityTransactionOrderBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtil.setImmersiveStatusBar(this, true);
        binding = ActivityTransactionOrderBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initToolbar();
        initTab();
    }

    private void initToolbar() {
        binding.toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void initTab() {
        binding.tab.addTab(binding.tab.newTab().setText("购买订单"));
        binding.tab.addTab(binding.tab.newTab().setText("出售订单"));
    }
}
