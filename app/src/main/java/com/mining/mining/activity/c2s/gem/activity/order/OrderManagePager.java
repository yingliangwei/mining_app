package com.mining.mining.activity.c2s.gem.activity.order;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.google.gson.Gson;
import com.mining.mining.activity.c2s.gem.activity.order.adapter.OrderManageAdapter;
import com.mining.mining.adapter.RecyclerAdapter;
import com.mining.mining.databinding.PagerOrderManageBinding;
import com.mining.mining.entity.C2cEntity;
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
    private final int code;
    private PagerOrderManageBinding binding;
    private OrderManageAdapter adapter;
    private final List<C2cEntity> c2cEntities = new ArrayList<>();
    private final int start = 20;
    private int end = 0;

    public OrderManagePager(Context context, int code) {
        super(context);
        this.code = code;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = PagerOrderManageBinding.inflate(LayoutInflater.from(getContext()), parent, false);
        return new ViewHolder(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        initRecycler();
        initSmart();
        SocketManage.init(this);
    }

    private void initSmart() {
        binding.Smart.setRefreshHeader(new ClassicsHeader(getContext()));
        binding.Smart.setRefreshFooter(new ClassicsFooter(getContext()));
        binding.Smart.setOnRefreshListener(this);
        binding.Smart.setOnRefreshLoadMoreListener(this);
    }

    private void initRecycler() {
        adapter = new OrderManageAdapter(getContext(), c2cEntities, code);
        adapter.setEmptyTextView(binding.blank);
        binding.recycle.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recycle.setAdapter(adapter);
    }

    @Override
    public void connect(SocketManage socketManage) {
        SharedUtil sharedUtil = new SharedUtil(getContext());
        JSONObject jsonObject = sharedUtil.getLogin(4, code, start, end);
        socketManage.print(jsonObject.toString());
    }

    @Override
    public void handle(String ds) {
        binding.spinKit.setVisibility(View.GONE);
        binding.Smart.finishRefresh(true);
        binding.Smart.finishLoadMore(true);
        JSONObject jsonObject = JSONObject.parseObject(ds);
        int code = jsonObject.getInteger("code");
        if (code == 200) {
            JSONArray data = jsonObject.getJSONArray("data");
            if (data == null) {
                return;
            }
            for (int i = 0; i < data.size(); i++) {
                JSONObject text = data.getJSONObject(i);
                C2cEntity entity = new Gson().fromJson(text.toString(), C2cEntity.class);
                c2cEntities.add(entity);
                adapter.notifyItemChanged(c2cEntities.size() - 1);
            }
        }
    }


    @Override
    public void error(String error) {
        binding.spinKit.setVisibility(View.GONE);
        binding.Smart.finishLoadMore(false);
        binding.Smart.finishRefresh(false);
    }

    @Override
    public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
        if (StringUtil.isPowerOf20(c2cEntities.size())) {
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
        c2cEntities.clear();
        adapter.notifyDataSetChanged();
        SocketManage.init(this);
    }
}
