package com.mining.mining.activity.c2s;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.mining.mining.R;
import com.mining.mining.activity.login.LoginActivity;
import com.mining.mining.adapter.PagerAdapter;
import com.mining.mining.databinding.ActivityBuyBinding;
import com.mining.mining.util.ArithHelper;
import com.mining.mining.util.Handler;
import com.mining.mining.util.OnHandler;
import com.mining.mining.util.StatusBarUtil;
import com.xframe.network.OnData;
import com.xframe.network.SocketManage;

import org.json.JSONException;
import org.json.JSONObject;

public class BuyActivity extends AppCompatActivity implements OnData, OnHandler, View.OnClickListener, TextWatcher {
    private ActivityBuyBinding binding;
    private String id;
    private SharedPreferences sharedPreferences;
    private final Handler handler = new Handler(Looper.myLooper(), this);

    private final OnData onData = new OnData() {
        @Override
        public void connect(SocketManage socketManage) {
            String id = sharedPreferences.getString("id", null);
            String _key = sharedPreferences.getString("_key", null);
            if (id == null || _key == null) {
                LoginActivity.login(BuyActivity.this);
                return;
            }
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("type", 4);
                jsonObject.put("code", 3);
                jsonObject.put("id", id);
                jsonObject.put("_key", _key);
                jsonObject.put("gem_id", BuyActivity.this.id);
                jsonObject.put("gem_size", binding.gem.getText().toString());
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
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = getSharedPreferences("user", Context.MODE_PRIVATE);
        id = getIntent().getStringExtra("id");
        if (id == null) {
            finish();
            return;
        }
        StatusBarUtil.setImmersiveStatusBar(this, true);
        binding = ActivityBuyBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initToolbar();
        initView();
        SocketManage.init(this);
    }

    private void initView() {
        binding.buy.setOnClickListener(this);
        binding.gem.addTextChangedListener(this);
        binding.all.setOnClickListener(this);
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
            jsonObject.put("code", 2);
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
                handler.handleMessage(1, data.toString());
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
        }
    }

    private void initViewData(JSONObject data) throws JSONException {
        String article = data.getString("article");
        String condition = data.getString("condition");
        String usdt = data.getString("usdt");
        JSONObject user = data.getJSONObject("user");
        String name = user.getString("name");
        String user_usdt = user.getString("usdt");


        binding.usdt.post(() -> {
            binding.usdt.setText(usdt);
            binding.name.setText(name);
            binding.article.setText(article);
            binding.condition.setText(condition);
            binding.userUsdt.setText(user_usdt);
        });
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
                Toast.makeText(this, "可用USDT不足", Toast.LENGTH_SHORT).show();
                return;
            }
            if (num > gem) {
                Toast.makeText(this, "购买数量大于可买数量", Toast.LENGTH_SHORT).show();
                return;
            }
            SocketManage.init(onData);
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        // 文本变化之前执行的代码
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
        // 文本变化之后执行的代码
    }
}
