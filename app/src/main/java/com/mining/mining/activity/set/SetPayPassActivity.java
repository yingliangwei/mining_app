package com.mining.mining.activity.set;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.mining.mining.R;
import com.mining.mining.activity.login.LoginActivity;
import com.mining.mining.databinding.ActivitySetPayPassBinding;
import com.mining.util.Handler;
import com.mining.util.OnHandler;
import com.xframe.network.OnData;
import com.xframe.network.SocketManage;

import org.json.JSONObject;

public class SetPayPassActivity extends AppCompatActivity implements OnData, OnHandler, View.OnClickListener {
    private ActivitySetPayPassBinding binding;
    private final Handler handler = new Handler(Looper.getMainLooper(), this);
    private final OnData onData = new OnData() {
        @Override
        public void handle(String ds) {
            try {
                JSONObject jsonObject = new JSONObject(ds);
                String msg = jsonObject.getString("msg");
                handler.sendMessage(0, msg);
            } catch (Exception e) {
                e.fillInStackTrace();
            }
        }

        @Override
        public void connect(SocketManage socketManage) {
            try {
                String id = sharedPreferences.getString("id", null);
                String _key = sharedPreferences.getString("_key", null);
                if (id == null || _key == null) {
                    LoginActivity.login(SetPayPassActivity.this);
                    return;
                }
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("type", 7);
                jsonObject.put("code", 5);
                jsonObject.put("id", id);
                jsonObject.put("_key", _key);
                jsonObject.put("_pass", binding.pass1.getText().toString());
                jsonObject.put("pass", binding.pass.getText().toString());
            } catch (Exception e) {
                e.fillInStackTrace();
            }
        }
    };
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = getSharedPreferences("user", Context.MODE_PRIVATE);
        binding = ActivitySetPayPassBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initToolbar();
        initView();
    }

    private void initView() {
        binding.post.setOnClickListener(this);
    }

    private void initToolbar() {
        binding.toolbar.setNavigationOnClickListener(v -> finish());
    }

    @Override
    public void handleMessage(int w, String str) {
        if (w == 0) {

        } else if (w == 1) {
            try {
                JSONObject jsonObject = new JSONObject(str);
                int code = jsonObject.getInt("code");
                if (code == 200) {
                    String is = jsonObject.getString("is");
                    if (is.equals("0")) {
                        binding.passL.setVisibility(View.GONE);
                    } else {
                        binding.passL.setVisibility(View.VISIBLE);
                    }
                }
            } catch (Exception e) {
                e.fillInStackTrace();
            }
        }
    }

    @Override
    public void handle(String ds) {
        handler.sendMessage(1, ds);
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
            jsonObject.put("code", 4);
            jsonObject.put("id", id);
            jsonObject.put("_key", _key);
        } catch (Exception e) {
            e.fillInStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.post) {
            SocketManage.init(onData);
        }
    }
}
