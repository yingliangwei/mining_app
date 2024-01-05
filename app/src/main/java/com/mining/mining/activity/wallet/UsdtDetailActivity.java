package com.mining.mining.activity.wallet;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.alibaba.fastjson2.JSONObject;
import com.mining.mining.R;
import com.mining.mining.activity.c2s.usdt.activity.UsdtC2cActivity;
import com.mining.mining.activity.recharge.RechargeActivity;
import com.mining.mining.activity.recharge.WithdrawalActivity;
import com.mining.mining.databinding.ActivityUsdtDetailBinding;
import com.mining.mining.util.SharedUtil;
import com.mining.util.StatusBarUtil;
import com.mining.util.StringUtil;
import com.xframe.network.OnData;
import com.xframe.network.SocketManage;


public class UsdtDetailActivity extends AppCompatActivity implements OnData, Toolbar.OnMenuItemClickListener, View.OnClickListener {
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
        binding.Withdrawal.setText("交易");
        binding.Withdrawal.setOnClickListener(this);
        binding.recharge.setOnClickListener(this);
    }

    private void initToolbar() {
        binding.toolbar.setNavigationOnClickListener(v -> finish());
        binding.toolbar.setOnMenuItemClickListener(this);
    }

    @Override
    public void connect(SocketManage socketManage) {
        SharedUtil sharedUtil = new SharedUtil(this);
        JSONObject jsonObject = sharedUtil.getLogin(5, 2);
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
        Intent intent = new Intent(this, UsdtBillActivity.class);
        intent.putExtra("code", 3);
        startActivity(intent);
        return false;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.Withdrawal) {
            startActivity(new Intent(this, UsdtC2cActivity.class));
        } else if (v.getId() == R.id.recharge) {
            startActivity(new Intent(this, RechargeActivity.class));
        }
    }
}
