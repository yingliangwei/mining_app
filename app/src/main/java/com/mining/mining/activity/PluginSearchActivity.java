package com.mining.mining.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.google.gson.Gson;
import com.mining.mining.R;
import com.mining.mining.databinding.ActivityPluginSearchBinding;
import com.mining.mining.entity.PluginEntity;
import com.mining.mining.pager.home.adapter.ItemVerticalAdapter;
import com.mining.util.Handler;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class PluginSearchActivity extends AppCompatActivity implements OnRefreshListener, OnRefreshLoadMoreListener, OnHandler, OnData, View.OnClickListener {
    private ActivityPluginSearchBinding binding;
    private int start = 0, end = 20;
    private final List<PluginEntity> list = new ArrayList<>();
    private ItemVerticalAdapter adapter;
    private final Handler handler = new Handler(Looper.getMainLooper(), this);

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
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("type", 12);
            jsonObject.put("code", 3);
            jsonObject.put("start", start);
            jsonObject.put("end", end);
            jsonObject.put("name", binding.edit.getText().toString());
            socketManage.print(jsonObject.toString());
        } catch (Exception e) {
            e.fillInStackTrace();
        }
    }

    @Override
    public void handleMessage(int w, String str) {
        if (w == 1) {
            binding.Smart.finishRefresh(1000, true, false);
            binding.Smart.finishLoadMore(1000, true, false);
            try {
                JSONObject jsonObject = new JSONObject(str);
                int code = jsonObject.getInt("code");
                if (code == 200) {
                    JSONArray data = jsonObject.getJSONArray("data");
                    initData(data);
                }
            } catch (Exception e) {
                e.fillInStackTrace();
            }
        }
    }

    @Override
    public void error(String error) {
        binding.Smart.finishRefresh(1000, false, false);
        binding.Smart.finishLoadMore(1000, false, false);
    }

    private void initData(JSONArray data) throws JSONException {
        for (int i = 0; i < data.length(); i++) {
            PluginEntity pluginEntity = new Gson().fromJson(data.getString(i), PluginEntity.class);
            pluginEntity.setJson(data.getString(i));
            list.add(pluginEntity);
            adapter.notifyItemChanged(i);
        }
    }

    @Override
    public void handle(String ds) {
        handler.sendMessage(1, ds);
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
