package com.mining.press.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Looper;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.GridLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.gson.Gson;
import com.mining.pluginwidget.refresh.header.ClassicsHeader;
import com.mining.press.R;
import com.mining.press.adapter.MainAdapter;
import com.mining.press.databinding.ActivityMainBinding;
import com.mining.press.databinding.ItemTextBinding;
import com.mining.press.entity.PressEntity;
import com.mining.util.Handler;
import com.mining.util.MessageEvent;
import com.mining.util.OnHandler;
import com.mining.util.StringUtil;
import com.plugin.activity.PluginActivity;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnRefreshListener;
import com.xframe.network.OnData;
import com.xframe.network.SocketManage;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends PluginActivity implements OnData, OnHandler, CompoundButton.OnCheckedChangeListener, View.OnClickListener, OnRefreshListener {
    private ActivityMainBinding binding;
    private MainAdapter mainAdapter;
    private final List<PressEntity> list = new ArrayList<>();
    private SharedPreferences sharedPreferences;
    private final Handler handler = new Handler(Looper.getMainLooper(), this);
    private final OnData onData = new OnData() {
        @Override
        public void connect(SocketManage socketManage) {
            try {
                String id = sharedPreferences.getString("id", null);
                String _key = sharedPreferences.getString("_key", null);
                if (id == null || _key == null) {
                    return;
                }
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("type", 13);
                jsonObject.put("code", 2);
                jsonObject.put("k", getSelector());
                jsonObject.put("stone", getChecked());
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
                String msg = jsonObject.getString("msg");
                handler.sendMessage(0, msg);
                SocketManage.init(MainActivity.this);
                EventBus.getDefault().post(new MessageEvent(1, ""));
            } catch (Exception e) {
                e.fillInStackTrace();
            }
        }
    };
    private CountDownTimer countDownTimer;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        sharedPreferences = thisContex.getSharedPreferences("user", Context.MODE_PRIVATE);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initToolbar();
        initView();
        initRecycler();
        initCheckBox();
        initSmart();
        SocketManage.init(this);
    }

    private void initToolbar() {
        binding.toolbar.setNavigationOnClickListener(v -> thisContex.finish());
    }

    private void initSmart() {
        binding.Smart.setRefreshHeader(new ClassicsHeader(thisContex));
        binding.Smart.setOnRefreshListener(this);
    }

    private void initView() {
        binding.ok.setOnClickListener(this);
        binding.pressLog.setOnClickListener(this);
    }

    private void initCheckBox() {
        binding.checkboxSelector1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    binding.checkboxSelector2.setChecked(false);
                    binding.checkboxSelector3.setChecked(false);
                }
                binding.linear.setVisibility(View.VISIBLE);
            }
        });
        binding.checkboxSelector2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    binding.checkboxSelector1.setChecked(false);
                    binding.checkboxSelector3.setChecked(false);
                }
                binding.linear.setVisibility(View.VISIBLE);
            }
        });
        binding.checkboxSelector3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    binding.checkboxSelector2.setChecked(false);
                    binding.checkboxSelector1.setChecked(false);
                }
                binding.linear.setVisibility(View.VISIBLE);
            }
        });
    }

    private void initRecycler() {
        list.add(new PressEntity());
        mainAdapter = new MainAdapter(thisContex, list);
        binding.recycler.setLayoutManager(new LinearLayoutManager(thisContex));
        binding.recycler.setAdapter(mainAdapter);
    }

    @Override
    public void connect(SocketManage socketManage) {
        try {
            String id = sharedPreferences.getString("id", null);
            String _key = sharedPreferences.getString("_key", null);
            if (id == null || _key == null) {
                return;
            }
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("type", 13);
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
        handler.sendMessage(1, ds);
    }

    @Override
    public void handleMessage(int w, String str) {
        if (w == 0) {
            Toast.makeText(thisContex, str, Toast.LENGTH_SHORT).show();
        } else if (w == 1) {
            binding.Smart.finishRefresh(true);
            binding.linear.setVisibility(View.GONE);
            try {
                JSONObject jsonObject = new JSONObject(str);
                initData(jsonObject);
            } catch (Exception e) {
                e.fillInStackTrace();
            }
        } else if (w == 2) {
            binding.Smart.finishRefresh(false);
        }
    }


    private void initData(JSONObject jsonObject) throws JSONException {
        JSONObject user = jsonObject.getJSONObject("user");
        String name = user.getString("name");
        String stone = user.getString("stone");
        String time = user.getString("time");
        String id = user.getString("id");
        binding.number.setText(String.format(getResources().getText(R.string.app_number).toString(), id));
        String press_size = user.getString("press_size");
        String press_size_1 = user.getString("press_size_1");
        binding.name.setText(name);
        binding.nameX.setText(StringUtil.getStringStart(name));
        binding.stone.setText(StringUtil.toRe(stone));
        binding.pressSize.setText(press_size);
        binding.pressSize1.setText(press_size_1);
        binding.text.setText("请选择...");

        initTime(time);

        JSONArray data = jsonObject.getJSONArray("data");
        initPressData(data);

        JSONArray game_press_stone = jsonObject.getJSONArray("game_press_stone");
        initGame_press_stone(game_press_stone);

        int isPress = user.getInt("isPress");
        if (isPress == 1) {
            binding.text.setText("以下注");
            String k = user.getString("k");
            switch (k) {
                case "1":
                    binding.checkboxSelector1.setChecked(true);
                    binding.checkboxSelector1.setClickable(false);
                    binding.checkboxSelector2.setClickable(false);
                    binding.checkboxSelector3.setClickable(false);
                    break;
                case "2":
                    binding.checkboxSelector2.setChecked(true);
                    binding.checkboxSelector2.setClickable(false);
                    binding.checkboxSelector1.setClickable(false);
                    binding.checkboxSelector3.setClickable(false);
                    break;
                case "3":
                    binding.checkboxSelector3.setChecked(true);
                    binding.checkboxSelector3.setClickable(false);
                    binding.checkboxSelector2.setClickable(false);
                    binding.checkboxSelector1.setClickable(false);
                    break;
            }
            binding.linear.setVisibility(View.GONE);
        } else {
            binding.checkboxSelector1.setChecked(false);
            binding.checkboxSelector2.setChecked(false);
            binding.checkboxSelector3.setChecked(false);
            binding.checkboxSelector1.setClickable(true);
            binding.checkboxSelector2.setClickable(true);
            binding.checkboxSelector3.setClickable(true);
            binding.linear.setVisibility(View.GONE);
        }
    }

    private void initTime(String time) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date date = sdf.parse(time);
            long now = System.currentTimeMillis();
            long di = date.getTime() + 360_000;
            //加2秒，防止获取不到数据
            long diff = di - now + 2000;
            if (countDownTimer != null) {
                countDownTimer.cancel();
                countDownTimer = null;
            }
            if (diff <= 0) {
                return;
            }
            countDownTimer = new CountDownTimer(diff, 1_000) {
                @Override
                public void onTick(long seconds) {
                    SimpleDateFormat formatter = new SimpleDateFormat("mm:ss");
                    String formattedTime = formatter.format(new Date(seconds));
                    // 执行您的逻辑
                    binding.time.setText(formattedTime);
                }

                @Override
                public void onFinish() {
                    binding.time.setText("0");
                    binding.text.setText("本局已经结束!刷新进入下一局");
                    SocketManage.init(MainActivity.this);
                }
            }.start();
        } catch (Exception e) {
            e.fillInStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }

    private void initGame_press_stone(JSONArray game_press_stone) throws JSONException {
        ViewGroup parent = binding.grid;
        parent.removeAllViews();
        for (int i = 0; i < game_press_stone.length(); i++) {
            JSONObject jsonObject = game_press_stone.getJSONObject(i);
            String stone = jsonObject.getString("stone");
            ItemTextBinding itemTextBinding = ItemTextBinding.inflate(getLayoutInflater());
            itemTextBinding.text.setText(StringUtil.toRe(stone));
            itemTextBinding.text.setId(i);
            itemTextBinding.text.setOnCheckedChangeListener(this);
            //使用Spec定义子控件的位置和比重
            GridLayout.Spec rowSpec = GridLayout.spec(i / 3, 1f);
            GridLayout.Spec columnSpec = GridLayout.spec(i % 3, 1f);
            //将Spec传入GridLayout.LayoutParams并设置宽高为0，必须设置宽高，否则视图异常
            GridLayout.LayoutParams layoutParams = new GridLayout.LayoutParams(rowSpec, columnSpec);
            layoutParams.setMargins(10, 10, 10, 10);
            layoutParams.height = 100;
            layoutParams.width = GridLayout.LayoutParams.WRAP_CONTENT;
            parent.addView(itemTextBinding.getRoot(), layoutParams);
        }
    }


    @SuppressLint("NotifyDataSetChanged")
    private void initPressData(JSONArray data) {
        list.clear();
        list.add(new PressEntity());
        for (int i = 0; i < data.length(); i++) {
            try {
                PressEntity pressEntity = new Gson().fromJson(data.getString(i), PressEntity.class);
                if (!pressEntity.getBill_k().equals(pressEntity.getK())) {
                    if (pressEntity.getStone().equals("0")) {
                        pressEntity.setStone("0");
                    } else {
                        pressEntity.setStone_x("-" + pressEntity.getStone());
                    }
                }
                list.add(pressEntity);
                mainAdapter.notifyItemChanged(i);
            } catch (Exception e) {
                e.fillInStackTrace();
            }
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            initChecked(buttonView.getId());
            binding.ok.setText(String.format(getResources().getText(R.string.app_ok).toString(), buttonView.getText()));
        }
    }

    private void initChecked(int id) {
        int count = binding.grid.getColumnCount();
        for (int i = 0; i < count; i++) {
            CheckBox checkBox = binding.grid.findViewById(i);
            checkBox.setChecked(i == id);
            if (checkBox.isChecked()) {
                checkBox.setTextColor(Color.GREEN);
            } else {
                checkBox.setTextColor(Color.BLACK);
            }
        }
    }

    private String getChecked() {
        int count = binding.grid.getColumnCount();
        for (int i = 0; i < count; i++) {
            CheckBox checkBox = binding.grid.findViewById(i);
            if (checkBox.isChecked()) {
                return checkBox.getText().toString();
            }
        }
        return null;
    }

    @Override
    public void error(String error) {
        handler.sendMessage(2, "");
    }

    private String getSelector() {
        if (binding.checkboxSelector1.isChecked()) {
            return "1";
        } else if (binding.checkboxSelector2.isChecked()) {
            return "2";
        } else if (binding.checkboxSelector3.isChecked()) {
            return "3";
        }
        return "1";
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.ok) {
            SocketManage.init(onData);
        } else if (v.getId() == R.id.press_log) {
            startActivity(new Intent(thisContex, PressLogActivity.class));
        }
    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        SocketManage.init(this);
    }
}
