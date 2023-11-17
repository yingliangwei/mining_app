package com.mining.mining.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowInsetsController;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.fastjson2.JSONObject;
import com.bumptech.glide.Glide;
import com.mining.mining.R;
import com.mining.mining.activity.login.LoginActivity;
import com.mining.mining.databinding.ActivityLaunchBinding;
import com.mining.mining.util.SharedUtil;
import com.mining.util.StatusBarUtil;
import com.xframe.network.OnData;
import com.xframe.network.SocketManage;

@SuppressLint("CustomSplashScreen")
public class LaunchActivity extends AppCompatActivity implements View.OnClickListener, OnData {
    private ActivityLaunchBinding binding;
    private CountDownTimer countDownTimer;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = getSharedPreferences("user", MODE_PRIVATE);
        StatusBarUtil.setImmersiveStatusBar(this, true);
        binding = ActivityLaunchBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initJump();
        SocketManage.init(this);
    }

    private void initJump() {
        binding.jump.setOnClickListener(this);
        countDownTimer = new CountDownTimer(5_000, 1_000) {
            @Override
            public void onTick(long millisUntilFinished) {
                if (isFinishing()) {
                    countDownTimer.cancel();
                } else {
                    long time = millisUntilFinished / 1000;
                    // 执行您的逻辑
                    binding.jump.setText(String.format(getString(R.string.app_jump) + " %d", time));
                }
            }

            @Override
            public void onFinish() {
                // 计时器结束后的逻辑
                JumpActivity();
            }
        }.start();
    }

    private void JumpActivity() {
        String id = sharedPreferences.getString("id", null);
        String key = sharedPreferences.getString("_key", null);
        if (id == null || key == null) {
            startActivity(new Intent(LaunchActivity.this, LoginActivity.class));
            finish();
            return;
        }
        startActivity(new Intent(LaunchActivity.this, MainActivity.class));
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        countDownTimer.cancel();
    }


    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            hideSystemUI();
        }
    }

    private void hideSystemUI() {
        WindowInsetsController controller = getWindow().getInsetsController();
        if (controller == null) {
            return;
        }
        controller.hide(WindowInsets.Type.navigationBars());
    }

    @Override
    public void connect(SocketManage socketManage) {
        SharedUtil sharedUtil = new SharedUtil(this);
        JSONObject jsonObject = sharedUtil.getLogin(9, 1);
        jsonObject.put("data", getJson());
        socketManage.print(jsonObject.toString());
    }

    private String getJson() {
        // 获取设备的详细信息
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        String androidVersion = Build.VERSION.RELEASE;
        String sdkVersion = String.valueOf(Build.VERSION.SDK_INT);
        // 获取设备的唯一标识
        String deviceId = getDeviceId(this);
        // 将详细信息和唯一标识转换为JSON格式
        JSONObject json = new JSONObject();
        json.put("manufacturer", manufacturer);
        json.put("model", model);
        json.put("androidVersion", androidVersion);
        json.put("sdkVersion", sdkVersion);
        json.put("deviceId", deviceId);
        return json.toString();
    }

    private String getDeviceId(@NonNull Context context) {
        String deviceId = null;
        try {
            // 获取设备的唯一标识
            PackageManager packageManager = context.getPackageManager();
            String[] packages = packageManager.getPackagesForUid(android.os.Process.myUid());
            if (packages != null && packages.length > 0) {
                String packageName = packages[0];
                deviceId = packageManager.getPackageInfo(packageName, 0).firstInstallTime + "";
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.e("", "Failed to get device ID", e);
        }
        return deviceId;
    }

    private void initData(JSONObject data) {
        String image = data.getString("image");
        String name = data.getString("name");
        try {
            Glide.with(this).load(image).into(binding.preloadDiagram);
        } catch (Exception e) {
            e.fillInStackTrace();
        }
        binding.ColorB.setText(name);
        try {
            String color_a = data.getString("color_a");
            String color_b = data.getString("color_b");
            binding.ColorA.setTextColor(Color.parseColor(color_a));
            binding.ColorB.setTextColor(Color.parseColor(color_b));
        } catch (Exception e) {
            e.fillInStackTrace();
        }
    }

    @Override
    public void handle(String ds) {
        JSONObject jsonObject = JSONObject.parseObject(ds);
        JSONObject data = jsonObject.getJSONObject("data");
        initData(data);
    }

    @Override
    public void onClick(View v) {
        countDownTimer.cancel();
        JumpActivity();
    }
}
