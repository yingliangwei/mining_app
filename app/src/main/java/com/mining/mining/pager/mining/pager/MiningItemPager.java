package com.mining.mining.pager.mining.pager;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.mining.mining.R;
import com.mining.mining.adapter.RecyclerAdapter;
import com.mining.mining.databinding.PagerItemMiningBinding;
import com.mining.mining.entity.MiningEntity;
import com.mining.mining.pager.holder.ViewHolder;
import com.mining.mining.pager.mining.pager.adapter.MiningAdapter;
import com.mining.util.Handler;
import com.mining.util.MessageEvent;
import com.mining.util.OnHandler;
import com.mining.util.StringUtil;
import com.xframe.network.OnData;
import com.xframe.network.SocketManage;
import com.xframe.widget.PayPass;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MiningItemPager extends RecyclerAdapter implements OnHandler, OnData, View.OnClickListener, PayPass.OnPay {
    private PagerItemMiningBinding binding;
    private final Context context;
    private final String mining_id;
    private MiningAdapter miningAdapter;
    private final List<MiningEntity> list = new ArrayList<>();
    private final Handler handler = new Handler(Looper.getMainLooper(), this);
    private SharedPreferences sharedPreferences;
    private String _mining_id;
    private String pass;
    private String mining_usdt;

    private final OnData onData = new OnData() {
        @Override
        public void connect(SocketManage socketManage) {
            try {
                String id = sharedPreferences.getString("id", null);
                String _key = sharedPreferences.getString("_key", null);
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("type", 8);
                jsonObject.put("code", 4);
                jsonObject.put("mining_id", _mining_id);
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
                if (code == 200) {
                    JSONObject permanent = jsonObject.getJSONObject("permanent");
                    handler.sendMessage(1, permanent.toString());
                }
                String msg = jsonObject.getString("msg");
                handler.sendMessage(0, msg);
                EventBus.getDefault().post(new MessageEvent(1, ""));
            } catch (Exception e) {
                e.fillInStackTrace();
            }
        }
    };

    private final OnData buy = new OnData() {
        @Override
        public void handle(String ds) {
            try {
                JSONObject jsonObject = new JSONObject(ds);
                int code = jsonObject.getInt("code");
                if (code == 200) {
                    JSONObject permanent = jsonObject.getJSONObject("permanent");
                    handler.sendMessage(1, permanent.toString());
                    EventBus.getDefault().post(new MessageEvent(3, ""));
                }
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
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("type", 8);
                jsonObject.put("code", 5);
                jsonObject.put("mining_id", _mining_id);
                jsonObject.put("id", id);
                jsonObject.put("pass", pass);
                jsonObject.put("_key", _key);
                socketManage.print(jsonObject.toString());
            } catch (Exception e) {
                e.fillInStackTrace();
            }
        }
    };

    public MiningItemPager(Context context, String mining_id) {
        super(context);
        this.context = context;
        this.mining_id = mining_id;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        sharedPreferences = context.getSharedPreferences("user", Context.MODE_PRIVATE);
        binding = PagerItemMiningBinding.inflate(LayoutInflater.from(context), parent, false);
        if (mining_id.equals("0")) {
            binding.permanent.setVisibility(View.VISIBLE);
        }
        return new ViewHolder(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        initRecycler();
        Socket();
    }

    public SharedPreferences getSharedPreferences() {
        return sharedPreferences;
    }

    public void Socket() {
        SocketManage.init(this);
    }

    private void initRecycler() {
        miningAdapter = new MiningAdapter(context, list, mining_id, this);
        miningAdapter.setEmptyTextView(binding.blank);
        binding.recycle.setLayoutManager(new LinearLayoutManager(context));
        binding.recycle.setAdapter(miningAdapter);
    }

    @Override
    public void connect(SocketManage socketManage) {
        try {
            String id = sharedPreferences.getString("id", null);
            String _key = sharedPreferences.getString("_key", null);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("type", 8);
            jsonObject.put("code", 2);
            jsonObject.put("tab_id", mining_id);
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
            if (code == 200) {
                if (mining_id.equals("0")) {
                    handler.sendMessage(1, jsonObject.getJSONObject("permanent").toString());
                }
                JSONArray pit_data = jsonObject.getJSONArray("pit_data");
                handler.sendMessage(2, pit_data.toString());
            }
        } catch (Exception e) {
            e.fillInStackTrace();
        }
    }

    private void initPermanent(String text) {
        System.out.println(text);
        try {
            JSONObject jsonObject = new JSONObject(text);
            String mining_gem = jsonObject.getString("mining_gem");
            String mining_remaining = jsonObject.getString("mining_remaining");
            mining_usdt = jsonObject.getString("mining_usdt");
            String day_gem = jsonObject.getString("day_gem");
            String mining_size = jsonObject.getString("mining_size");
            this._mining_id = jsonObject.getString("id");
            binding.miningUsdt.setText(String.format(context.getString(R.string.app_s_usdt_5), StringUtil.toRe(mining_usdt)));
            binding.miningGem.setText(context.getString(R.string.app_accumulate, StringUtil.toRe(mining_gem)));
            binding.miningRemaining.setText(context.getString(R.string.app_diggable, mining_remaining));
            binding.miningRemaining.setOnClickListener(this);
            binding.buyMining.setOnClickListener(this);
            binding.dayGem.setText(context.getString(R.string.app_day_gem, StringUtil.toRe(day_gem)));
            binding.miningLv.setText(context.getString(R.string.app_mining_lv, mining_size));
        } catch (Exception e) {
            e.fillInStackTrace();
        }
    }


    @Override
    public void handleMessage(int w, String str) {
        if (w == 0) {
            Toast.makeText(context, str, Toast.LENGTH_SHORT).show();
        } else if (w == 1) {
            initPermanent(str);
        } else if (w == 2) {
            initPit_data(str);
        }
    }


    private void initPit_data(String str) {
        list.clear();
        try {
            JSONArray data = new JSONArray(str);
            for (int i = 0; i < data.length(); i++) {
                JSONObject jsonObject = data.getJSONObject(i);
                MiningEntity mining = new Gson().fromJson(jsonObject.toString(), MiningEntity.class);
                String _mining = jsonObject.getString("mining");
                if (!_mining.equals("{}")) {
                    if (_mining.length() != 0) {
                        mining.setMining(_mining);
                    }
                }
                list.add(mining);
                miningAdapter.notifyItemChanged(i);
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.mining_remaining) {
            SocketManage.init(onData);
        } else if (v.getId() == R.id.buyMining) {
            PayPass payPass = new PayPass(context);
            payPass.setMoney(mining_usdt);
            payPass.setPay(this);
            payPass.show();
        }
    }

    @Override
    public void onText(String pass) {
        this.pass = pass;
        SocketManage.init(buy);
    }
}
