package com.mining.mining.activity.scan;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.mining.mining.databinding.ActivityScanTextBinding;
import com.mining.util.StatusBarUtil;

public class ScanTextActivity extends AppCompatActivity {
    private ActivityScanTextBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtil.setImmersive(this, true);
        binding = ActivityScanTextBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initToolbar();
        initView();
    }

    private void initView() {
        String text = getIntent().getStringExtra("text");
        if (text == null) {
            return;
        }
        binding.text.setText(text);
    }

    private void initToolbar() {
        binding.toolbar.setNavigationOnClickListener(v -> finish());
    }
}
