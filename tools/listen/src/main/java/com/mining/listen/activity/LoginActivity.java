package com.mining.listen.activity;

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
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.mining.listen.R;
import com.mining.listen.databinding.ActivityLoginBinding;
import com.mining.listen.util.SharedUtil;
import com.mining.util.StatusBarUtil;
import com.xframe.network.OnData;
import com.xframe.network.SocketManage;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity implements OnData, View.OnClickListener, ActivityResultCallback<ActivityResult>, CompoundButton.OnCheckedChangeListener {
    private ActivityLoginBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtil.setImmersiveStatusBar(this, true);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        isLogin();
        initView();
    }

    private void isLogin() {
        SharedUtil sharedUtil = new SharedUtil(this);
        if (sharedUtil.isLogin()) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
    }

    private void initView() {
        binding.login.setOnClickListener(this);
        binding.togglePwd.setOnCheckedChangeListener(this);
    }

    private void Login() {
        String user_id = binding.userId.getText().toString();
        String pass = binding.pass.getText().toString();
        if (user_id.length() == 0 || pass.length() == 0) {
            Toast.makeText(this, "密码不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        SocketManage.init(this);
    }

    @Override
    public void connect(SocketManage socketManage) {
        String user_id = binding.userId.getText().toString();
        String pass = binding.pass.getText().toString();
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("type", 1);
            jsonObject.put("user_id", user_id);
            jsonObject.put("pass", pass);
            socketManage.print(jsonObject.toString());
        } catch (Exception e) {
            e.fillInStackTrace();
        }
    }

    @Override
    public void handle(String ds) {
        try {
            JSONObject jsonObject = new JSONObject(ds);
            int code = jsonObject.getInt("code");
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
        } catch (JSONException e) {
            throw new RuntimeException(e);
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

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.login) {
            Login();
        }
    }
}
