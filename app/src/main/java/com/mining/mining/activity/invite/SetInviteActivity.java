package com.mining.mining.activity.invite;

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
import com.mining.mining.databinding.ActivityInviteSetBinding;
import com.mining.util.Handler;
import com.mining.util.OnHandler;
import com.mining.util.StatusBarUtil;
import com.xframe.network.OnData;
import com.xframe.network.SocketManage;

import org.json.JSONObject;

public class SetInviteActivity extends AppCompatActivity implements OnData, OnHandler, View.OnClickListener {
    private final Handler handler = new Handler(Looper.getMainLooper(), this);
    private ActivityInviteSetBinding binding;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtil.setImmersiveStatusBar(this, true);
        sharedPreferences = getSharedPreferences("user", Context.MODE_PRIVATE);
        binding = ActivityInviteSetBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initView();
    }

    private void initView() {
        binding.sava.setOnClickListener(this);
    }

    @Override
    public void connect(SocketManage socketManage) {
        try {
            String id = sharedPreferences.getString("id", null);
            String _key = sharedPreferences.getString("_key", null);
            if (id == null || _key == null) {
                LoginActivity.login(SetInviteActivity.this);
                return;
            }
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("type", 11);
            jsonObject.put("code", 2);
            jsonObject.put("id", id);
            jsonObject.put("_key", _key);
            jsonObject.put("uid", binding.uid.getText().toString());
            socketManage.print(jsonObject.toString());
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
    public void handle(String ds) {
        try {
            JSONObject jsonObject = new JSONObject(ds);
            //int code = jsonObject.getInt("code");
            String msg = jsonObject.getString("msg");
            handler.sendMessage(0, msg);
        } catch (Exception e) {
            e.fillInStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.sava) {
            SocketManage.init(this);
        }
    }

}
