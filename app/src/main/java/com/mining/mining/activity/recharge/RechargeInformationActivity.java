package com.mining.mining.activity.recharge;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.fastjson2.JSONObject;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.mining.mining.databinding.ActivityRechargeInformationBinding;
import com.mining.util.StatusBarUtil;
import com.mining.util.StringUtil;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class RechargeInformationActivity extends AppCompatActivity implements Runnable {
    private ActivityRechargeInformationBinding binding;
    private String json;
    private CountDownTimer countDownTimer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtil.setImmersiveStatusBar(this, true);
        binding = ActivityRechargeInformationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initToolbar();
        initData();
        new Thread(this).start();
    }

    private void initData() {
        json = getIntent().getStringExtra("json");
        if (json == null) {
            return;
        }
        JSONObject jsonObject = JSONObject.parseObject(json);
        String usdt = jsonObject.getString("usdt");
        String type = jsonObject.getString("type");
        String uid = jsonObject.getString("uid");
        String address_type = jsonObject.getString("address_type");
        String address = jsonObject.getString("address");
        String time = jsonObject.getString("time");
        initTime(time);
        binding.address.setText(address);
        binding.addressType.setText(address_type);
        binding.uid.setText(uid);
        binding.usdt.setText(StringUtil.toRe(usdt));
        if (type.equals("0")) {
            binding.type.setText("等待充币到账");
        } else {
            binding.type.setText("充值成功");
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
                    String formattedTime = String.format("%02d:%02d", minutes, remainingSeconds);
                    binding.time.setText(formattedTime);
                }

                @Override
                public void onFinish() {
                    binding.time.setText("0");
                    binding.type.setText("已过期，切勿充值");
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

    private void initToolbar() {
        binding.toolbar.setNavigationOnClickListener(v -> finish());
    }

    @Override
    public void run() {
        if (json == null) {
            return;
        }
        JSONObject jsonObject = JSONObject.parseObject(json);
        if (jsonObject == null) {
            return;
        }
        String address = jsonObject.getString("address");
        initCode(address, 400);
    }

    private void initCode(String qrCodeData, int qrCodeImageWidth) {
        try {
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(qrCodeData, BarcodeFormat.QR_CODE, qrCodeImageWidth, qrCodeImageWidth);
            Bitmap bitmap = Bitmap.createBitmap(qrCodeImageWidth, qrCodeImageWidth, Bitmap.Config.RGB_565);
            for (int x = 0; x < qrCodeImageWidth; x++) {
                for (int y = 0; y < qrCodeImageWidth; y++) {
                    bitmap.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                }
            }
            runOnUiThread(() -> binding.image.setImageBitmap(bitmap));
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }
}
