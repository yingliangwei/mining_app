package com.mining.mining.pager.home;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.google.gson.Gson;
import com.mining.mining.adapter.RecyclerAdapter;
import com.mining.mining.databinding.PagerVerticalItemHomeBinding;
import com.mining.mining.entity.PluginEntity;
import com.mining.mining.pager.holder.ViewHolder;
import com.mining.mining.pager.home.adapter.ItemVerticalAdapter;
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

public class HomeVerticalItemPager extends RecyclerAdapter implements OnData, OnRefreshListener, OnRefreshLoadMoreListener {
    private final Context context;
    private PagerVerticalItemHomeBinding binding;
    private final String tab_id;
    private final List<PluginEntity> list = new ArrayList<>();
    private ItemVerticalAdapter adapter;
    private int start = 20, end = 0;

    public HomeVerticalItemPager(Context context, String tab_id) {
        super(context);
        this.context = context;
        this.tab_id = tab_id;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = PagerVerticalItemHomeBinding.inflate(LayoutInflater.from(context), parent, false);
        return new ViewHolder(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        initRecycler();
        initSmart();
        SocketManage.init(this);
    }

    private void initSmart() {
        binding.Smart.setRefreshFooter(new ClassicsFooter(context));
        binding.Smart.setRefreshHeader(new ClassicsHeader(context));
        binding.Smart.setOnRefreshListener(this);
        binding.Smart.setOnRefreshLoadMoreListener(this);
    }

    private void initRecycler() {
        adapter = new ItemVerticalAdapter(context, list);
        adapter.setEmptyTextView(binding.blank);
        binding.recycle.setAdapter(adapter);
        binding.recycle.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
    }

    @Override
    public void connect(SocketManage socketManage) {
        SharedUtil sharedUtil = new SharedUtil(context);
        JSONObject jsonObject = sharedUtil.getLogin(12, 2, start, end);
        jsonObject.put("tab_id", Integer.parseInt(tab_id));
        socketManage.print(jsonObject.toString());
    }

    @Override
    public void error(String error) {
        binding.Smart.finishLoadMore(1000, false, false);
        binding.Smart.finishRefresh(1000, false, false);
    }

    private void initData(JSONArray data) {
        for (int i = 0; i < data.size(); i++) {
            PluginEntity pluginEntity = new Gson().fromJson(data.getString(i), PluginEntity.class);
            pluginEntity.setJson(data.getString(i));
            list.add(pluginEntity);
            adapter.notifyItemChanged(list.size() - 1);
        }
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
            initData(data);
        }
        binding.Smart.finishLoadMore(1000, true, false);
        binding.Smart.finishRefresh(1000, true, false);
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
