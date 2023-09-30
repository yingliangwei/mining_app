package com.mining.mining.pager.mining;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.mining.mining.R;
import com.mining.mining.activity.c2s.C2CActivity;
import com.mining.mining.activity.login.LoginActivity;
import com.mining.mining.adapter.PagerAdapter;
import com.mining.mining.adapter.RecyclerAdapter;
import com.mining.mining.databinding.PagerMiningBinding;
import com.mining.mining.pager.holder.ViewHolder;
import com.mining.mining.pager.mining.pager.MiningItemPager;
import com.mining.util.Handler;
import com.mining.util.MessageEvent;
import com.mining.util.OnHandler;
import com.mining.util.StatusBarUtil;
import com.mining.util.StringUtil;
import com.xframe.network.OnData;
import com.xframe.network.SocketManage;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MiningPager extends RecyclerAdapter implements OnHandler, TabLayout.OnTabSelectedListener, OnData, View.OnClickListener {
    private final Activity context;
    private PagerMiningBinding binding;
    private final Handler handler = new Handler(Looper.getMainLooper(), this);
    private SharedPreferences sharedPreferences;

    public MiningPager(Activity context) {
        super(context);
        this.context = context;
        EventBus.getDefault().register(this);
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
        initTab();
        initPager();
        initView();
        SocketManage.init(this);
    }

    private void initView() {
        binding.game.setOnClickListener(this);
        binding.transaction.setOnClickListener(this);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void StatusBar(Activity activity) {
        StatusBarUtil.setImmersiveStatusBar(activity, false);
    }

    private void initPager() {
        List<RecyclerAdapter> recyclerAdapters = new ArrayList<>();
        recyclerAdapters.add(new MiningItemPager(context, "0"));
        recyclerAdapters.add(new MiningItemPager(context, "1"));
        recyclerAdapters.add(new MiningItemPager(context, "2"));
        PagerAdapter pagerAdapter = new PagerAdapter(recyclerAdapters);
        binding.pager.setAdapter(pagerAdapter);
        binding.pager.setOffscreenPageLimit(recyclerAdapters.size());
        binding.pager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                TabLayout.Tab tab = binding.tab.getTabAt(position);
                if (tab == null) {
                    return;
                }
                tab.select();
            }
        });
    }

    private void initTab() {
        binding.tab.addTab(binding.tab.newTab().setText("普通矿山").setId(0));
        binding.tab.addTab(binding.tab.newTab().setText("宝石矿山").setId(1));
        binding.tab.addTab(binding.tab.newTab().setText("算力挖矿").setId(2));
        binding.tab.addOnTabSelectedListener(this);
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
        try {
            JSONObject jsonObject = new JSONObject(ds);
            int code = jsonObject.getInt("code");
            if (code == 200) {
                String gem = jsonObject.getString("gem");
                String day_gem = jsonObject.getString("day_gem");
                binding.dayGem.setText(StringUtil.toRe(day_gem));
                handler.sendMessage(1, gem);
            }
        } catch (Exception e) {
            e.fillInStackTrace();
        }
    }

    @Override
    public void handleMessage(int w, String str) {
        if (w == 0) {
            Toast.makeText(context, str, Toast.LENGTH_SHORT).show();
        } else if (w == 1) {
            binding.gem.setText(StringUtil.toRe(str));
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        binding.pager.setCurrentItem(tab.getId());
        if (tab.getId() == 0) {
            binding.tab.setBackground(context.getDrawable(R.mipmap.bg_boring_ape_indicator_left));
        } else if (tab.getId() == 1) {
            binding.tab.setBackground(context.getDrawable(R.mipmap.bg_boring_ape_indicator_center));
        } else if (tab.getId() == 2) {
            binding.tab.setBackground(context.getDrawable(R.mipmap.bg_boring_ape_indicator_right));
        }
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.game) {
            EventBus.getDefault().post(new MessageEvent(2, ""));
        } else if (v.getId() == R.id.transaction) {
            context.startActivity(new Intent(context, C2CActivity.class));
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onEvent(MessageEvent event) {
        if (event.getW() == 1) {
            SocketManage.init(this);
        }
    }
}
