package com.mining.listen.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.gson.Gson;
import com.mining.listen.adapter.MainAdapter;
import com.mining.listen.databinding.ActivityLogBinding;
import com.mining.listen.entity.MainEntity;
import com.mining.listen.util.LogSharedUtil;
import com.mining.util.StatusBarUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class LogActivity extends AppCompatActivity {
    private ActivityLogBinding binding;
    private final List<MainEntity> entities = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtil.setImmersiveStatusBar(this,true);
        binding = ActivityLogBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initToolbar();
        initView();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void initView() {
        MainAdapter adapter = new MainAdapter(this, entities);
        binding.recycler.setLayoutManager(new LinearLayoutManager(this));
        binding.recycler.setAdapter(adapter);
        try {
            LogSharedUtil sharedUtil = new LogSharedUtil(this);
            Map<String, String> data = (Map<String, String>) sharedUtil.getAll();
            Set<String> keys = data.keySet();
            for (String key : keys) {
                String value = data.get(key);
                MainEntity entity = new Gson().fromJson(value, MainEntity.class);
                entity.setJson(value);
                entities.add(entity);
            }
            adapter.notifyDataSetChanged();
        } catch (Exception e) {
            e.fillInStackTrace();
        }
    }

    private void initToolbar() {
        binding.toolbar.setNavigationOnClickListener(v -> finish());
    }
}
