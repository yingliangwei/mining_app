package com.mining.mining.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowInsetsController;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.mining.mining.R;
import com.mining.mining.activity.login.LoginActivity;
import com.mining.mining.databinding.ActivityLaunchBinding;
import com.mining.mining.util.StatusBarUtil;

@SuppressLint("CustomSplashScreen")
public class LaunchActivity extends AppCompatActivity implements View.OnClickListener {
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
    public void onClick(View v) {
        JumpActivity();
    }
}
