package com.mining.mining.activity.wallet;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.alibaba.fastjson2.JSONObject;
import com.mining.mining.R;
import com.mining.mining.activity.c2s.C2CActivity;
import com.mining.mining.databinding.ActivityUsdtDetailBinding;
import com.mining.mining.util.SharedUtil;
import com.mining.util.StatusBarUtil;
import com.mining.util.StringUtil;
import com.xframe.network.OnData;
import com.xframe.network.SocketManage;

public class GemDetailActivity extends AppCompatActivity implements OnData, Toolbar.OnMenuItemClickListener, View.OnClickListener {
    private ActivityUsdtDetailBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtil.setImmersiveStatusBar(this, true);
        binding = ActivityUsdtDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initToolbar();
        initView();
        SocketManage.init(this);
    }

    private void initView() {
        binding.image.setImageDrawable(ContextCompat.getDrawable(this, R.mipmap.ic_ape_new_gemstone));
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
        SharedUtil sharedUtil = new SharedUtil(this);
        JSONObject jsonObject = sharedUtil.getLogin(5, 6);
        socketManage.print(jsonObject.toString());
    }


    @Override
    public void handle(String ds) {
        JSONObject jsonObject = JSONObject.parseObject(ds);
        int code = jsonObject.getInteger("code");
        if (code == 200) {
            String usdt = jsonObject.getString("usdt");
            binding.usdt.setText(StringUtil.toRe(usdt));
        } else {
            String msg = jsonObject.getString("msg");
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
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