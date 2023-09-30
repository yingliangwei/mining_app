package com.mining.mining.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Looper;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.gson.Gson;
import com.mining.mining.activity.adapter.TransactionOrderAdapter;
import com.mining.mining.activity.login.LoginActivity;
import com.mining.mining.databinding.ActivityTransactionOrderBinding;
import com.mining.mining.entity.TransactionOrderEntity;
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

public class TransactionOrderActivity extends AppCompatActivity implements OnData, OnHandler, OnRefreshListener, OnRefreshLoadMoreListener {
    private ActivityTransactionOrderBinding binding;
    private int start = 0, end = 20;
    private TransactionOrderAdapter adapter;
    private final List<TransactionOrderEntity> list = new ArrayList<>();
    private String type;
    private SharedPreferences sharedPreferences;
    private final Handler handler = new Handler(Looper.getMainLooper(), this);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = getSharedPreferences("user", Context.MODE_PRIVATE);
        type = getIntent().getStringExtra("type");
        StatusBarUtil.setImmersiveStatusBar(this, true);
        binding = ActivityTransactionOrderBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initSmart();
        initToolbar();
        initRecycler();
        SocketManage.init(this);
    }

    private void initSmart() {
        binding.Smart.setRefreshFooter(new ClassicsFooter(this));
        binding.Smart.setRefreshHeader(new ClassicsHeader(this));
        binding.Smart.setOnRefreshListener(this);
        binding.Smart.setOnLoadMoreListener(this);
    }

    private void initRecycler() {
        adapter = new TransactionOrderAdapter(this, list);
        adapter.setEmptyTextView(binding.blank);
        binding.recycler.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        binding.recycler.setLayoutManager(new LinearLayoutManager(this));
        binding.recycler.setAdapter(adapter);
    }

    @Override
    public void error(String error) {
        binding.Smart.finishLoadMore(1000, false, false);
        binding.Smart.finishRefresh(1000, false, false);
    }

    @Override
    public void connect(SocketManage socketManage) {
        try {
            String id = sharedPreferences.getString("id", null);
            String _key = sharedPreferences.getString("_key", null);
            if (id == null || _key == null) {
                LoginActivity.login(this);
                return;
            }
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("type", 4);
            jsonObject.put("code", 7);
            jsonObject.put("end", end);
            jsonObject.put("is", type);
            jsonObject.put("id", id);
            jsonObject.put("_key", _key);
            jsonObject.put("start", start);
            socketManage.print(jsonObject.toString());
        } catch (Exception e) {
            e.fillInStackTrace();
        }
    }


    @Override
    public void handle(String ds) {
        handler.sendMessage(1, ds);
    }

    @Override
    public void handleMessage(int w, String str) {
        if (w == 0) {
            Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
        } else if (w == 1) {
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

    private void initData(JSONArray data) throws JSONException {
        for (int i = 0; i < data.length(); i++) {
            JSONObject jsonObject = data.getJSONObject(i);
            System.out.println(jsonObject);
            list.add(new Gson().fromJson(jsonObject.toString(), TransactionOrderEntity.class));
            adapter.notifyItemChanged(i);
        }
    }

    private void initToolbar() {
        binding.toolbar.setNavigationOnClickListener(v -> finish());
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
}
