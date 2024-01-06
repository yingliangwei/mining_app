package com.mining.mining.activity.c2s.gem.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.fastjson2.JSONObject;
import com.mining.mining.R;
import com.mining.mining.databinding.ActivityPledgeBinding;
import com.mining.mining.entity.MessageEvent;
import com.mining.mining.pager.my.MyPager;
import com.mining.mining.util.SharedUtil;
import com.mining.util.StatusBarUtil;
import com.mining.util.StringUtil;
import com.xframe.network.OnData;
import com.xframe.network.SocketManage;
import com.xframe.widget.PayPass;

import org.greenrobot.eventbus.EventBus;


public class PledgeActivity extends AppCompatActivity implements OnData, View.OnClickListener {
    private ActivityPledgeBinding binding;
    private String usdt = "0";
    private String pass;
    private int data_type;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtil.setImmersive(this, true);
        binding = ActivityPledgeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initToolbar();
        initIntent();
        initView();
        SocketManage.init(this);
    }

    private void initIntent() {
        data_type = getIntent().getIntExtra("data_type", 1);
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
        SharedUtil sharedUtil = new SharedUtil(this);
        JSONObject jsonObject = sharedUtil.getLogin(4, 15);
        jsonObject.put("data_type", data_type);
        socketManage.print(jsonObject.toString());
    }

    @Override
    public void error(String error) {
        binding.spinKit.setVisibility(View.GONE);
    }

    @Override
    public void handle(String ds) {
        binding.spinKit.setVisibility(View.GONE);
        JSONObject jsonObject = JSONObject.parseObject(ds);
        int code = jsonObject.getInteger("code");
        if (code == 200) {
            JSONObject data = jsonObject.getJSONObject("data");
            String name = data.getString("name");
            usdt = data.getString("usdt");
            binding.usdt.setText(String.format("质押金额USDT：%s", StringUtil.toRe(usdt)));
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
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.pledge) {
            PayPass payPass = new PayPass(this);
            payPass.setMoney("支付USDT:" + StringUtil.toRe(usdt));
            payPass.setPay(pass -> {
                PledgeActivity.this.pass = pass;
                SocketManage.init(new Pledge());
            });
            payPass.show();
        } else if (v.getId() == R.id.back_pledge) {
            PayPass payPass = new PayPass(this);
            payPass.setPay(pass -> {
                PledgeActivity.this.pass = pass;
                SocketManage.init(new BackPledge());
            });
            payPass.show();
        }
    }

    private class Pledge implements OnData {
        @Override
        public void handle(String ds) {
            JSONObject jsonObject = JSONObject.parseObject(ds);
            int code = jsonObject.getInteger("code");
            if (code == 200) {
                EventBus.getDefault().post(new MessageEvent(MyPager.class));
            }
            String msg = jsonObject.getString("msg");
            Toast.makeText(PledgeActivity.this, msg, Toast.LENGTH_SHORT).show();
            SocketManage.init(PledgeActivity.this);

        }

        @Override
        public void connect(SocketManage socketManage) {
            SharedUtil sharedUtil = new SharedUtil(PledgeActivity.this);
            JSONObject jsonObject = sharedUtil.getLogin(4, 16);
            jsonObject.put("data_type", data_type);
            jsonObject.put("pass", pass);
            socketManage.print(jsonObject.toString());
        }
    }

    private class BackPledge implements OnData {
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
            jsonObject.put("data_type", data_type);
            jsonObject.put("pass", pass);
            socketManage.print(jsonObject.toString());
        }
    }
}
