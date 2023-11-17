package com.mining.mining.activity.login;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.fastjson2.JSONObject;
import com.mining.mining.R;
import com.mining.mining.activity.MainActivity;
import com.mining.mining.databinding.ActivityLoginBinding;
import com.mining.mining.util.SharedUtil;
import com.mining.util.StatusBarUtil;
import com.xframe.network.OnData;
import com.xframe.network.SocketManage;

public class LoginActivity extends AppCompatActivity implements OnData, View.OnClickListener, ActivityResultCallback<ActivityResult>, CompoundButton.OnCheckedChangeListener {
    private ActivityLoginBinding binding;
    private ActivityResultLauncher<Intent> resultLauncher;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtil.setImmersiveStatusBar(this, true);
        resultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), this);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initView();
    }

    private void initView() {
        binding.forgot.setOnClickListener(this);
        binding.login.setOnClickListener(this);
        binding.register.setOnClickListener(this);
        binding.togglePwd.setOnCheckedChangeListener(this);
    }

    private void Login() {
        String user_id = binding.userId.getText().toString();
        String pass = binding.pass.getText().toString();
        if (user_id.length() == 0 || pass.length() == 0) {
            Toast.makeText(this, getString(R.string.toast_login), Toast.LENGTH_SHORT).show();
            return;
        }
        SocketManage.init(this);
    }

    @Override
    public void connect(SocketManage socketManage) {
        String user_id = binding.userId.getText().toString();
        String pass = binding.pass.getText().toString();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("type", 1);
        jsonObject.put("user_id", user_id);
        jsonObject.put("pass", pass);
        socketManage.print(jsonObject.toString());
    }

    @Override
    public void handle(String ds) {
        JSONObject jsonObject = JSONObject.parseObject(ds);
        int code = jsonObject.getInteger("code");
        String msg = jsonObject.getString("msg");
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        if (code == 200) {
            SharedPreferences sharedPreferences = getSharedPreferences("user", MODE_PRIVATE);
            SharedPreferences.Editor edit = sharedPreferences.edit();
            edit.putString("_key", jsonObject.getString("_key"));
            edit.putString("id", jsonObject.getString("id"));
            edit.apply();
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public void onActivityResult(ActivityResult result) {
        int resultCode = result.getResultCode();
        if (resultCode == RESULT_OK) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }


    public static void login(Activity context) {
        context.startActivity(new Intent(context, LoginActivity.class));
        context.finish();
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            //如果选中，显示密码
            binding.pass.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            binding.pass.setSelection(binding.pass.getText().length(), binding.pass.getText().length());
        } else {
            //否则隐藏密码
            binding.pass.setTransformationMethod(PasswordTransformationMethod.getInstance());
            binding.pass.setSelection(binding.pass.getText().length(), binding.pass.getText().length());
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.login) {
            Login();
        } else if (v.getId() == R.id.register) {
            Intent intent = new Intent(this, RegisterActivity.class);
            resultLauncher.launch(intent);
        } else if (v.getId() == R.id.forgot) {
            Intent intent = new Intent(this, ForgotActivity.class);
            startActivity(intent);
        }
    }
}
