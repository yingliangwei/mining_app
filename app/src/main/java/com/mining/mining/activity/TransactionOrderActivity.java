package com.mining.mining.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Looper;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.google.gson.Gson;
import com.mining.mining.activity.adapter.TransactionOrderAdapter;
import com.mining.mining.activity.login.LoginActivity;
import com.mining.mining.databinding.ActivityTransactionOrderBinding;
import com.mining.mining.entity.TransactionOrderEntity;
import com.mining.mining.util.SharedUtil;
import com.mining.util.Handler;
import com.mining.util.OnHandler;
import com.mining.util.StatusBarUtil;
import com.mining.util.StringUtil;
import com.scwang.smart.refresh.footer.ClassicsFooter;
import com.scwang.smart.refresh.header.ClassicsHeader;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnRefreshListener;
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener;
import com.xframe.network.OnData;
import com.xframe.network.SocketManage;


import java.util.ArrayList;
import java.util.List;

public class TransactionOrderActivity extends AppCompatActivity implements OnData, OnRefreshListener, OnRefreshLoadMoreListener {
    private ActivityTransactionOrderBinding binding;
    private int start = 0, end = 20;
    private TransactionOrderAdapter adapter;
    private final List<TransactionOrderEntity> list = new ArrayList<>();
    private String type;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        type = getIntent().getStringExtra("type");
        StatusBarUtil.setImmersiveStatusBar(this, true);
        binding = ActivityTransactionOrderBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initSmart();
        initToolbar();
        initRecycler();
        SocketManage.init(this);
    }

    private void initSmart() {
        binding.Smart.setRefreshFooter(new ClassicsFooter(this));
        binding.Smart.setRefreshHeader(new ClassicsHeader(this));
        binding.Smart.setOnRefreshListener(this);
        binding.Smart.setOnLoadMoreListener(this);
    }

    private void initRecycler() {
        adapter = new TransactionOrderAdapter(this, list);
        adapter.setEmptyTextView(binding.blank);
        binding.recycler.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        binding.recycler.setLayoutManager(new LinearLayoutManager(this));
        binding.recycler.setAdapter(adapter);
    }

    @Override
    public void error(String error) {
        binding.Smart.finishLoadMore(1000, false, false);
        binding.Smart.finishRefresh(1000, false, false);
    }

    @Override
    public void connect(SocketManage socketManage) {
        SharedUtil sharedUtil = new SharedUtil(this);
        JSONObject jsonObject = sharedUtil.getLogin(4, 7, start, end);
        jsonObject.put("is", type);
        socketManage.print(jsonObject.toString());
    }


    @Override
    public void handle(String ds) {
        JSONObject jsonObject = JSONObject.parseObject(ds);
        int code = jsonObject.getInteger("code");
        if (code == 200) {
            JSONArray data = jsonObject.getJSONArray("data");
            for (int i = 0; i < data.size(); i++) {
                JSONObject jsonObject1 = data.getJSONObject(i);
                list.add(new Gson().fromJson(jsonObject1.toString(), TransactionOrderEntity.class));
                adapter.notifyItemChanged(i);
            }
        }
        binding.Smart.finishRefresh(1000, true, false);
        binding.Smart.finishLoadMore(1000, true, false);
    }

    private void initToolbar() {
        binding.toolbar.setNavigationOnClickListener(v -> finish());
    }

    @Override
    public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
        if (StringUtil.isPowerOf20(list.size())) {
            start = end;
            end = end + 20;
            SocketManage.init(this);
        } else {
            refreshLayout.finishLoadMoreWithNoMoreData();
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        start = 0;
        end = 20;
        list.clear();
        adapter.notifyDataSetChanged();
        SocketManage.init(this);
    }
}
