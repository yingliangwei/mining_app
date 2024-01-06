package com.mining.mining.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.google.gson.Gson;
import com.mining.mining.R;
import com.mining.mining.databinding.ActivityPluginSearchBinding;
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

public class PluginSearchActivity extends AppCompatActivity implements OnRefreshListener, OnRefreshLoadMoreListener, OnData, View.OnClickListener {
    private ActivityPluginSearchBinding binding;
    private final int start = 20;
    private int end = 0;
    private final List<PluginEntity> list = new ArrayList<>();
    private ItemVerticalAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtil.setImmersive(this, true);
        binding = ActivityPluginSearchBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initView();
        initSmart();
        initRecycler();
    }

    private void initView() {
        binding.search.setOnClickListener(this);
        binding.close.setOnClickListener(this);
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
        JSONObject jsonObject = sharedUtil.getLogin(12, 3, start, end);
        jsonObject.put("name", binding.edit.getText().toString());
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
                PluginEntity pluginEntity = new Gson().fromJson(data.getString(i), PluginEntity.class);
                pluginEntity.setJson(data.getString(i));
                list.add(pluginEntity);
                adapter.notifyItemChanged(list.size() - 1);
            }
        }
        binding.Smart.finishRefresh(true);
        binding.Smart.finishLoadMore(true);
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

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.close) {
            String text = binding.edit.getText().toString();
            if (text.length() == 0) {
                finish();
            } else {
                binding.edit.setText("");
            }
        } else if (v.getId() == R.id.search) {
            String text = binding.edit.getText().toString();
            if (text.length() < 2) {
                Toast.makeText(this, "搜索内容少于2个字符串!", Toast.LENGTH_SHORT).show();
                return;
            }
            list.clear();
            adapter.notifyItemRemoved(0);
            SocketManage.init(this);
        }
    }
}
