package com.mining.mining.activity.c2s;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.mining.mining.adapter.RecyclerAdapter;
import com.mining.mining.databinding.PagerItemC2cBinding;
import com.mining.mining.entity.C2cEntity;
import com.mining.mining.pager.holder.ViewHolder;
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

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class C2cGemPager extends RecyclerAdapter implements OnData, OnHandler, OnRefreshListener, OnRefreshLoadMoreListener {
    private PagerItemC2cBinding binding;
    private final Context activity;
    private C2cAdapter c2cAdapter;
    private final List<C2cEntity> list = new ArrayList<>();
    private final Handler handler = new Handler(Looper.getMainLooper(), this);
    private final int type;
    private int start = 0, end = 20;

    public C2cGemPager(Context activity, int type) {
        super(activity);
        this.activity = activity;
        this.type = type;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = PagerItemC2cBinding.inflate(LayoutInflater.from(activity), parent, false);
        return new ViewHolder(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        initRecycler();
        initSmart();
        SocketManage.init(this);
    }

    private void initSmart() {
        binding.Smart.setRefreshFooter(new ClassicsFooter(activity));
        binding.Smart.setRefreshHeader(new ClassicsHeader(activity));
        binding.Smart.setOnRefreshListener(this);
        binding.Smart.setOnLoadMoreListener(this);
    }

    private void initRecycler() {
        c2cAdapter = new C2cAdapter(list, activity, type);
        c2cAdapter.setEmptyTextView(binding.blank);
        binding.recycle.setLayoutManager(new LinearLayoutManager(activity));
        binding.recycle.addItemDecoration(new DividerItemDecoration(activity, DividerItemDecoration.VERTICAL));
        binding.recycle.setAdapter(c2cAdapter);
    }

    @Override
    public void connect(SocketManage socketManage) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("type", 4);
            jsonObject.put("code", type);
            jsonObject.put("start", start);
            jsonObject.put("end", end);
            System.out.println(jsonObject);
            socketManage.print(jsonObject.toString());
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

    @SuppressLint("NotifyDataSetChanged")
    private void initRecyclerData(JSONArray data) throws Exception {
        for (int i = 0; i < data.length(); i++) {
            String text = data.getString(i);
            C2cEntity entity = new Gson().fromJson(text, C2cEntity.class);
            list.add(entity);
        }
        c2cAdapter.notifyDataSetChanged();
    }

    @Override
    public void error(String error) {
        handler.sendMessage(2, "");
    }

    @Override
    public void handle(String ds) {
        try {
            JSONObject jsonObject = new JSONObject(ds);
            int code = jsonObject.getInt("code");
            if (code == 200) {
                JSONArray data = jsonObject.getJSONArray("data");
                handler.sendMessage(1, data.toString());
            }
            handler.sendMessage(3, "");
        } catch (Exception e) {
            e.fillInStackTrace();
        }
    }

    @Override
    public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
        if (StringUtil.isPowerOf20(list.size())) {
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
        list.clear();
        c2cAdapter.notifyDataSetChanged();
        SocketManage.init(this);
    }
}
