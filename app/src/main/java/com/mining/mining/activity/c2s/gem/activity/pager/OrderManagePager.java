package com.mining.mining.activity.c2s.gem.activity.pager;

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
import com.google.gson.Gson;
import com.mining.mining.activity.c2s.gem.activity.pager.adapter.OrderManageAdapter;
import com.mining.mining.adapter.RecyclerAdapter;
import com.mining.mining.databinding.PagerOrderManagementBinding;
import com.mining.mining.entity.OrderManageEntity;
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
    private final int  data_type;
    private OrderManageAdapter orderManageAdapter;
    private final List<OrderManageEntity> entities = new ArrayList<>();
    private int start = 20, end = 0;

    public OrderManagePager(Context context, int s) {
        super(context);
        this.data_type = s;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = PagerOrderManagementBinding.inflate(LayoutInflater.from(getContext()), parent, false);
        return new ViewHolder(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        initRecycler();
        initSmart();
        SocketManage.init(this);
    }

    private void initSmart() {
        binding.Smart.setRefreshFooter(new ClassicsFooter(getContext()));
        binding.Smart.setRefreshHeader(new ClassicsHeader(getContext()));
        binding.Smart.setOnRefreshListener(this);
        binding.Smart.setOnLoadMoreListener(this);
    }

    private void initRecycler() {
        orderManageAdapter = new OrderManageAdapter(getContext(), entities, data_type);
        binding.recycler.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recycler.setAdapter(orderManageAdapter);
    }

    @Override
    public void connect(SocketManage socketManage) {
        SharedUtil sharedUtil = new SharedUtil(getContext());
        JSONObject jsonObject = sharedUtil.getLogin(4, 18, start, end);
        jsonObject.put("data_type", data_type);
        socketManage.print(jsonObject.toString());
    }

    @Override
    public void error(String error) {
        
        binding.Smart.finishLoadMore(false);
        binding.Smart.finishRefresh(false);
    }

    @Override
    public void handle(String ds) {
        
        binding.Smart.finishRefresh(true);
        binding.Smart.finishLoadMore(true);
        JSONObject jsonObject = JSONObject.parseObject(ds);
        JSONArray data = jsonObject.getJSONArray("data");
        if (data == null) {
            return;
        }
        for (int i = 0; i < data.size(); i++) {
            JSONObject jsonObject1 = data.getJSONObject(i);
            OrderManageEntity entity = new Gson().fromJson(jsonObject1.toString(), OrderManageEntity.class);
            entities.add(entity);
            orderManageAdapter.notifyItemChanged(entities.size() - 1);
        }
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

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        end = 0;
        entities.clear();
        orderManageAdapter.notifyDataSetChanged();
        SocketManage.init(this);
    }
}
