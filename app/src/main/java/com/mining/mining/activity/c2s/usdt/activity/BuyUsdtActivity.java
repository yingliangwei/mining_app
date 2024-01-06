package com.mining.mining.activity.c2s.usdt.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.fastjson2.JSONObject;
import com.google.gson.Gson;
import com.mining.mining.R;
import com.mining.mining.databinding.ActivityBuyUsdtBinding;
import com.mining.mining.entity.C2cUsdtEntity;
import com.mining.mining.util.SharedUtil;
import com.mining.util.ArithHelper;
import com.mining.util.StatusBarUtil;
import com.mining.util.StringUtil;
import com.xframe.network.OnData;
import com.xframe.network.SocketManage;

public class BuyUsdtActivity extends AppCompatActivity implements OnData, TextWatcher, View.OnClickListener {
    private ActivityBuyUsdtBinding binding;
    private String id;
    private int type;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtil.setImmersiveStatusBar(this, true);
        binding = ActivityBuyUsdtBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initToolbar();
        initIntent();
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        SocketManage.init(this);
    }

    private void initView() {
        binding.gem.addTextChangedListener(this);
        binding.buy.setOnClickListener(this);
    }

    private void initIntent() {
        Intent intent = getIntent();
        id = intent.getStringExtra("id");
        type = intent.getIntExtra("type", 1);
        if (id == null) {
            finish();
            return;
        }
        initData();
    }

    private void initData() {
        if (type == 2) {
            binding.title.setText(getString(R.string.sell_usdt));
            binding.Available.setText(getString(R.string.Available_sell));
            binding.buy.setText(getString(R.string.sell));
            binding.Limited.setText(getString(R.string.Restricted));
            binding.userUsdt.setVisibility(View.GONE);
        }
    }

    private void initToolbar() {
        binding.toolbar.setOnClickListener(v -> finish());
    }

    @Override
    public void connect(SocketManage socketManage) {
        SharedUtil sharedUtil = new SharedUtil(this);
        JSONObject jsonObject = sharedUtil.getLogin(24, 2);
        jsonObject.put("data_type", type);
        jsonObject.put("data_id", id);
        socketManage.print(jsonObject.toString());
    }

    @Override
    public void handle(String ds) {
        JSONObject jsonObject = JSONObject.parseObject(ds);
        int code = jsonObject.getInteger("code");
        if (code != 200) {
            return;
        }
        JSONObject data = jsonObject.getJSONObject("data");
        if (data == null) {
            return;
        }
        setData(data);
    }

    private void setData(JSONObject jsonObject) {
        C2cUsdtEntity entity = new Gson().fromJson(jsonObject.toString(), C2cUsdtEntity.class);
        String text = switch (entity.getType()) {
            case "1" -> "微信支付";
            case "2" -> "银行卡支付";
            default -> "支付宝支付";
        };

        binding.userUsdt.setText(getString(R.string.app_available_usdt, StringUtil.toRe(entity.getUser_usdt())));
        binding.pay.setText(text);
        binding.article.setText(StringUtil.toRe(entity.getUsdt()));
        binding.usdt.setText(StringUtil.toRe(entity.getPrice()));
        binding.name.setText(entity.getName());
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        String text = s.toString();
        if (text.length() == 0 || text.contains(".")) {
            binding.gem.setTextColor(getColor(android.R.color.holo_red_dark));
            return;
        }
        binding.gem.setTextColor(getColor(android.R.color.black));
        double quantity = ArithHelper.mul(text, binding.usdt.getText().toString());
        int article = Integer.parseInt(binding.article.getText().toString());
        int usdt = Integer.parseInt(text);
        if (article < usdt) {
            binding.article.setTextColor(getColor(android.R.color.holo_red_dark));
        } else {
            binding.article.setTextColor(getColor(android.R.color.black));
        }
        binding.xUsdt.setText(String.valueOf(quantity));
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.buy) {
            SocketManage.init(new Buy(this));
        }
    }

    private class Buy implements OnData {
        private final Context context;

        public Buy(Context context) {
            this.context = context;
        }

        @Override
        public void connect(SocketManage socketManage) {
            SharedUtil sharedUtil = new SharedUtil(context);
            JSONObject jsonObject = sharedUtil.getLogin(24, 3);
            jsonObject.put("data_type", type);
            jsonObject.put("data_id", id);
            jsonObject.put("usdt", binding.gem.getText().toString());
            socketManage.print(jsonObject.toString());
        }

        @Override
        public void handle(String ds) {
            JSONObject jsonObject = JSONObject.parseObject(ds);
            int code = jsonObject.getInteger("code");
            if (code != 200) {
                String msg = jsonObject.getString("msg");
                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
                return;
            }
            String id = jsonObject.getString("id");
            Intent intent = new Intent(context, BuyDetailActivity.class);
            intent.putExtra("id", id);
            intent.putExtra("type", type);
            context.startActivity(intent);
        }
    }
}
