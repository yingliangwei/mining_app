package com.mining.mining.activity.c2s.gem.activity.pager.activity;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.fastjson2.JSONObject;
import com.google.gson.Gson;
import com.mining.mining.R;
import com.mining.mining.databinding.ActivityC2cGemDetailedBinding;
import com.mining.mining.entity.OrderManageEntity;
import com.mining.mining.util.SharedUtil;
import com.mining.util.ArithHelper;
import com.mining.util.StatusBarUtil;
import com.mining.util.StringUtil;
import com.xframe.network.OnData;
import com.xframe.network.SocketManage;

public class DetailedActivity extends AppCompatActivity implements OnData {
    private ActivityC2cGemDetailedBinding binding;
    private String data_id, data_type;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtil.setImmersiveStatusBar(this, true);
        binding = ActivityC2cGemDetailedBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initToolbar();
        initIntent();
        SocketManage.init(this);
    }

    private void initIntent() {
        data_id = getIntent().getStringExtra("data_id");
        data_type = getIntent().getStringExtra("data_type");
        if (data_id == null) {
            finish();
        }
    }

    private void initToolbar() {
        binding.toolbar.setNavigationOnClickListener(v -> finish());
    }

    @Override
    public void connect(SocketManage socketManage) {
        SharedUtil sharedUtil = new SharedUtil(this);
        JSONObject jsonObject = sharedUtil.getLogin(4, 19);
        jsonObject.put("data_id", data_id);
        jsonObject.put("data_type", data_type);
        socketManage.print(jsonObject.toString());
    }

    @Override
    public void handle(String ds) {
        JSONObject jsonObject = JSONObject.parseObject(ds);
        int code = jsonObject.getInteger("code");
        if (code != 200) {
            String msg = jsonObject.getString("msg");
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
            return;
        }
        JSONObject data = jsonObject.getJSONObject("data");
        if (data == null) {
            return;
        }
        OrderManageEntity entity = new Gson().fromJson(data.toString(), OrderManageEntity.class);
        setViewData(entity);
    }

    private void setViewData(OrderManageEntity entity) {
        binding.price.setText(getString(R.string.moneys, StringUtil.toRe(entity.getUsdt())));
        binding.size.setText(getString(R.string._0_00_gem, entity.getNumber()));
        double all = ArithHelper.mul(entity.getUsdt(), entity.getNumber());
        double premium = Double.parseDouble(entity.getPremium());
        double allUsdt = ArithHelper.add(all, premium);
        binding.usdt.setText(getString(R.string.s_usdt, StringUtil.toRe(String.valueOf(allUsdt))));
        binding.commission.setText(getString(R.string.s_usdt, StringUtil.toRe(entity.getPremium())));
        binding.name.setText(entity.getName());
        binding.Order.setText(entity.getId());
        binding.time.setText(entity.getTime());
    }
}
