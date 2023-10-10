package com.mining.mining.activity.wallet;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Looper;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.mining.mining.R;
import com.mining.mining.activity.c2s.C2CActivity;
import com.mining.mining.activity.login.LoginActivity;
import com.mining.mining.databinding.ActivityUsdtDetailBinding;
import com.mining.util.Handler;
import com.mining.util.OnHandler;
import com.mining.util.StatusBarUtil;
import com.mining.util.StringUtil;
import com.xframe.network.OnData;
import com.xframe.network.SocketManage;

import org.json.JSONObject;

public class GemDetailActivity extends AppCompatActivity implements OnData, OnHandler, Toolbar.OnMenuItemClickListener, View.OnClickListener {
    private ActivityUsdtDetailBinding binding;
    private final Handler handler = new Handler(Looper.getMainLooper(), this);
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = getSharedPreferences("user", Context.MODE_PRIVATE);
        StatusBarUtil.setImmersiveStatusBar(this, true);
        binding = ActivityUsdtDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initToolbar();
        initView();
        SocketManage.init(this);
    }

    private void initView() {
        binding.image.setImageDrawable(getDrawable(R.mipmap.ic_ape_new_gemstone));
        binding.recharge.setVisibility(View.GONE);
        binding.Withdrawal.setText("交易");
        binding.Withdrawal.setOnClickListener(this);
    }

    private void initToolbar() {
        binding.toolbar.setTitle("我的宝石");
        binding.toolbar.setNavigationOnClickListener(v -> finish());
        binding.toolbar.setOnMenuItemClickListener(this);
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
            jsonObject.put("code", 6);
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
            binding.usdt.setText(StringUtil.toRe(str));
        }
    }

    @Override
    public void handle(String ds) {
        try {
            JSONObject jsonObject = new JSONObject(ds);
            int code = jsonObject.getInt("code");
            if (code == 200) {
                String usdt = jsonObject.getString("usdt");
                handler.sendMessage(1, usdt);
            } else {
                String msg = jsonObject.getString("msg");
                handler.sendMessage(0, msg);
            }
        } catch (Exception e) {
            e.fillInStackTrace();
        }
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        Intent intent = new Intent(this, GemBillActivity.class);
        intent.putExtra("code", 4);
        startActivity(intent);
        return false;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.Withdrawal) {
            startActivity(new Intent(this, C2CActivity.class));
        }
    }
}