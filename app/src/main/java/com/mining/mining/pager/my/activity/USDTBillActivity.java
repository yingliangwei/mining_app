package com.mining.mining.pager.my.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.google.gson.Gson;
import com.mining.mining.activity.wallet.UsdtBillAdapter;
import com.mining.mining.databinding.ActivityUsdtBillBinding;
import com.mining.mining.entity.UsdtBillEntity;
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

public class USDTBillActivity extends AppCompatActivity implements OnData, OnRefreshListener, OnRefreshLoadMoreListener {
    public ActivityUsdtBillBinding binding;
    private UsdtBillAdapter adapter;
    private final List<UsdtBillEntity> list = new ArrayList<>();
    private final int start = 20;
    private int end = 0;
    private String bill_id;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bill_id = getIntent().getStringExtra("bill_id");
        if (bill_id == null) {
            Toast.makeText(this, "用户ID为空", Toast.LENGTH_SHORT).show();
            return;
        }
        StatusBarUtil.setImmersiveStatusBar(this, true);
        binding = ActivityUsdtBillBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initToolbar();
        initRecycler();
        initSmart();
        SocketManage.init(this);
    }


    private void initSmart() {
        binding.Smart.setRefreshHeader(new ClassicsHeader(this));
        binding.Smart.setRefreshFooter(new ClassicsFooter(this));
        binding.Smart.setOnRefreshListener(this);
        binding.Smart.setOnLoadMoreListener(this);
    }

    private void initRecycler() {
        adapter = new UsdtBillAdapter(this, list);
        adapter.setEmptyTextView(binding.blank);
        binding.recycle.setLayoutManager(new LinearLayoutManager(this));
        binding.recycle.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        binding.recycle.setAdapter(adapter);
    }

    private void initToolbar() {
        binding.toolbar.setNavigationOnClickListener(v -> finish());
    }

    @Override
    public void connect(SocketManage socketManage) {
        SharedUtil sharedUtil = new SharedUtil(this);
        JSONObject jsonObject = sharedUtil.getLogin(21, 6, start, end);
        jsonObject.put("bill_id", bill_id);
        socketManage.print(jsonObject.toString());
    }


    @Override
    public void error(String error) {
        binding.Smart.finishRefresh(false);
        binding.Smart.finishLoadMore(false);
    }

    @Override
    public void handle(String ds) {
        JSONObject jsonObject = JSONObject.parseObject(ds);
        int code = jsonObject.getInteger("code");
        if (code == 200) {
            JSONArray data = jsonObject.getJSONArray("data");
            if (data == null) {
                return;
            }
            for (int i = 0; i < data.size(); i++) {
                JSONObject jsonObject1 = data.getJSONObject(i);
                list.add(new Gson().fromJson(jsonObject1.toString(), UsdtBillEntity.class));
                adapter.notifyItemChanged(list.size() - 1);
            }
        } else {
            String msg = jsonObject.getString("msg");
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        }
        binding.Smart.finishRefresh(true);
        binding.Smart.finishLoadMore(true);
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        end = 0;
        list.clear();
        adapter.notifyDataSetChanged();
        SocketManage.init(this);
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
}
