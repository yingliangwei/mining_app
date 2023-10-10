package com.mining.mining.activity.invite;

import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.view.MenuItem;
import android.widget.Toast;

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
import com.mining.util.Handler;
import com.mining.util.OnHandler;
import com.mining.util.StatusBarUtil;
import com.mining.util.StringUtil;
import com.xframe.network.OnData;
import com.xframe.network.SocketManage;

import java.util.ArrayList;
import java.util.List;

public class InviteActivity extends AppCompatActivity implements OnData, OnHandler, Toolbar.OnMenuItemClickListener {
    private ActivityInviteBinding binding;
    private InviteAdapter adapter;
    private final List<InviteEntity> list = new ArrayList<>();
    private final Handler handle = new Handler(Looper.getMainLooper(), this);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtil.setImmersiveStatusBar(this, true);
        binding = ActivityInviteBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initToolbar();
        initRecycle();
        SocketManage.init(this);
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
        JSONObject jsonObject = sharedUtil.getLogin(11, 1);
        socketManage.print(jsonObject.toString());
    }

    @Override
    public void handle(String ds) {
        System.out.println(ds);
        try {
            JSONObject jsonObject =  JSONObject.parseObject(ds);
            int code = jsonObject.getInteger("code");
            if (code == 200) {
                handle.sendMessage(1, jsonObject.toString());
            }
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
                JSONObject jsonObject =  JSONObject.parseObject(str);
                int is_invite = jsonObject.getInteger("is_invite");
                if (is_invite == 1) {
                    binding.toolbar.getMenu().findItem(R.id.set_invite).setVisible(false);
                }
                String sum = jsonObject.getString("sum");
                int invite_sum = jsonObject.getInteger("invite_sum");
                binding.sum.setText(StringUtil.toRe(sum));
                binding.inviteSum.setText(String.valueOf(invite_sum));
                JSONArray data = jsonObject.getJSONArray("data");
                initData(data);
            } catch (Exception e) {
                e.fillInStackTrace();
            }
        }
    }

    private void initData(JSONArray data)  {
        for (int i = 0; i < data.size(); i++) {
            JSONObject jsonObject = data.getJSONObject(i);
            InviteEntity entity = new Gson().fromJson(jsonObject.toString(), InviteEntity.class);
            list.add(entity);
            adapter.notifyItemChanged(i);
        }
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        if (item.getItemId() == R.id.set_invite) {
            startActivity(new Intent(this, SetInviteActivity.class));
        }
        return false;
    }
}
