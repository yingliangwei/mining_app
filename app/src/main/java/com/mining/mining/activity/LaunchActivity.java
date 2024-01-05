package com.mining.mining.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.WindowInsets;
import android.view.WindowInsetsController;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.fastjson2.JSONObject;
import com.mining.mining.activity.login.LoginActivity;
import com.mining.mining.databinding.ActivityLaunchBinding;
import com.mining.mining.util.PhoneUtil;
import com.mining.util.StatusBarUtil;
import com.xframe.network.OnData;
import com.xframe.network.SocketManage;

@SuppressLint("CustomSplashScreen")
public class LaunchActivity extends AppCompatActivity implements OnData {
    private final JumpActivity jumpActivity = new JumpActivity(3000, 1000);
    private ActivityLaunchBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtil.setImmersiveStatusBar(this, true);
        binding = ActivityLaunchBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        SocketManage.init(this);
        jumpActivity.start();
    }


    private void JumpActivity() {
        SharedPreferences sharedPreferences = getSharedPreferences("user", MODE_PRIVATE);
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
    public void connect(SocketManage socketManage) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("type", 9);
        jsonObject.put("code", 1);
        jsonObject.put("data", getJson());
        jsonObject.put("app", getVersion());
        socketManage.print(jsonObject.toString());
    }

    private String getVersion() {
        JSONObject jsonObject = new JSONObject();
        PackageManager packageManager = getPackageManager();
        String name = getPackageName();
        try {
            PackageInfo info = packageManager.getPackageInfo(name, 0);
            jsonObject.put("versionName", info.versionName);
            jsonObject.put("VersionCode", info.getLongVersionCode());
        } catch (PackageManager.NameNotFoundException e) {
            e.fillInStackTrace();
        }
        jsonObject.put("name", name);
        return jsonObject.toString();
    }

    private String getJson() {
        // 获取设备的详细信息
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        String androidVersion = Build.VERSION.RELEASE;
        String sdkVersion = String.valueOf(Build.VERSION.SDK_INT);
        JSONObject json = new JSONObject();
        json.put("manufacturer", manufacturer);
        json.put("model", model);
        json.put("androidVersion", androidVersion);
        json.put("sdkVersion", sdkVersion);
        json.put("deviceId", PhoneUtil.getDeviceId(this));
        return json.toString();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        jumpActivity.cancel();
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

    private class JumpActivity extends CountDownTimer {

        public JumpActivity(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {

        }

        @Override
        public void onFinish() {
            JumpActivity();
        }
    }
}
