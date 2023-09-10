package com.mining.mining.pager.mining;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.icu.text.SimpleDateFormat;
import android.net.ParseException;
import android.os.CountDownTimer;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.AppBarLayout;
import com.mining.mining.R;
import com.mining.mining.activity.login.LoginActivity;
import com.mining.mining.adapter.PagerAdapter;
import com.mining.mining.adapter.RecyclerAdapter;
import com.mining.mining.databinding.PagerMiningBinding;
import com.mining.mining.pager.holder.ViewHolder;
import com.mining.mining.pager.mining.item.ItemPager;
import com.mining.mining.util.Handler;
import com.mining.mining.util.OnHandler;
import com.mining.mining.util.StatusBarUtil;
import com.mining.mining.util.TabLayoutUtil;
import com.xframe.network.OnData;
import com.xframe.network.SocketManage;
import com.xframe.widget.MinerView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MiningPager extends RecyclerAdapter implements OnHandler, OnData, View.OnClickListener {
    private final Activity context;
    private PagerMiningBinding binding;

    private final Handler handler = new Handler(Looper.getMainLooper(), this);
    private SharedPreferences sharedPreferences;
    private CountDownTimer countDownTimer;

    public MiningPager(Activity context) {
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        sharedPreferences = context.getSharedPreferences("user", Context.MODE_PRIVATE);
        binding = PagerMiningBinding.inflate(LayoutInflater.from(context), parent, false);
        return new ViewHolder(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        initView();
        SocketManage.init(this);
    }

    private void initView() {
        binding.mining.setOnClickListener(this);
    }

    @Override
    public void connect(SocketManage socketManage) {
        try {
            String id = sharedPreferences.getString("id", null);
            String _key = sharedPreferences.getString("_key", null);
            if (id == null || _key == null) {
                LoginActivity.login(context);
                return;
            }
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("type", 8);
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
            }
        } catch (Exception e) {
            e.fillInStackTrace();
        }
    }

    @Override
    public void handleMessage(int w, String str) {
        if (w == 1) {
            try {
                JSONObject jsonObject = new JSONObject(str);
                initData(jsonObject);
            } catch (Exception e) {
                e.fillInStackTrace();
            }
        }
    }

    private void initData(JSONObject jsonObject) throws Exception {
        String gem = jsonObject.getString("gem");
        String stone = jsonObject.getString("stone");
        String miner_count = jsonObject.getString("miner_count");
        String miner_usdt = jsonObject.getString("miner_usdt");
        initMiner_count(miner_count);
        binding.minerUsdt.setText(context.getString(R.string.app_miner_usdt, miner_usdt));
        binding.gem.setText(gem);
        binding.gemstone.setText(stone);
        binding.miner.setText(context.getString(R.string.app_miner, miner_count));
        String is_time = jsonObject.getString("is_time");
        if (is_time.equals("0")) {
            binding.burial.setText(context.getString(R.string.app_burial, "等待挖矿"));
        } else {
            String time = jsonObject.getString("time");
            initTime(time);
        }
    }

    private void initMiner_count(String miner_count) {
        ViewGroup parent = binding.minerL;
        for (int i = 0; i < parent.getChildCount(); i++) {
            View child = parent.getChildAt(i);
            if (child instanceof MinerView) {
                MinerView minerView = (MinerView) child;
                minerView.stop();
            }
        }
        parent.removeAllViews();
        int count = Integer.parseInt(miner_count);
        for (int i = 0; i < count; i++) {
            MinerView minerView = new MinerView(context);
            minerView.setLayoutParams(new RecyclerView.LayoutParams(60, 60));
            binding.minerL.addView(minerView);
        }
    }

    private void initTime(String time) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date date = sdf.parse(time);
            long now = System.currentTimeMillis();
            long di = date.getTime() + 1800000;
            long diff = di - now;
            if (countDownTimer != null) {
                countDownTimer.cancel();
                countDownTimer = null;
            }
            countDownTimer = new CountDownTimer(diff, 1_000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    long time = millisUntilFinished / 1000;
                    // 执行您的逻辑
                    binding.burial.setText(context.getString(R.string.app_burial, String.valueOf(time)));
                }

                @Override
                public void onFinish() {
                    binding.burial.setText(context.getString(R.string.app_burial, "等待挖矿"));
                }
            }.start();
        } catch (Exception e) {
            e.fillInStackTrace();
        }
    }

    private final OnData onData = new OnData() {
        @Override
        public void handle(String ds) {
            System.out.println(ds);
            try {
                JSONObject jsonObject = new JSONObject(ds);
                int code = jsonObject.getInt("code");
                SocketManage.init(MiningPager.this);
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
                    LoginActivity.login(context);
                    return;
                }
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("type", 8);
                jsonObject.put("code", 2);
                jsonObject.put("id", id);
                jsonObject.put("_key", _key);
                socketManage.print(jsonObject.toString());
            } catch (Exception e) {
                e.fillInStackTrace();
            }
        }
    };

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.mining) {
            String min = binding.burial.getText().toString();
            if (min.contains("等待挖矿")) {
                SocketManage.init(onData);
            }
        }
    }
}
