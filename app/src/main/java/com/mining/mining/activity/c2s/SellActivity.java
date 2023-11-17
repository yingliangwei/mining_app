package com.mining.mining.activity.c2s;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.fastjson2.JSONObject;
import com.mining.mining.R;
import com.mining.mining.databinding.ActivitySellBinding;
import com.mining.mining.entity.MessageEvent;
import com.mining.mining.pager.home.HomePager;
import com.mining.mining.pager.mining.MiningPager;
import com.mining.mining.pager.my.MyPager;
import com.mining.mining.util.SharedUtil;
import com.mining.util.ArithHelper;
import com.mining.util.StatusBarUtil;
import com.scwang.smart.refresh.header.ClassicsHeader;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnRefreshListener;
import com.xframe.network.OnData;
import com.xframe.network.SocketManage;
import com.xframe.widget.PayPass;

import org.greenrobot.eventbus.EventBus;

public class SellActivity extends AppCompatActivity implements TextWatcher, OnData, View.OnClickListener, OnRefreshListener, PayPass.OnPay {
    private ActivitySellBinding binding;
    private String id;
    private String pass;

    private final OnData onData = new OnData() {
        @Override
        public void connect(SocketManage socketManage) {
            SharedUtil sharedUtil = new SharedUtil(SellActivity.this);
            JSONObject jsonObject = sharedUtil.getLogin(4, 6);
            jsonObject.put("pass", pass);
            jsonObject.put("gem_id", SellActivity.this.id);
            jsonObject.put("gem_size", binding.gem.getText().toString());
            socketManage.print(jsonObject.toString());
        }

        @Override
        public void handle(String ds) {
            JSONObject jsonObject = JSONObject.parseObject(ds);
            int code = jsonObject.getInteger("code");
            if (code == 200) {
                SocketManage.init(SellActivity.this);
                EventBus.getDefault().post(new MessageEvent(HomePager.class));
                EventBus.getDefault().post(new MessageEvent(1, MiningPager.class));
                EventBus.getDefault().post(new MessageEvent(MyPager.class));
            }
            String msg = jsonObject.getString("msg");
            Toast.makeText(SellActivity.this, msg, Toast.LENGTH_SHORT).show();
        }
    };

    private final OnData getCommission = new OnData() {
        @Override
        public void handle(String ds) {
            JSONObject jsonObject = JSONObject.parseObject(ds);
            int code = jsonObject.getInteger("code");
            if (code == 200) {
                double commission = jsonObject.getDouble("commission");
                PayPass payPass = new PayPass(SellActivity.this);
                payPass.setPay(SellActivity.this);
                double xUsdt = Double.parseDouble(binding.gem.getText().toString());
                double commissionUsdt = ArithHelper.mul(xUsdt, commission);
                double result = ArithHelper.add(xUsdt, commissionUsdt);
                double gem = Double.parseDouble(binding.xUsdt.getText().toString());
                double commissionGem = ArithHelper.mul(gem, commission);
                double resultGem = ArithHelper.sub(gem, commissionGem);
                payPass.setMoney("消耗宝石:" + result + "\n" + "手续费:" + commissionUsdt + "\n" + "获得USDT:" + resultGem + "\n" + "手续费:" + commissionGem);
                payPass.show();
            }
        }

        @Override
        public void connect(SocketManage socketManage) {
            SharedUtil sharedUtil = new SharedUtil(SellActivity.this);
            JSONObject jsonObject = sharedUtil.getLogin(5, 7);
            socketManage.print(jsonObject.toString());
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initId();
        StatusBarUtil.setImmersiveStatusBar(this, true);
        binding = ActivitySellBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initToolbar();
        initView();
        initSmart();
        SocketManage.init(this);
    }

    private void initSmart() {
        binding.Smart.setRefreshHeader(new ClassicsHeader(this));
        binding.Smart.setOnRefreshListener(this);
    }

    private void initView() {
        binding.buy.setOnClickListener(this);
        binding.gem.addTextChangedListener(this);
        binding.all.setOnClickListener(this);
    }

    private void initId() {
        id = getIntent().getStringExtra("id");
        if (id == null) {
            finish();
        }
    }

    private void initToolbar() {
        binding.toolbar.setOnClickListener(v -> finish());
    }

    @Override
    public void connect(SocketManage socketManage) {
        SharedUtil sharedUtil = new SharedUtil(SellActivity.this);
        JSONObject jsonObject = sharedUtil.getLogin(4, 5);
        jsonObject.put("gem_id", this.id);
        socketManage.print(jsonObject.toString());
    }

    @Override
    public void handle(String ds) {
        binding.spinKit.setVisibility(View.GONE);
        JSONObject jsonObject = JSONObject.parseObject(ds);
        int code = jsonObject.getInteger("code");
        if (code == 200) {
            JSONObject data = jsonObject.getJSONObject("data");
            initViewData(data);
        } else if (code == 402) {
            String msg = jsonObject.getString("msg");
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
            finish();
        }
        binding.Smart.finishRefresh(true);
    }

    @Override
    public void error(String error) {
        binding.spinKit.setVisibility(View.GONE);
        binding.Smart.finishRefresh(false);
    }

    private void initViewData(JSONObject data) {
        String article = data.getString("article");
        String usdt = data.getString("usdt");
        JSONObject user = data.getJSONObject("user");
        String name = user.getString("name");
        String user_usdt = user.getString("usdt");

        binding.usdt.setText(usdt);
        binding.name.setText(name);
        binding.article.setText(article);
        binding.userUsdt.setText(user_usdt);

    }


    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        // 文本变化时执行的代码
        String text = s.toString();
        if (text.length() == 0) {
            return;
        }
        int num = Integer.parseInt(text);
        int gem = Integer.parseInt(binding.article.getText().toString());

        double quantity = ArithHelper.mul(num, Double.parseDouble(binding.usdt.getText().toString()));
        double dd = Double.parseDouble(binding.userUsdt.getText().toString());

        if (num > dd) {
            binding.userUsdt.setTextColor(getColor(android.R.color.holo_red_dark));
        } else {
            binding.userUsdt.setTextColor(getColor(android.R.color.black));
        }

        if (num > gem) {
            binding.article.setTextColor(getColor(android.R.color.holo_red_dark));
        } else {
            binding.article.setTextColor(getColor(android.R.color.black));
        }
        binding.xUsdt.setText(String.valueOf(quantity));
        // 在这里可以根据文本内容执行相应的操作
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.all) {
            binding.gem.setText(binding.article.getText());
            binding.gem.setSelection(binding.gem.getText().length());
        } else if (v.getId() == R.id.buy) {
            int num = Integer.parseInt(binding.gem.getText().toString());
            int gem = Integer.parseInt(binding.article.getText().toString());
            double dd = Double.parseDouble(binding.userUsdt.getText().toString());
            if (num > dd) {
                Toast.makeText(this, "可用宝石不足", Toast.LENGTH_SHORT).show();
                return;
            }
            if (num > gem) {
                Toast.makeText(this, "购买数量大于可买数量", Toast.LENGTH_SHORT).show();
                return;
            }
            SocketManage.init(getCommission);
        }
    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        SocketManage.init(this);
    }

    @Override
    public void onText(String pass) {
        this.pass = pass;
        SocketManage.init(onData);
    }
}
