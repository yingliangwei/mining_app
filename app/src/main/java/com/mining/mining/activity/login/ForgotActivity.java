package com.mining.mining.activity.login;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.mining.mining.R;
import com.mining.mining.databinding.ActivityForgotBinding;
import com.mining.mining.util.StringUtil;
import com.xframe.network.OnData;
import com.xframe.network.SocketManage;

import org.json.JSONObject;

public class ForgotActivity extends AppCompatActivity implements View.OnClickListener, OnData {
    private ActivityForgotBinding binding;
    private final OnData onData = new OnData() {
        @Override
        public void handle(String ds) {
            try {
                JSONObject jsonObject = new JSONObject(ds);
                String msg = jsonObject.getString("msg");
                Toast.makeText(ForgotActivity.this, msg, Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                e.fillInStackTrace();
            }
        }

        @Override
        public void connect(SocketManage socketManage) {
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("type", 17);
                jsonObject.put("code", 1);
                jsonObject.put("phone", binding.phone.getText().toString());
                socketManage.print(jsonObject.toString());
            } catch (Exception e) {
                e.fillInStackTrace();
            }
        }
    };

    private final CountDownTimer timer = new CountDownTimer(59_000, 1_000) {
        @Override
        public void onTick(long millisUntilFinished) {
            long time = millisUntilFinished / 1000;
            binding.getVerify.setText(String.format("等待%s", time));
        }

        @Override
        public void onFinish() {
            binding.getVerify.setText(getString(R.string.app_get_verify));
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityForgotBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initView();
    }

    private void initView() {
        binding.getVerify.setOnClickListener(this);
        binding.post.setOnClickListener(this);
        binding.togglePwd.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                //如果选中，显示密码
                binding.pass.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            } else {
                //否则隐藏密码
                binding.pass.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }
        });
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
            SocketManage.init(onData);
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
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("type", 17);
            jsonObject.put("code", 2);
            jsonObject.put("phone", binding.phone.getText().toString());
            jsonObject.put("verify", binding.verify.getText().toString());
            jsonObject.put("pass", binding.pass.getText().toString());
            socketManage.print(jsonObject.toString());
        } catch (Exception e) {
            e.fillInStackTrace();
        }
    }

    @Override
    public void handle(String ds) {
        try {
            JSONObject jsonObject = new JSONObject(ds);
            String msg = jsonObject.getString("msg");
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
            int code = jsonObject.getInt("code");
            if (code == 200) {
                finish();
            }
        } catch (Exception e) {
            e.fillInStackTrace();
        }
    }
}
