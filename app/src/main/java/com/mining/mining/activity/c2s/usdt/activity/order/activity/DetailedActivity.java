package com.mining.mining.activity.c2s.usdt.activity.order.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.fastjson2.JSONObject;
import com.mining.mining.activity.c2s.usdt.activity.BuyDetailActivity;
import com.mining.mining.databinding.ActivityUsdtOrdeDetaileBinding;
import com.mining.mining.entity.UsdtOrderEntity;
import com.mining.mining.util.SharedUtil;
import com.mining.mining.util.StringUtil;
import com.mining.util.StatusBarUtil;
import com.xframe.network.OnData;
import com.xframe.network.SocketManage;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class DetailedActivity extends AppCompatActivity implements OnData {
    private ActivityUsdtOrdeDetaileBinding binding;
    private String data_type;
    private String data_id;
    private CountDownTimer countDownTimer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtil.setImmersive(this, true);
        binding = ActivityUsdtOrdeDetaileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initToolbar();
        initIntent();
        SocketManage.init(this);
    }

    private void initIntent() {
        data_id = getIntent().getStringExtra("data_id");
        data_type = getIntent().getStringExtra("data_type");
        if (data_type == null || data_id == null) {
            finish();
        }
    }

    private void initToolbar() {
        binding.toolbar.setNavigationOnClickListener(v -> finish());
    }

    @Override
    public void connect(SocketManage socketManage) {
        SharedUtil sharedUtil = new SharedUtil(this);
        JSONObject jsonObject = sharedUtil.getLogin(24, 10);
        jsonObject.put("data_type", data_type);
        jsonObject.put("data_id", data_id);
        System.out.println(jsonObject);
        socketManage.print(jsonObject.toString());
    }

    @Override
    public void handle(String ds) {
        System.out.println(ds);
        JSONObject jsonObject = JSONObject.parseObject(ds);
        if (jsonObject == null) {
            return;
        }
        int code = jsonObject.getInteger("code");
        JSONObject data = jsonObject.getJSONObject("data");
        if (code != 200 && data == null) {
            return;
        }
        initData(data);
    }

    private void initData(JSONObject jsonObject) {
        if (jsonObject == null) {
            return;
        }
        UsdtOrderEntity entity = UsdtOrderEntity.objectFromData(jsonObject.toString());
        binding.time.setText(entity.time);
        binding.order.setText(entity.id);
        binding.name.setText(entity.name);
        binding.price.setText(String.format("%sRMB", entity.price));
        binding.size.setText(String.format("%sUSDT", entity.usdt));
        binding.usdt.setText(String.format("%sRMB", entity.rmb));
        switch (entity.type) {
            case "0" -> {
                binding.title.setText("等待付款");
                if (!StringUtil.isTimeExceeded(entity.time)) {
                    initTime(entity.time);
                    binding.view.setOnClickListener(v -> {
                        Intent intent = new Intent(DetailedActivity.this, BuyDetailActivity.class);
                        intent.putExtra("id", entity.id);
                        intent.putExtra("type", data_type);
                        startActivity(intent);
                    });
                } else {
                    binding.title.setText("订单已超时");
                    binding.Time1.setVisibility(View.GONE);
                    binding.pay.setVisibility(View.GONE);
                }
            }
            case "2" -> {
                binding.title.setText("已完成");
                binding.pay.setVisibility(View.GONE);
            }
            case "1" -> {
                binding.title.setText("付款完成,待商家审核!");
                binding.pay.setVisibility(View.GONE);
            }
        }
    }

    private void initTime(String time) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        try {
            LocalDateTime dateTime = LocalDateTime.parse(time, formatter);
            LocalDateTime now = LocalDateTime.now();
            Duration duration = Duration.between(now, dateTime);
            long diff = duration.toMillis();
            if (countDownTimer != null) {
                countDownTimer.cancel();
                countDownTimer = null;
            }
            if (diff <= 0) {
                return;
            }
            countDownTimer = new CountDownTimer(diff, 1_000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    long seconds = millisUntilFinished / 1000;
                    long minutes = seconds / 60;
                    long remainingSeconds = seconds % 60;
                    String formattedTime = String.format(Locale.US, "%s:%s", minutes, remainingSeconds);
                    binding.Time1.setText(formattedTime);
                    binding.Time1.setVisibility(View.VISIBLE);
                }

                @Override
                public void onFinish() {
                    binding.title.setText("订单已超时");
                    binding.Time1.setVisibility(View.GONE);
                    binding.pay.setVisibility(View.GONE);
                }
            }.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }
}
