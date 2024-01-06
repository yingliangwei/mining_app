package com.mining.mining.activity.c2s.usdt.pager.pager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.google.gson.Gson;
import com.mining.mining.activity.c2s.usdt.pager.pager.adapter.C2cUsdtAdapter;
import com.mining.mining.adapter.RecyclerAdapter;
import com.mining.mining.databinding.PagerItemC2cBinding;
import com.mining.mining.entity.C2cUsdtEntity;
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

public class C2cUsdtPager extends RecyclerAdapter implements OnData, OnRefreshListener, OnRefreshLoadMoreListener {
    private PagerItemC2cBinding binding;
    private final int type;
    private C2cUsdtAdapter c2cAdapter;
    private final List<C2cUsdtEntity> list = new ArrayList<>();
    private final int start = 20;
    private int end = 0;

    public C2cUsdtPager(Context context, int type) {
        super(context);
        this.type = type;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = PagerItemC2cBinding.inflate(LayoutInflater.from(getContext()), parent, false);
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
        c2cAdapter = new C2cUsdtAdapter(getContext(), list, type);
        c2cAdapter.setEmptyTextView(binding.blank);
        binding.recycle.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recycle.setAdapter(c2cAdapter);
    }

    @SuppressLint("NotifyDataSetChanged")
    private void initRecyclerData(JSONArray data) {
        for (int i = 0; i < data.size(); i++) {
            String text = data.getString(i);
            C2cUsdtEntity entity = new Gson().fromJson(text, C2cUsdtEntity.class);
            list.add(entity);
        }
        c2cAdapter.notifyDataSetChanged();
    }

    @Override
    public void connect(SocketManage socketManage) {
        SharedUtil sharedUtil = new SharedUtil(getContext());
        JSONObject jsonObject = sharedUtil.getLogin(24, 1, start, end);
        jsonObject.put("data_type", type);
        socketManage.print(jsonObject.toString());
    }

    @Override
    public void error(String error) {
        
    }

    @Override
    public void handle(String ds) {
        binding.Smart.finishRefresh( true);
        binding.Smart.finishLoadMore( true);
        
        JSONObject jsonObject = JSONObject.parseObject(ds);
        int code = jsonObject.getInteger("code");
        if (code != 200) {
            String msg = jsonObject.getString("msg");
            Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
            return;
        }
        JSONArray data = jsonObject.getJSONArray("data");
        if (data == null) {
            return;
        }
        initRecyclerData(data);
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

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        end = 0;
        list.clear();
        c2cAdapter.notifyDataSetChanged();
        SocketManage.init(this);
    }
}
