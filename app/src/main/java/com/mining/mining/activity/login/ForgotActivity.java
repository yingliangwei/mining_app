package com.mining.mining.activity.login;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.fastjson2.JSONObject;
import com.mining.mining.R;
import com.mining.mining.databinding.ActivityForgotBinding;
import com.mining.mining.util.StringUtil;
import com.mining.util.StatusBarUtil;
import com.xframe.network.OnData;
import com.xframe.network.SocketManage;


public class ForgotActivity extends AppCompatActivity implements View.OnClickListener, OnData, CompoundButton.OnCheckedChangeListener {
    private ActivityForgotBinding binding;
    private final CountDownTimer timer = new CountDown(59_000, 1_000);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtil.setImmersive(this, true);
        binding = ActivityForgotBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initView();
    }

    private void initView() {
        binding.getVerify.setOnClickListener(this);
        binding.post.setOnClickListener(this);
        binding.togglePwd.setOnCheckedChangeListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.getVerify) {
            String phone = binding.phone.getText().toString();
            if (phone.length() == 0) {
                Toast.makeText(this, getString(R.string.toast_phone_null), Toast.LENGTH_SHORT).show();
                return;
            }
            timer.start();
            SocketManage.init(new getVerify());
        } else if (v.getId() == R.id.post) {
            if (StringUtil.isText(binding.pass) == 0 || StringUtil.isText(binding.phone) == 0 || StringUtil.isText(binding.verify) == 0) {
                Toast.makeText(this, "检查信息是否为空", Toast.LENGTH_SHORT).show();
                return;
            }
            SocketManage.init(this);
        }
    }

    @Override
    public void connect(SocketManage socketManage) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("type", 17);
        jsonObject.put("code", 2);
        jsonObject.put("phone", binding.phone.getText().toString());
        jsonObject.put("verify", binding.verify.getText().toString());
        jsonObject.put("pass", binding.pass.getText().toString());
        socketManage.print(jsonObject.toString());
    }

    @Override
    public void handle(String ds) {
        JSONObject jsonObject = JSONObject.parseObject(ds);
        String msg = jsonObject.getString("msg");
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        int code = jsonObject.getInteger("code");
        if (code == 200) {
            finish();
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            //如果选中，显示密码
            binding.pass.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
        } else {
            //否则隐藏密码
            binding.pass.setTransformationMethod(PasswordTransformationMethod.getInstance());
        }
    }

    private class getVerify implements OnData {
        @Override
        public void handle(String ds) {
            JSONObject jsonObject = JSONObject.parseObject(ds);
            String msg = jsonObject.getString("msg");
            Toast.makeText(ForgotActivity.this, msg, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void connect(SocketManage socketManage) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("type", 17);
            jsonObject.put("code", 1);
            jsonObject.put("phone", binding.phone.getText().toString());
            socketManage.print(jsonObject.toString());
        }
    }

    private class CountDown extends CountDownTimer {

        /**
         * @param millisInFuture    The number of millis in the future from the call
         *                          to {@link #start()} until the countdown is done and {@link #onFinish()}
         *                          is called.
         * @param countDownInterval The interval along the way to receive
         *                          {@link #onTick(long)} callbacks.
         */
        public CountDown(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            long time = millisUntilFinished / 1000;
            binding.getVerify.setText(String.format("等待%s", time));
        }

        @Override
        public void onFinish() {
            binding.getVerify.setText(getString(R.string.app_get_verify));
        }
    }
}
