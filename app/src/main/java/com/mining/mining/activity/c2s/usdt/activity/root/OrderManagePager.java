package com.mining.mining.activity.c2s.usdt.activity.root;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.mining.mining.activity.c2s.usdt.activity.adapter.UsdtOrderAdapter;
import com.mining.mining.adapter.RecyclerAdapter;
import com.mining.mining.databinding.PagerOrderManagementBinding;
import com.mining.mining.entity.UsdtOrderEntity;
import com.mining.mining.pager.holder.ViewHolder;
import com.mining.mining.util.SharedUtil;
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

public class OrderManagePager extends RecyclerAdapter implements OnData, OnRefreshListener, OnRefreshLoadMoreListener {
    private PagerOrderManagementBinding binding;
    private final int start = 20;
    private int end = 0;
    private final String data_type;
    private final List<UsdtOrderEntity> strings = new ArrayList<>();
    private UsdtOrderAdapter adapter;

    public OrderManagePager(Context context, String id) {
        super(context);
        this.data_type = id;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = PagerOrderManagementBinding.inflate(LayoutInflater.from(getContext()), parent, false);
        return new ViewHolder(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        initSmart();
        initRecycler();
        SocketManage.init(this);
    }

    private void initRecycler() {
        adapter = new UsdtOrderAdapter(getContext(), strings, data_type, true);
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        binding.recycler.setLayoutManager(manager);
        binding.recycler.setAdapter(adapter);
    }

    private void initSmart() {
        binding.Smart.setRefreshFooter(new ClassicsFooter(getContext()));
        binding.Smart.setRefreshHeader(new ClassicsHeader(getContext()));
        binding.Smart.setOnRefreshListener(this);
        binding.Smart.setOnLoadMoreListener(this);
    }

    @Override
    public void connect(SocketManage socketManage) {
        SharedUtil sharedUtil = new SharedUtil(getContext());
        JSONObject jsonObject = sharedUtil.getLogin(24, 13, start, end);
        jsonObject.put("data_type", data_type);
        socketManage.print(jsonObject.toString());
    }

    @Override
    public void error(String error) {
        binding.spinKit.setVisibility(View.GONE);
        binding.Smart.finishLoadMore(false);
        binding.Smart.finishRefresh(false);
    }

    @Override
    public void handle(String ds) {
        binding.spinKit.setVisibility(View.GONE);
        binding.Smart.finishRefresh(true);
        binding.Smart.finishLoadMore(true);
        JSONObject jsonObject = JSONObject.parseObject(ds);
        if (jsonObject == null) {
            return;
        }
        JSONArray data = jsonObject.getJSONArray("data");
        int code = jsonObject.getInteger("code");
        if (code != 200 || data == null) {
            return;
        }
        for (int i = 0; i < data.size(); i++) {
            JSONObject jsonObject1 = data.getJSONObject(i);
            strings.add(UsdtOrderEntity.objectFromData(jsonObject1.toString()));
            adapter.notifyItemChanged(strings.size() - 1);
        }
    }

    @Override
    public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
        if (StringUtil.isPowerOf20(strings.size())) {
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
        strings.clear();
        adapter.notifyDataSetChanged();
        SocketManage.init(this);
    }
}
