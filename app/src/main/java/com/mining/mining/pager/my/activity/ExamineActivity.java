package com.mining.mining.pager.my.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.google.gson.Gson;
import com.mining.mining.databinding.ActivityExamineBinding;
import com.mining.mining.entity.WithdrawalEntity;
import com.mining.mining.pager.my.activity.adapter.ExamineAdapter;
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

public class ExamineActivity extends AppCompatActivity implements OnData, OnRefreshListener, OnRefreshLoadMoreListener {
    private ActivityExamineBinding binding;
    private ExamineAdapter adapter;
    private final List<WithdrawalEntity> entities = new ArrayList<>();

    private int start = 20, end = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtil.setImmersiveStatusBar(this, true);
        binding = ActivityExamineBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initToolbar();
        initView();
        SocketManage.init(this);
    }

    private void initView() {
        adapter = new ExamineAdapter(this, entities);
        binding.recycler.setLayoutManager(new LinearLayoutManager(this));
        binding.recycler.setAdapter(adapter);

        binding.Smart.setOnRefreshListener(this);
        binding.Smart.setOnLoadMoreListener(this);
    }

    private void initToolbar() {
        binding.toolbar.setNavigationOnClickListener(v -> finish());
    }

    @Override
    public void connect(SocketManage socketManage) {
        SharedUtil sharedUtil = new SharedUtil(this);
        JSONObject jsonObject = sharedUtil.getLogin(21, 5, start, end);
        socketManage.print(jsonObject.toString());
    }

    @Override
    public void error(String error) {
        binding.Smart.finishLoadMore(true);
        binding.Smart.finishRefresh(true);
    }

    @Override
    public void handle(String ds) {
        binding.Smart.finishLoadMore(true);
        binding.Smart.finishRefresh(true);
        JSONObject jsonObject = JSONObject.parseObject(ds);
        int code = jsonObject.getInteger("code");
        if (code == 200) {
            JSONArray data = jsonObject.getJSONArray("data");
            if (data == null) {
                return;
            }
            for (int i = 0; i < data.size(); i++) {
                JSONObject jsonObject1 = data.getJSONObject(i);
                WithdrawalEntity entity = new Gson().fromJson(jsonObject1.toString(), WithdrawalEntity.class);
                JSONObject user = jsonObject1.getJSONObject("user");
                entity.setUser(user);
                entities.add(entity);
                adapter.notifyItemChanged(entities.size() - 1);
            }
        }
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
