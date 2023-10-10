package com.mining.mining.activity.c2s.orderManage;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.google.gson.Gson;
import com.mining.mining.activity.c2s.orderManage.adapter.OrderManageAdapter;
import com.mining.mining.adapter.RecyclerAdapter;
import com.mining.mining.databinding.PagerOrderManageBinding;
import com.mining.mining.entity.C2cEntity;
import com.mining.mining.pager.holder.ViewHolder;
import com.mining.mining.util.SharedUtil;
import com.mining.util.Handler;
import com.mining.util.OnHandler;
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

public class OrderManagePager extends RecyclerAdapter implements OnData, OnHandler, OnRefreshListener, OnRefreshLoadMoreListener {
    private final int code;
    private PagerOrderManageBinding binding;
    private OrderManageAdapter adapter;
    private final List<C2cEntity> c2cEntities = new ArrayList<>();
    private int start = 0, end = 20;
    private final Handler handler = new Handler(Looper.myLooper(), this);

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
        binding.recycle.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        binding.recycle.setAdapter(adapter);
    }

    @Override
    public void connect(SocketManage socketManage) {
        try {
            SharedUtil sharedUtil = new SharedUtil(getContext());
            JSONObject jsonObject = sharedUtil.getLogin(4, code, start, end);
            if (jsonObject == null) {
                return;
            }
            socketManage.print(jsonObject.toString());
        } catch (Exception e) {
            e.fillInStackTrace();
        }
    }

    @Override
    public void handle(String ds) {
        handler.sendMessage(3, "");
        try {
            JSONObject jsonObject = JSONObject.parseObject(ds);
            int code = jsonObject.getInteger("code");
            if (code == 200) {
                JSONArray data = jsonObject.getJSONArray("data");
                handler.sendMessage(1, data.toString());
            }
        } catch (Exception e) {
            e.fillInStackTrace();
        }
    }

    @Override
    public void handleMessage(int w, String str) {
        if (w == 1) {
            try {
                JSONArray array = new JSONArray(str);
                initRecyclerData(array);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else if (w == 2) {
            binding.Smart.finishLoadMore(1000, false, false);
            binding.Smart.finishRefresh(1000, false, false);
        } else if (w == 3) {
            binding.Smart.finishRefresh(1000, true, false);
            binding.Smart.finishLoadMore(1000, true, false);
        }
    }

    @Override
    public void error(String error) {
        handler.sendMessage(2, "");
    }

    private void initRecyclerData(JSONArray data) throws Exception {
        for (int i = 0; i < data.size(); i++) {
            String text = data.getString(i);
            C2cEntity entity = new Gson().fromJson(text, C2cEntity.class);
            c2cEntities.add(entity);
            adapter.notifyItemChanged(i);
        }
    }


    @Override
    public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
        if (StringUtil.isPowerOf20(c2cEntities.size())) {
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
        c2cEntities.clear();
        adapter.notifyDataSetChanged();
        SocketManage.init(this);
    }
}
