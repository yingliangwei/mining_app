package com.mining.press.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.gson.Gson;
import com.mining.pluginwidget.refresh.header.ClassicsHeader;
import com.mining.press.adapter.MainAdapter;
import com.mining.press.databinding.ActivityPressLogBinding;
import com.mining.press.entity.PressEntity;
import com.plugin.activity.PluginActivity;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnRefreshListener;
import com.xframe.network.OnData;
import com.xframe.network.SocketManage;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class PressLogActivity extends PluginActivity implements OnData, OnRefreshListener {
    private ActivityPressLogBinding binding;
    private SharedPreferences sharedPreferences;
    private MainAdapter mainAdapter;
    private final List<PressEntity> list = new ArrayList<>();

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        sharedPreferences = thisContex.getSharedPreferences("user", Context.MODE_PRIVATE);
        binding = ActivityPressLogBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initToolbar();
        initRecycler();
        initSmart();
        SocketManage.init(this);
    }

    private void initToolbar() {
        binding.toolbar.setNavigationOnClickListener(v -> thisContex.finish());
    }

    private void initSmart() {
        binding.Smart.setRefreshHeader(new ClassicsHeader(thisContex));
        binding.Smart.setOnRefreshListener(this);
    }

    private void initRecycler() {
        list.add(new PressEntity());
        mainAdapter = new MainAdapter(thisContex, list);
        binding.recycler.setLayoutManager(new LinearLayoutManager(thisContex));
        binding.recycler.setAdapter(mainAdapter);
    }

    @Override
    public void connect(SocketManage socketManage) {
        try {
            String id = sharedPreferences.getString("id", null);
            String _key = sharedPreferences.getString("_key", null);
            if (id == null || _key == null) {
                return;
            }
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("type", 13);
            jsonObject.put("code", 3);
            jsonObject.put("id", id);
            jsonObject.put("_key", _key);
            socketManage.print(jsonObject.toString());
        } catch (Exception e) {
            e.fillInStackTrace();
        }
    }

    @Override
    public void handle(String ds) {
        try {
            JSONObject jsonObject = new JSONObject(ds);
            int code = jsonObject.getInt("code");
            if (code == 200) {
                JSONArray data = jsonObject.getJSONArray("data");
                initPressData(data);
                binding.Smart.finishRefresh(true);
            }
        } catch (Exception e) {
            e.fillInStackTrace();
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private void initPressData(JSONArray data) {
        list.clear();
        list.add(new PressEntity());
        for (int i = 0; i < data.length(); i++) {
            try {
                PressEntity pressEntity = new Gson().fromJson(data.getString(i), PressEntity.class);
                if (!pressEntity.getBill_k().equals(pressEntity.getK())) {
                    if (pressEntity.getStone().equals("0")) {
                        pressEntity.setStone("0");
                    } else {
                        pressEntity.setStone_x("-" + pressEntity.getStone());
                    }
                }
                list.add(pressEntity);
                mainAdapter.notifyItemChanged(i);
            } catch (Exception e) {
                e.fillInStackTrace();
            }
        }
    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        SocketManage.init(this);
    }
}
