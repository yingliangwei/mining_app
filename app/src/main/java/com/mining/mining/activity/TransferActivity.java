package com.mining.mining.activity;

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
import com.mining.mining.databinding.ActivityTransferBinding;
import com.mining.util.Handler;
import com.mining.util.OnHandler;
import com.mining.util.StatusBarUtil;
import com.mining.util.StringUtil;
import com.xframe.network.OnData;
import com.xframe.network.SocketManage;
import com.xframe.widget.PayPass;

import org.json.JSONObject;

public class TransferActivity extends AppCompatActivity implements OnData, OnHandler, PayPass.OnPay, View.OnClickListener {
    private ActivityTransferBinding binding;
    private final Handler handler = new Handler(Looper.getMainLooper(), this);
    private String pass;
    private final OnData onData = new OnData() {
        @Override
        public void connect(SocketManage socketManage) {
            try {
                String id = sharedPreferences.getString("id", null);
                String _key = sharedPreferences.getString("_key", null);
                if (id == null || _key == null) {
                    LoginActivity.login(TransferActivity.this);
                    return;
                }
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("type", 14);
                jsonObject.put("code", 1);
                jsonObject.put("id", id);
                jsonObject.put("uid", binding.uid.getText().toString());
                jsonObject.put("_key", _key);
                socketManage.print(jsonObject.toString());
            } catch (Exception e) {
                e.fillInStackTrace();
            }
        }

        @Override
        public void handle(String ds) {
            handler.sendMessage(1, ds);
        }
    };

    private final OnData getUsdt = new OnData() {
        @Override
        public void handle(String ds) {
            try {
                JSONObject jsonObject = new JSONObject(ds);
                int code = jsonObject.getInt("code");
                if (code == 200) {
                    String usdt = jsonObject.getString("usdt");
                    handler.sendMessage(2, usdt);
                } else {
                    String msg = jsonObject.getString("msg");
                    handler.sendMessage(0, msg);
                }
            } catch (Exception e) {
                e.fillInStackTrace();
            }
        }

        @Override
        public void connect(SocketManage socketManage) {
            try {
                String id = sharedPreferences.getString("id", null);
                String _key = sharedPreferences.getString("_key", null);
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("type", 5);
                jsonObject.put("code", 2);
                jsonObject.put("id", id);
                jsonObject.put("_key", _key);
                socketManage.print(jsonObject.toString());
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
        StatusBarUtil.setImmersive(this, true);
        binding = ActivityTransferBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initToolbar();
        initView();
        SocketManage.init(getUsdt);
    }


    private void initView() {
        binding.userInformation.setOnClickListener(this);
        binding.transfer.setOnClickListener(this);
    }

    @Override
    public void handleMessage(int w, String str) {
        if (w == 0) {
            Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
        } else if (w == 1) {
            initUser(str);
        } else if (w == 2) {
            binding.userMoney.setText(getString(R.string.app_available_usdt, str));
        }
    }


    private void initUser(String str) {
        try {
            JSONObject object = new JSONObject(str);
            int code = object.getInt("code");
            if (code == 200) {
                JSONObject jsonObject = object.getJSONObject("data");
                String name = jsonObject.getString("name");
                String id = jsonObject.getString("id");
                binding.name.setText(name);
                binding.id.setText(id);
                binding.nameX.setText(StringUtil.getStringStart(name));
                return;
            }
            String msg = object.getString("msg");
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.fillInStackTrace();
        }
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
                LoginActivity.login(TransferActivity.this);
                return;
            }
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("type", 14);
            jsonObject.put("code", 2);
            jsonObject.put("id", id);
            jsonObject.put("uid", binding.uid.getText().toString());
            jsonObject.put("_key", _key);
            jsonObject.put("pass", pass);
            jsonObject.put("money", binding.money.getText().toString());
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
        } catch (Exception e) {
            e.fillInStackTrace();
        }
    }

    @Override
    public void onText(String pass) {
        this.pass = pass;
        SocketManage.init(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.user_information) {
            if (binding.uid.getText().length() == 0) {
                Toast.makeText(this, "转账账号不能为空", Toast.LENGTH_SHORT).show();
                return;
            }
            SocketManage.init(onData);
        } else if (v.getId() == R.id.transfer) {
            PayPass payPass = new PayPass(this);
            payPass.setMoney(binding.money.getText().toString());
            payPass.setPay(this);
            payPass.show();
        }
    }
}
