package com.mining.mining.activity.recharge;

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
import com.mining.mining.databinding.ActivityRechargeBinding;
import com.mining.mining.util.SharedUtil;
import com.mining.util.StatusBarUtil;
import com.xframe.network.OnData;
import com.xframe.network.SocketManage;

public class RechargeActivity extends AppCompatActivity implements View.OnClickListener, OnData, Toolbar.OnMenuItemClickListener {
    private ActivityRechargeBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtil.setImmersiveStatusBar(this, true);
        binding = ActivityRechargeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initToolbar();
        initView();
    }

    private void initView() {
        binding.recharge.setOnClickListener(this);
    }

    private void initToolbar() {
        binding.toolbar.setNavigationOnClickListener(v -> finish());
        binding.toolbar.setOnMenuItemClickListener(this);
    }

    @Override
    public void connect(SocketManage socketManage) {
        SharedUtil sharedUtil = new SharedUtil(this);
        JSONObject jsonObject = sharedUtil.getLogin(6, 1);
        String usdt = binding.usdt.getText().toString();
        jsonObject.put("usdt", usdt);
        socketManage.print(jsonObject.toString());
    }

    @Override
    public void handle(String ds) {
        JSONObject jsonObject = JSONObject.parseObject(ds);
        int code = jsonObject.getInteger("code");
        if (code == 200) {
            JSONObject data = jsonObject.getJSONObject("data");
            if (data == null) {
                return;
            }
            Intent intent = new Intent(this, RechargeInformationActivity.class);
            intent.putExtra("json", data.toString());
            startActivity(intent);
        } else {
            String msg = jsonObject.getString("msg");
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.recharge) {
            String usdt = binding.usdt.getText().toString();
            if (usdt.equals("")) {
                Toast.makeText(this, "金额不能为空", Toast.LENGTH_SHORT).show();
                return;
            }
            try {
                Double d = Double.parseDouble(usdt);
            } catch (Exception e) {
                e.fillInStackTrace();
                Toast.makeText(this, "金额错误", Toast.LENGTH_SHORT).show();
                return;
            }
            SocketManage.init(this);
        }
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        if (item.getItemId() == R.id.log) {
            startActivity(new Intent(this, RechargeLogActivity.class));
        }
        return false;
    }
}
