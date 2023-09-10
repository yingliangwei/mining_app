package com.mining.mining.activity.wallet;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.mining.mining.R;
import com.mining.mining.activity.login.LoginActivity;
import com.mining.mining.databinding.ActivityWalletBinding;
import com.mining.mining.util.Handler;
import com.mining.mining.util.OnHandler;
import com.mining.mining.util.StatusBarUtil;
import com.xframe.network.OnData;
import com.xframe.network.SocketManage;

import org.json.JSONException;
import org.json.JSONObject;

public class WalletActivity extends AppCompatActivity implements OnData, OnHandler, View.OnClickListener {
    private ActivityWalletBinding binding;
    private final Handler handler = new Handler(Looper.myLooper(), this);
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtil.setImmersiveStatusBar(this, true);
        sharedPreferences = getSharedPreferences("user", Context.MODE_PRIVATE);
        binding = ActivityWalletBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initToolbar();
        initView();
        SocketManage.init(this);
    }

    private void initView() {
        binding.usdtL.setOnClickListener(this);
    }

    private void initToolbar() {
        binding.toolbar.setNavigationOnClickListener(v -> finish());
    }

    @Override
    public void connect(SocketManage socketManage) {
        String id = sharedPreferences.getString("id", null);
        String _key = sharedPreferences.getString("_key", null);
        if (id == null || _key == null) {
            LoginActivity.login(this);
            return;
        }
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("type", 5);
            jsonObject.put("code", 1);
            jsonObject.put("id", id);
            jsonObject.put("_key", _key);
            socketManage.print(jsonObject.toString());
        } catch (Exception e) {
            e.fillInStackTrace();
        }
    }

    @Override
    public void handle(String ds) {
        System.out.println(ds);
        try {
            JSONObject jsonObject = new JSONObject(ds);
            int code = jsonObject.getInt("code");
            if (code == 200) {
                JSONObject data = jsonObject.getJSONObject("data");
                handler.sendMessage(1, data.toString());
            } else if (code == 202) {
                String msg = jsonObject.getString("msg");
                handler.sendMessage(0, msg);
            }
        } catch (Exception e) {
            e.fillInStackTrace();
        }
    }

    @Override
    public void handleMessage(int w, String str) {
        if (w == 0) {
            Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
        } else if (w == 1) {
            try {
                JSONObject data = new JSONObject(str);
                initData(data);
            } catch (Exception e) {
                e.fillInStackTrace();
            }
        }
    }

    private void initData(JSONObject data) throws JSONException {
        String usdt = data.getString("usdt");
        String gme = data.getString("gem");
        String stone = data.getString("stone");
        binding.gem.setText(gme);
        binding.usdt.setText(usdt);
        binding.stone.setText(stone);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.usdt_l) {
            startActivity(new Intent(this, UsdtDetailActivity.class));
        }
    }
}
