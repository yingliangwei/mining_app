package com.mining.mining.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.fastjson2.JSONObject;
import com.mining.mining.R;
import com.mining.mining.databinding.ActivityPledgeBinding;
import com.mining.mining.util.SharedUtil;
import com.mining.util.StatusBarUtil;
import com.mining.util.StringUtil;
import com.xframe.network.OnData;
import com.xframe.network.SocketManage;
import com.xframe.widget.PayPass;


public class PledgeActivity extends AppCompatActivity implements OnData, View.OnClickListener {
    private ActivityPledgeBinding binding;
    private final OnData pledge = new OnData() {
        @Override
        public void handle(String ds) {
            try {
                JSONObject jsonObject = JSONObject.parseObject(ds);
                String msg = jsonObject.getString("msg");
                Toast.makeText(PledgeActivity.this, msg, Toast.LENGTH_SHORT).show();
                SocketManage.init(PledgeActivity.this);
            } catch (Exception e) {
                e.fillInStackTrace();
            }
        }

        @Override
        public void connect(SocketManage socketManage) {
            SharedUtil sharedUtil = new SharedUtil(PledgeActivity.this);
            JSONObject jsonObject = sharedUtil.getLogin(4, 16);
            jsonObject.put("pass", pass);
            socketManage.print(jsonObject.toString());
        }
    };

    private final OnData back_pledge = new OnData() {
        @Override
        public void handle(String ds) {
            JSONObject jsonObject = JSONObject.parseObject(ds);
            String msg = jsonObject.getString("msg");
            Toast.makeText(PledgeActivity.this, msg, Toast.LENGTH_SHORT).show();
            SocketManage.init(PledgeActivity.this);
        }

        @Override
        public void connect(SocketManage socketManage) {
            SharedUtil sharedUtil = new SharedUtil(PledgeActivity.this);
            JSONObject jsonObject = sharedUtil.getLogin(4, 17);
            jsonObject.put("pass", pass);
            socketManage.print(jsonObject.toString());
        }
    };
    private String usdt = "0";
    private String pass;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtil.setImmersive(this, true);
        binding = ActivityPledgeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initToolbar();
        initView();
        SocketManage.init(this);
    }

    private void initView() {
        binding.pledge.setOnClickListener(this);
        binding.backPledge.setOnClickListener(this);
    }

    private void initToolbar() {
        binding.toolbar.setNavigationOnClickListener(v -> finish());
    }

    @Override
    public void connect(SocketManage socketManage) {
        try {
            SharedUtil sharedUtil = new SharedUtil(this);
            JSONObject jsonObject = sharedUtil.getLogin(4, 15);
            socketManage.print(jsonObject.toString());
        } catch (Exception e) {
            e.fillInStackTrace();
        }
    }

    @Override
    public void handle(String ds) {
        try {
            JSONObject jsonObject = JSONObject.parseObject(ds);
            int code = jsonObject.getInteger("code");
            if (code == 200) {
                JSONObject data = jsonObject.getJSONObject("data");
                String name = data.getString("name");
                usdt = data.getString("usdt");
                binding.usdt.setText(String.format("质押金额：%s", StringUtil.toRe(usdt)));
                binding.text.setText(name);
                int isAuthentication = jsonObject.getInteger("isAuthentication");
                if (isAuthentication == 0) {
                    binding.pledge.setVisibility(View.VISIBLE);
                    binding.backPledge.setVisibility(View.GONE);
                } else {
                    binding.pledge.setVisibility(View.GONE);
                    binding.backPledge.setVisibility(View.VISIBLE);
                }
            }
        } catch (Exception e) {
            e.fillInStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.pledge) {
            PayPass payPass = new PayPass(this);
            payPass.setMoney(usdt);
            payPass.setPay(new PayPass.OnPay() {
                @Override
                public void onText(String pass) {
                    PledgeActivity.this.pass = pass;
                    SocketManage.init(pledge);
                }
            });
            payPass.show();
        } else if (v.getId() == R.id.back_pledge) {
            PayPass payPass = new PayPass(this);
            payPass.setPay(new PayPass.OnPay() {
                @Override
                public void onText(String pass) {
                    PledgeActivity.this.pass = pass;
                    SocketManage.init(back_pledge);
                }
            });
            payPass.show();
        }
    }
}
