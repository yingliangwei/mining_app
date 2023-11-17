package com.mining.mining.activity;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.fastjson2.JSONObject;
import com.google.gson.Gson;
import com.mining.mining.databinding.ActivityNewsDetailBinding;
import com.mining.mining.entity.NewsEntity;
import com.mining.mining.util.SharedUtil;
import com.mining.util.MessageEvent;
import com.mining.util.StatusBarUtil;
import com.xframe.network.OnData;
import com.xframe.network.SocketManage;


public class NewsDetailActivity extends AppCompatActivity implements OnData {
    private ActivityNewsDetailBinding binding;
    private NewsEntity entity;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtil.setImmersive(this, true);
        binding = ActivityNewsDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        String json = getIntent().getStringExtra("json");
        initToolbar();
        if (json == null) {
            finish();
        }
        initView(json);
    }

    private void initView(String json) {
        entity = new Gson().fromJson(json, NewsEntity.class);
        binding.title.setText(entity.getTitle());
        binding.message.setText(entity.getName());
        binding.time.setText(entity.getTime());
        SocketManage.init(this);
    }

    private void initToolbar() {
        binding.toolbar.setNavigationOnClickListener(v -> finish());
    }

    @Override
    public void connect(SocketManage socketManage) {
        SharedUtil sharedUtil = new SharedUtil(this);
        JSONObject jsonObject = sharedUtil.getLogin(12, 8);
        jsonObject.put("news_id", entity.getId());
        socketManage.print(jsonObject.toString());
    }

    @Override
    public void handle(String ds) {
    }
}
