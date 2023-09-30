package com.mining.mining.activity.wallet;

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
import com.mining.mining.activity.login.LoginActivity;
import com.mining.mining.databinding.ActivityUsdtBillBinding;
import com.mining.mining.entity.GemBillEntity;
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

public class GemBillActivity extends AppCompatActivity implements OnData, OnHandler, OnRefreshListener, OnRefreshLoadMoreListener {
    public ActivityUsdtBillBinding binding;
    private GemBillAdapter adapter;
    private final List<GemBillEntity> list = new ArrayList<>();
    private SharedPreferences sharedPreferences;
    private final Handler handler = new Handler(Looper.getMainLooper(), this);
    private int start = 0, end = 20;
    private int code;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        code = getIntent().getIntExtra("code", 3);
        sharedPreferences = getSharedPreferences("user", Context.MODE_PRIVATE);
        StatusBarUtil.setImmersiveStatusBar(this, true);
        binding = ActivityUsdtBillBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        if (code == 4) {
            binding.toolbar.setTitle("宝石记录");
        }
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
        adapter = new GemBillAdapter(this, list);
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
        try {
            String id = sharedPreferences.getString("id", null);
            String _key = sharedPreferences.getString("_key", null);
            if (id == null || _key == null) {
                LoginActivity.login(this);
                return;
            }
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("type", 5);
            jsonObject.put("code", code);
            jsonObject.put("start", start);
            jsonObject.put("end", end);
            jsonObject.put("id", id);
            jsonObject.put("_key", _key);
            socketManage.print(jsonObject.toString());
        } catch (Exception e) {
            e.fillInStackTrace();
        }
    }

    @Override
    public void handleMessage(int w, String str) {
        if (w == 0) {
            Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
        } else if (w == 1) {
            try {
                JSONArray data = new JSONArray(str);
                initData(data);
            } catch (Exception e) {
                e.fillInStackTrace();
            }
        } else if (w == 4) {
            binding.Smart.finishRefresh(1000, true, false);
            binding.Smart.finishLoadMore(1000, true, false);
        } else if (w == 5) {
            binding.Smart.finishRefresh(1000, false, false);
            binding.Smart.finishLoadMore(1000, false, false);
        }
    }

    private void initData(JSONArray data) throws JSONException {
        for (int i = 0; i < data.length(); i++) {
            JSONObject jsonObject = data.getJSONObject(i);
            list.add(new Gson().fromJson(jsonObject.toString(), GemBillEntity.class));
            adapter.notifyItemChanged(i);
        }
    }

    @Override
    public void error(String error) {
        handler.sendMessage(5, "");
    }

    @Override
    public void handle(String ds) {
        handler.sendMessage(4, "");
        try {
            JSONObject jsonObject = new JSONObject(ds);
            int code = jsonObject.getInt("code");
            if (code == 200) {
                JSONArray data = jsonObject.getJSONArray("data");
                handler.sendMessage(1, data.toString());
            } else {
                String msg = jsonObject.getString("msg");
                handler.sendMessage(0, msg);
            }
        } catch (Exception e) {
            e.fillInStackTrace();
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
    public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
        if (StringUtil.isPowerOf20(list.size())) {
            start = end;
            end = end + 20;
            SocketManage.init(this);
        } else {
            refreshLayout.finishLoadMoreWithNoMoreData();
        }
    }
}
