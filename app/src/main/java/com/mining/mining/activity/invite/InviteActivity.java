package com.mining.mining.activity.invite;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.google.gson.Gson;
import com.mining.mining.R;
import com.mining.mining.databinding.ActivityInviteBinding;
import com.mining.mining.entity.InviteEntity;
import com.mining.mining.util.SharedUtil;
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

public class InviteActivity extends AppCompatActivity implements OnData, OnHandler, Toolbar.OnMenuItemClickListener, OnRefreshListener, OnRefreshLoadMoreListener {
    private ActivityInviteBinding binding;
    private InviteAdapter adapter;
    private final List<InviteEntity> list = new ArrayList<>();
    private int start = 20, end = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtil.setImmersiveStatusBar(this, true);
        binding = ActivityInviteBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initToolbar();
        initRecycle();
        initSmart();
        SocketManage.init(this);
    }

    private void initSmart() {
        binding.Smart.setRefreshFooter(new ClassicsFooter(this));
        binding.Smart.setRefreshHeader(new ClassicsHeader(this));
    }

    private void initRecycle() {
        adapter = new InviteAdapter(this, list);
        adapter.setEmptyTextView(binding.blank);
        binding.recycle.setAdapter(adapter);
        binding.recycle.setLayoutManager(new LinearLayoutManager(this));
    }


    private void initToolbar() {
        binding.toolbar.setOnMenuItemClickListener(this);
        binding.toolbar.setNavigationOnClickListener(v -> finish());
    }


    @Override
    public void connect(SocketManage socketManage) {
        SharedUtil sharedUtil = new SharedUtil(this);
        JSONObject jsonObject = sharedUtil.getLogin(11, 1, start, end);
        socketManage.print(jsonObject.toString());
    }

    @Override
    public void error(String error) {
        binding.Smart.finishLoadMore();
        binding.Smart.finishRefresh();
        binding.spinKit.setVisibility(View.GONE);
    }

    @Override
    public void handle(String ds) {
        binding.Smart.finishLoadMore();
        binding.Smart.finishRefresh();
        binding.spinKit.setVisibility(View.GONE);
        JSONObject jsonObject = JSONObject.parseObject(ds);
        int code = jsonObject.getInteger("code");
        if (code == 200) {
            int is_invite = jsonObject.getInteger("is_invite");
            if (is_invite == 1) {
                binding.toolbar.getMenu().findItem(R.id.set_invite).setVisible(false);
            }
            String sum = jsonObject.getString("sum");
            int invite_sum = jsonObject.getInteger("invite_sum");
            binding.sum.setText(StringUtil.toRe(sum));
            binding.inviteSum.setText(String.valueOf(invite_sum));
            JSONArray data = jsonObject.getJSONArray("data");
            if (data == null) {
                return;
            }
            initData(data);
        }
    }


    private void initData(JSONArray data) {
        if (data == null) {
            return;
        }
        for (int i = 0; i < data.size(); i++) {
            JSONObject jsonObject = data.getJSONObject(i);
            InviteEntity entity = new Gson().fromJson(jsonObject.toString(), InviteEntity.class);
            list.add(entity);
            adapter.notifyItemChanged(list.size() - 1);
        }
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        if (item.getItemId() == R.id.set_invite) {
            startActivity(new Intent(this, SetInviteActivity.class));
        }
        return false;
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
