package com.mining.mining.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.google.gson.Gson;
import com.mining.mining.databinding.ActivityPluginLogBinding;
import com.mining.mining.entity.PluginEntity;
import com.mining.mining.pager.home.adapter.ItemVerticalAdapter;
import com.mining.mining.util.SharedUtil;
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

public class PluginLogActivity extends AppCompatActivity implements OnData, OnRefreshListener, OnRefreshLoadMoreListener {
    private ActivityPluginLogBinding binding;
    private int start = 20, end = 0;
    private final List<PluginEntity> list = new ArrayList<>();
    private ItemVerticalAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtil.setImmersiveStatusBar(this, true);
        binding = ActivityPluginLogBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initToolbar();
        initRecycler();
        initSmart();
        SocketManage.init(this);
    }

    private void initToolbar() {
        binding.toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void initRecycler() {
        adapter = new ItemVerticalAdapter(this, list);
        adapter.setEmptyTextView(binding.blank);
        binding.recycler.setAdapter(adapter);
        binding.recycler.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
    }

    private void initSmart() {
        binding.Smart.setRefreshHeader(new ClassicsHeader(this));
        binding.Smart.setRefreshFooter(new ClassicsFooter(this));
        binding.Smart.setOnRefreshLoadMoreListener(this);
        binding.Smart.setOnRefreshListener(this);
    }

    @Override
    public void connect(SocketManage socketManage) {
        SharedUtil sharedUtil = new SharedUtil(this);
        JSONObject jsonObject = sharedUtil.getLogin(12, 10, start, end);
        socketManage.print(jsonObject.toString());
    }

    @Override
    public void error(String error) {
        binding.spinKit.setVisibility(View.GONE);
        binding.Smart.finishRefresh();
        binding.Smart.finishLoadMore();
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
                if (jsonObject1 == null) {
                    continue;
                }
                PluginEntity pluginEntity = new Gson().fromJson(jsonObject1.toString(), PluginEntity.class);
                pluginEntity.setJson(jsonObject1.toString());
                list.add(pluginEntity);
                adapter.notifyItemChanged(list.size() - 1);
            }
        }
        binding.Smart.finishRefresh(true);
        binding.Smart.finishLoadMore( true);
    }

    @Override
    public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
        if (StringUtil.isPowerOf20(list.size())) {
            end = end + start;
            SocketManage.init(this);
        } else {
            refreshLayout.finishLoadMoreWithNoMoreData();
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        end = 0;
        list.clear();
        adapter.notifyDataSetChanged();
        SocketManage.init(this);
    }

}
