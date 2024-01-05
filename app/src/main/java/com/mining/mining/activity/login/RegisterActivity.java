package com.mining.mining.activity.login;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.fastjson2.JSONObject;
import com.mining.mining.R;
import com.mining.mining.databinding.ActivityRegisterBinding;
import com.mining.util.StatusBarUtil;
import com.xframe.network.OnData;
import com.xframe.network.SocketManage;


public class RegisterActivity extends AppCompatActivity implements View.OnClickListener, OnData {
    private ActivityRegisterBinding binding;
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

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtil.setImmersiveStatusBar(this, true);
        sharedPreferences = getSharedPreferences("user", MODE_PRIVATE);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initView();
    }

    private void initView() {
        binding.getVerify.setOnClickListener(this);
        binding.register.setOnClickListener(this);
        binding.togglePwd.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    //如果选中，显示密码
                    binding.pass.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    setSelection(binding.pass);
                } else {
                    //否则隐藏密码
                    binding.pass.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    setSelection(binding.pass);
                }
            }
        });
        binding.togglePwd1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    //如果选中，显示密码
                    binding.pass1.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    setSelection(binding.pass1);
                } else {
                    //否则隐藏密码
                    binding.pass1.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    setSelection(binding.pass1);
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        timer.cancel();
    }

    @Override
    public void connect(SocketManage socketManage) {
        String phone = binding.phone.getText().toString();
        String verify = binding.verify.getText().toString();
        String pass = binding.pass.getText().toString();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("type", 2);
        jsonObject.put("code", 2);
        jsonObject.put("phone", phone);
        jsonObject.put("verify", verify);
        jsonObject.put("pass", pass);
        socketManage.print(jsonObject.toString());
    }

    @Override
    public void handle(String ds) {
        JSONObject jsonObject = JSONObject.parseObject(ds);
        int code = jsonObject.getInteger("code");
        if (code == 200) {
            SharedPreferences.Editor edit = sharedPreferences.edit();
            edit.putString("_key", jsonObject.getString("_key"));
            edit.putString("id", jsonObject.getString("id"));
            edit.apply();
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            setResult(RESULT_OK, intent);
            finish();
        } else if (code == 201) {
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            setResult(RESULT_CANCELED, intent);
            finish();
        }
        String msg = jsonObject.getString("msg");
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
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
            SocketManage.init(new getVerifyData());
        } else if (v.getId() == R.id.register) {
            String phone = binding.phone.getText().toString();
            String verify = binding.verify.getText().toString();
            String pass = binding.pass.getText().toString();
            String pass1 = binding.pass1.getText().toString();
            if (phone.length() == 0 || verify.length() == 0 || pass.length() == 0 || pass1.length() == 0) {
                Toast.makeText(this, "请检查内容是否为空", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!pass.equals(pass1)) {
                Toast.makeText(this, "两次密码不一样", Toast.LENGTH_SHORT).show();
                return;
            }
            SocketManage.init(this);
        }
    }


    private void setSelection(EditText editText) {
        editText.setSelection(editText.getText().length());
    }

    private class getVerifyData implements OnData {
        @Override
        public void connect(SocketManage socketManage) {
            String phone = binding.phone.getText().toString();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("type", 2);
            jsonObject.put("code", 1);
            jsonObject.put("phone", phone);
            System.out.println(jsonObject);
            socketManage.print(jsonObject.toString());
        }

        @Override
        public void handle(String ds) {
            JSONObject jsonObject = JSONObject.parseObject(ds);
            int code = jsonObject.getInteger("code");
            String msg = jsonObject.getString("msg");
            Toast.makeText(RegisterActivity.this, msg, Toast.LENGTH_SHORT).show();
            if (code != 200) {
                timer.cancel();
                binding.getVerify.setText(getString(R.string.app_get_verify));
            }
        }
    }

}
