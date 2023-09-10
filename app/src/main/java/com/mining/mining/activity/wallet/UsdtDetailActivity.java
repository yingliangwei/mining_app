package com.mining.mining.activity.wallet;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.mining.mining.databinding.ActivityUsdtBillBinding;
import com.mining.mining.databinding.ActivityUsdtDetailBinding;
import com.mining.mining.util.StatusBarUtil;

public class UsdtDetailActivity extends AppCompatActivity {
    private ActivityUsdtDetailBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtil.setImmersiveStatusBar(this,true);
        binding = ActivityUsdtDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }
}
