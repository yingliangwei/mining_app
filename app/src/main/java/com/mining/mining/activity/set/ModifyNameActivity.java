package com.mining.mining.activity.set;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.mining.mining.R;
import com.mining.mining.activity.login.LoginActivity;
import com.mining.mining.databinding.ActivityModifyNameBinding;
import com.mining.util.Handler;
import com.mining.util.OnHandler;
import com.mining.util.StatusBarUtil;
import com.xframe.network.OnData;
import com.xframe.network.SocketManage;

import org.json.JSONObject;

public class ModifyNameActivity extends AppCompatActivity implements OnData, OnHandler, View.OnClickListener {
    private ActivityModifyNameBinding binding;
    private final Handler handler = new Handler(Looper.getMainLooper(), this);
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtil.setImmersiveStatusBar(this, true);
        sharedPreferences = getSharedPreferences("user", Context.MODE_PRIVATE);
        binding = ActivityModifyNameBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initToolbar();
        initView();
        String name = getIntent().getStringExtra("name");
        if (name != null) {
            binding.name.setText(name);
        }
    }

    private void initView() {
        binding.sava.setOnClickListener(this);
    }

    private void initToolbar() {
        binding.toolbar.setNavigationOnClickListener(v -> finish());
    }

    @Override
    public void connect(SocketManage socketManage) {
        try {
            String id = sharedPreferences.getString("id", null);
            String _key = sharedPreferences.getString("_key", null);
            if (id == null || _key == null) {
                LoginActivity.login(this);
                return;
            }
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("type", 7);
            jsonObject.put("code", 2);
            jsonObject.put("name", binding.name.getText().toString());
            jsonObject.put("id", id);
            jsonObject.put("_key", _key);
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
            if (code == 202) {
                LoginActivity.login(this);
            }
            handler.sendMessage(0, msg);
        } catch (Exception e) {
            e.fillInStackTrace();
        }
    }

    @Override
    public void handleMessage(int w, String str) {
        if (w == 0) {
            Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.sava) {
            String name = binding.name.getText().toString();
            if (name.length() == 0) {
                Toast.makeText(this, "昵称不能为空", Toast.LENGTH_SHORT).show();
                return;
            }
            SocketManage.init(this);
        }
    }
}
