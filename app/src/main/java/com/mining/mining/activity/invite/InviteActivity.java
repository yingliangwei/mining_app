package com.mining.mining.activity.invite;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Looper;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.gson.Gson;
import com.mining.mining.R;
import com.mining.mining.activity.login.LoginActivity;
import com.mining.mining.databinding.ActivityInviteBinding;
import com.mining.mining.entity.InviteEntity;
import com.mining.util.Handler;
import com.mining.util.OnHandler;
import com.mining.util.StatusBarUtil;
import com.xframe.network.OnData;
import com.xframe.network.SocketManage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class InviteActivity extends AppCompatActivity implements OnData, OnHandler, Toolbar.OnMenuItemClickListener {
    private ActivityInviteBinding binding;
    private InviteAdapter adapter;
    private final List<InviteEntity> list = new ArrayList<>();
    private final Handler handle = new Handler(Looper.getMainLooper(), this);
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtil.setImmersiveStatusBar(this, true);
        sharedPreferences = getSharedPreferences("user", Context.MODE_PRIVATE);
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
        try {
            String id = sharedPreferences.getString("id", null);
            String _key = sharedPreferences.getString("_key", null);
            if (id == null || _key == null) {
                LoginActivity.login(InviteActivity.this);
                return;
            }
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("type", 11);
            jsonObject.put("code", 1);
            jsonObject.put("id", id);
            jsonObject.put("_key", _key);
            socketManage.print(jsonObject.toString());
        } catch (Exception e) {
            e.fillInStackTrace();
        }
    }

    @Override
    public void handle(String ds) {
        System.out.println(ds);
        try {
            JSONObject jsonObject = new JSONObject(ds);
            int code = jsonObject.getInt("code");
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
                JSONObject jsonObject = new JSONObject(str);
                String sum = jsonObject.getString("sum");
                String invite_sum = jsonObject.getString("invite_sum");
                binding.sum.setText(sum);
                binding.inviteSum.setText(invite_sum);
                JSONArray data = jsonObject.getJSONArray("data");
                initData(data);
                int is_invite = jsonObject.getInt("is_invite");
                if (is_invite == 1) {
                    binding.toolbar.getMenu().findItem(R.id.set_invite).setVisible(false);
                }
            } catch (Exception e) {
                e.fillInStackTrace();
            }
        }
    }

    private void initData(JSONArray data) throws JSONException {
        for (int i = 0; i < data.length(); i++) {
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
