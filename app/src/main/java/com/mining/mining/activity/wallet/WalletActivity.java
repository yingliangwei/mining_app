package com.mining.mining.activity.wallet;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.fastjson2.JSONObject;
import com.mining.mining.R;
import com.mining.mining.databinding.ActivityWalletBinding;
import com.mining.mining.util.SharedUtil;
import com.mining.util.StatusBarUtil;
import com.mining.util.StringUtil;
import com.xframe.network.OnData;
import com.xframe.network.SocketManage;

public class WalletActivity extends AppCompatActivity implements OnData, View.OnClickListener {
    private ActivityWalletBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtil.setImmersiveStatusBar(this, true);
        binding = ActivityWalletBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initToolbar();
        initView();
        SocketManage.init(this);
    }

    private void initView() {
        binding.usdtL.setOnClickListener(this);
        binding.gemL.setOnClickListener(this);
    }

    private void initToolbar() {
        binding.toolbar.setNavigationOnClickListener(v -> finish());
    }

    @Override
    public void connect(SocketManage socketManage) {
        SharedUtil sharedUtil = new SharedUtil(this);
        JSONObject jsonObject = sharedUtil.getLogin(5, 1);
        socketManage.print(jsonObject.toString());
    }

    @Override
    public void handle(String ds) {
        JSONObject jsonObject = JSONObject.parseObject(ds);
        int code = jsonObject.getInteger("code");
        if (code == 200) {
            JSONObject data = jsonObject.getJSONObject("data");
            String usdt = data.getString("usdt");
            String gme = data.getString("gem");
            binding.gem.setText(StringUtil.toRe(gme));
            binding.usdt.setText(StringUtil.toRe(usdt));
        } else if (code == 202) {
            String msg = jsonObject.getString("msg");
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.usdt_l) {
            startActivity(new Intent(this, UsdtDetailActivity.class));
        } else if (v.getId() == R.id.gem_l) {
            startActivity(new Intent(this, GemDetailActivity.class));
        }
    }
}
