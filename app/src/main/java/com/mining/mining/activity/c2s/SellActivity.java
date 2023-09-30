package com.mining.mining.activity.c2s;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.mining.mining.R;
import com.mining.mining.activity.login.LoginActivity;
import com.mining.mining.databinding.ActivitySellBinding;
import com.mining.util.ArithHelper;
import com.mining.util.Handler;
import com.mining.util.MessageEvent;
import com.mining.util.OnHandler;
import com.mining.util.StatusBarUtil;
import com.scwang.smart.refresh.header.ClassicsHeader;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnRefreshListener;
import com.xframe.network.OnData;
import com.xframe.network.SocketManage;
import com.xframe.widget.PayPass;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

public class SellActivity extends AppCompatActivity implements TextWatcher, OnData, OnHandler, View.OnClickListener, OnRefreshListener, PayPass.OnPay {
    private final Handler handler = new Handler(Looper.myLooper(), this);
    private ActivitySellBinding binding;
    private SharedPreferences sharedPreferences;
    private String id;
    private String pass;

    private final OnData onData = new OnData() {
        @Override
        public void connect(SocketManage socketManage) {
            try {
                String id = sharedPreferences.getString("id", null);
                String _key = sharedPreferences.getString("_key", null);
                if (id == null || _key == null) {
                    LoginActivity.login(SellActivity.this);
                    return;
                }
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("type", 4);
                jsonObject.put("code", 6);
                jsonObject.put("id", id);
                jsonObject.put("pass", pass);
                jsonObject.put("_key", _key);
                jsonObject.put("gem_id", SellActivity.this.id);
                jsonObject.put("gem_size", binding.gem.getText().toString());
                System.out.println(jsonObject);
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
                EventBus.getDefault().post(new MessageEvent(1, ""));
                EventBus.getDefault().post(new MessageEvent(3,""));
            } catch (Exception e) {
                e.fillInStackTrace();
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initId();
        sharedPreferences = getSharedPreferences("user", Context.MODE_PRIVATE);
        StatusBarUtil.setImmersiveStatusBar(this, true);
        binding = ActivitySellBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initToolbar();
        initView();
        initSmart();
        SocketManage.init(this);
    }

    private void initSmart() {
        binding.Smart.setRefreshHeader(new ClassicsHeader(this));
        binding.Smart.setOnRefreshListener(this);
    }

    private void initView() {
        binding.buy.setOnClickListener(this);
        binding.gem.addTextChangedListener(this);
        binding.all.setOnClickListener(this);
    }

    private void initId() {
        id = getIntent().getStringExtra("id");
        if (id == null) {
            finish();
        }
    }

    private void initToolbar() {
        binding.toolbar.setOnClickListener(v -> finish());
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
            jsonObject.put("type", 4);
            jsonObject.put("code", 5);
            jsonObject.put("id", id);
            jsonObject.put("_key", _key);
            jsonObject.put("gem_id", this.id);
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
            if (code == 200) {
                JSONObject data = jsonObject.getJSONObject("data");
                handler.sendMessage(1, data.toString());
                handler.sendMessage(4, "");
            } else if (code == 402) {
                String msg = jsonObject.getString("msg");
                handler.sendMessage(0, msg);
                finish();
            }
        } catch (Exception e) {
            e.fillInStackTrace();
        }
    }

    @Override
    public void error(String error) {
        handler.sendMessage(5, "");
    }

    @Override
    public void handleMessage(int w, String str) {
        if (w == 0) {
            Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
        } else if (w == 1) {
            try {
                JSONObject jsonObject = new JSONObject(str);
                initViewData(jsonObject);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        } else if (w == 4) {
            binding.Smart.finishRefresh(true);
        } else if (w == 5) {
            binding.Smart.finishRefresh(false);
        }
    }

    private void initViewData(JSONObject data) throws JSONException {
        String article = data.getString("article");
        String usdt = data.getString("usdt");
        JSONObject user = data.getJSONObject("user");
        String name = user.getString("name");
        String user_usdt = user.getString("usdt");

        binding.usdt.setText(usdt);
        binding.name.setText(name);
        binding.article.setText(article);
        binding.userUsdt.setText(user_usdt);

    }


    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        // 文本变化时执行的代码
        String text = s.toString();
        if (text.length() == 0) {
            return;
        }
        int num = Integer.parseInt(text);
        int gem = Integer.parseInt(binding.article.getText().toString());
        double quantity = ArithHelper.mul(num, Double.parseDouble(binding.usdt.getText().toString()));
        double dd = Double.parseDouble(binding.userUsdt.getText().toString());
        if (quantity > dd) {
            binding.userUsdt.setTextColor(getColor(android.R.color.holo_red_dark));
        } else {
            binding.userUsdt.setTextColor(getColor(android.R.color.black));
        }

        if (num > gem) {
            binding.article.setTextColor(getColor(android.R.color.holo_red_dark));
        } else {
            binding.article.setTextColor(getColor(android.R.color.black));
        }

        binding.xUsdt.setText(String.valueOf(quantity));
        // 在这里可以根据文本内容执行相应的操作
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.all) {
            binding.gem.setText(binding.article.getText());
            binding.gem.setSelection(binding.gem.getText().length());
        } else if (v.getId() == R.id.buy) {
            int num = Integer.parseInt(binding.gem.getText().toString());
            int gem = Integer.parseInt(binding.article.getText().toString());
            double quantity = ArithHelper.mul(num, Double.parseDouble(binding.usdt.getText().toString()));
            double dd = Double.parseDouble(binding.userUsdt.getText().toString());
            if (quantity > dd) {
                Toast.makeText(this, "可用宝石不足", Toast.LENGTH_SHORT).show();
                return;
            }
            if (num > gem) {
                Toast.makeText(this, "购买数量大于可买数量", Toast.LENGTH_SHORT).show();
                return;
            }
            PayPass payPass = new PayPass(this);
            payPass.setPay(this);
            payPass.show();
        }
    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        SocketManage.init(this);
    }

    @Override
    public void onText(String pass) {
        this.pass = pass;
        SocketManage.init(onData);
    }
}
