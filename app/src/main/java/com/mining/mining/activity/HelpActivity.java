package com.mining.mining.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.google.gson.Gson;
import com.mining.mining.activity.adapter.HelpAdapter;
import com.mining.mining.databinding.ActivityHelpBinding;
import com.mining.mining.entity.HelpEntity;
import com.mining.mining.util.SharedUtil;
import com.mining.util.StatusBarUtil;
import com.mining.util.StringUtil;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnRefreshListener;
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener;
import com.xframe.network.OnData;
import com.xframe.network.SocketManage;

import java.util.ArrayList;
import java.util.List;

public class HelpActivity extends AppCompatActivity implements OnData, OnRefreshListener, OnRefreshLoadMoreListener {
    private int start = 20, end = 0;
    private ActivityHelpBinding binding;
    private HelpAdapter adapter;
    private final List<HelpEntity> entities = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtil.setImmersiveStatusBar(this, true);
        binding = ActivityHelpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initToolbar();
        initView();
        SocketManage.init(this);
    }

    private void initView() {
        adapter = new HelpAdapter(this, entities);
        binding.recycler.setLayoutManager(new LinearLayoutManager(this));
        binding.recycler.setAdapter(adapter);
        binding.Smart.setOnLoadMoreListener(this);
        binding.Smart.setOnRefreshListener(this);
    }

    private void initToolbar() {
        binding.toolbar.setNavigationOnClickListener(v -> finish());
    }

    @Override
    public void connect(SocketManage socketManage) {
        SharedUtil sharedUtil = new SharedUtil(this);
        JSONObject jsonObject = sharedUtil.getLogin(22, 1, start, end);
        socketManage.print(jsonObject.toString());
    }


    @Override
    public void handle(String ds) {
        binding.spinKit.setVisibility(View.GONE);
        JSONObject jsonObject = JSONObject.parseObject(ds);
        int code = jsonObject.getInteger("code");
        if (code == 200) {
            JSONArray data = jsonObject.getJSONArray("data");
            if (data == null) {
                return;
            }
            for (int i = 0; i < data.size(); i++) {
                JSONObject jsonObject1 = data.getJSONObject(i);
                HelpEntity entity = new Gson().fromJson(jsonObject1.toString(), HelpEntity.class);
                entities.add(entity);
                adapter.notifyItemChanged(entities.size() - 1);
            }
        } else {
            String msg = jsonObject.getString("msg");
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        }

        binding.Smart.finishRefresh(true);
        binding.Smart.finishLoadMore(true);
    }

    @Override
    public void error(String error) {
        binding.spinKit.setVisibility(View.GONE);
        binding.Smart.finishRefresh(true);
        binding.Smart.finishLoadMore(true);
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        end = 0;
        entities.clear();
        adapter.notifyDataSetChanged();
        SocketManage.init(this);
    }

    @Override
    public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
        if (StringUtil.isPowerOf20(entities.size())) {
            end = end + start;
            SocketManage.init(this);
        } else {
            refreshLayout.finishLoadMoreWithNoMoreData();
        }
    }
}
