package com.mining.mining.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.fastjson2.JSONObject;
import com.mining.mining.R;
import com.mining.mining.activity.login.LoginActivity;
import com.mining.mining.databinding.ActivitityLogOffBinding;
import com.mining.mining.util.SharedUtil;
import com.mining.util.StatusBarUtil;
import com.xframe.network.OnData;
import com.xframe.network.SocketManage;
import com.xframe.widget.PayPass;

public class LogOffActivity extends AppCompatActivity implements View.OnClickListener, PayPass.OnPay {
    private ActivitityLogOffBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtil.setImmersive(this, true);
        binding = ActivitityLogOffBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initToolbar();
        initView();
    }

    private void initView() {
        binding.ok.setOnClickListener(this);
    }

    private void initToolbar() {
        binding.toolbar.setNavigationOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.ok) {
            PayPass payPass = new PayPass(this);
            payPass.setPay(this);
            payPass.show();
        } else {
            finish();
        }
    }

    @Override
    public void onText(String pass) {
        SocketManage.init(new LogOff(pass));
    }

    private class LogOff implements OnData {
        private final String pass;

        public LogOff(String pass) {
            this.pass = pass;
        }

        @Override
        public void connect(SocketManage socketManage) {
            SharedUtil sharedUtil = new SharedUtil(LogOffActivity.this);
            JSONObject jsonObject = sharedUtil.getLogin(19, 1);
            jsonObject.put("pass", pass);
            socketManage.print(jsonObject.toString());
        }

        @Override
        public void handle(String ds) {
            JSONObject jsonObject = JSONObject.parseObject(ds);
            if (jsonObject == null) {
                return;
            }
            int code = jsonObject.getInteger("code");
            String msg = jsonObject.getString("msg");
            Toast.makeText(LogOffActivity.this, msg, Toast.LENGTH_SHORT).show();
            if (code == 200) {
                SharedPreferences sharedPreferences = getSharedPreferences("user", Context.MODE_PRIVATE);
                sharedPreferences.edit().clear().apply();
                Intent intent = new Intent(LogOffActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        }
    }

}
