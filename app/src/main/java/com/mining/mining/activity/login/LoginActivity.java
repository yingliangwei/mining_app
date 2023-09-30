package com.mining.mining.activity.login;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Looper;
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

import com.mining.mining.R;
import com.mining.mining.activity.MainActivity;
import com.mining.mining.databinding.ActivityLoginBinding;
import com.mining.util.Handler;
import com.mining.util.OnHandler;
import com.mining.util.StatusBarUtil;
import com.xframe.network.OnData;
import com.xframe.network.SocketManage;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity implements OnData, View.OnClickListener, OnHandler, ActivityResultCallback<ActivityResult> {
    private ActivityLoginBinding binding;
    private final Handler handler = new Handler(Looper.getMainLooper(), this);
    private SharedPreferences sharedPreferences;
    private ActivityResultLauncher<Intent> resultLauncher;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtil.setImmersiveStatusBar(this, true);
        resultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), this);
        sharedPreferences = getSharedPreferences("user", MODE_PRIVATE);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initToolbar();
        initView();
    }

    private void initView() {
        binding.login.setOnClickListener(this);
        binding.register.setOnClickListener(this);
        binding.togglePwd.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
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
        });
    }

    private void initToolbar() {
        binding.toolbar.setNavigationOnClickListener(v -> finish());
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.login) {
            Login();
        } else if (v.getId() == R.id.register) {
            Intent intent = new Intent(this, RegisterActivity.class);
            resultLauncher.launch(intent);
        }
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
            handler.sendMessage(0, msg);
            if (code == 200) {
                SharedPreferences.Editor edit = sharedPreferences.edit();
                edit.putString("_key", jsonObject.getString("_key"));
                edit.putString("id", jsonObject.getString("id"));
                edit.apply();
                handler.sendMessage(1, "");
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void handleMessage(int w, String str) {
        if (w == 0) {
            Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
        } else if (w == 1) {
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

}
