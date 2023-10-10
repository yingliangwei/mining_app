package com.mining.mining.pager.mining;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONException;
import com.alibaba.fastjson2.JSONObject;
import com.google.android.material.tabs.TabLayout;
import com.mining.mining.R;
import com.mining.mining.activity.c2s.C2CActivity;
import com.mining.mining.adapter.PagerAdapter;
import com.mining.mining.adapter.RecyclerAdapter;
import com.mining.mining.databinding.PagerMiningBinding;
import com.mining.mining.pager.holder.ViewHolder;
import com.mining.mining.pager.mining.pager.MiningItemPager;
import com.mining.mining.util.SharedUtil;
import com.mining.util.MessageEvent;
import com.mining.util.OnHandler;
import com.mining.util.StatusBarUtil;
import com.mining.util.StringUtil;
import com.xframe.network.OnData;
import com.xframe.network.SocketManage;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

public class MiningPager extends RecyclerAdapter implements TabLayout.OnTabSelectedListener, OnData, View.OnClickListener {
    private final Activity context;
    private PagerAdapter pagerAdapter;
    private PagerMiningBinding binding;
    private final List<RecyclerAdapter> recyclerAdapters = new ArrayList<>();

    public MiningPager(Activity context) {
        super(context);
        this.context = context;
        EventBus.getDefault().register(this);
    }

    private final OnData getGem = new OnData() {
        @Override
        public void handle(String ds) {
            JSONObject jsonObject = JSONObject.parseObject(ds);
            int code = jsonObject.getInteger("code");
            if (code == 200) {
                String gem = jsonObject.getString("gem");
                String day_gem = jsonObject.getString("day_gem");
                binding.dayGem.setText(StringUtil.toRe(day_gem));
                binding.gem.setText(StringUtil.toRe(gem));
            }
        }

        @Override
        public void connect(SocketManage socketManage) {
            SharedUtil sharedUtil = new SharedUtil(context);
            JSONObject jsonObject = sharedUtil.getLogin(8, 1);
            socketManage.print(jsonObject.toString());
        }
    };

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = PagerMiningBinding.inflate(LayoutInflater.from(context), parent, false);
        return new ViewHolder(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        initTab();
        initPager();
        initView();
        SocketManage.init(getGem);
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
        pagerAdapter = new PagerAdapter(recyclerAdapters);
        binding.pager.setAdapter(pagerAdapter);
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
        binding.tab.addOnTabSelectedListener(this);
    }

    @Override
    public void connect(SocketManage socketManage) {
        SharedUtil sharedUtil = new SharedUtil(context);
        JSONObject jsonObject = sharedUtil.getLogin(8, 2);
        socketManage.print(jsonObject.toString());
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void handle(String ds) {
        JSONObject jsonObject = JSONObject.parseObject(ds);
        int code = jsonObject.getInteger("code");
        if (code == 200) {
            JSONArray data = jsonObject.getJSONArray("data");
            for (int i = 0; i < data.size(); i++) {
                JSONObject jsonObject1 = data.getJSONObject(i);
                String id = jsonObject1.getString("id");
                String name = jsonObject1.getString("name");
                //因为id不可能为0所以-1
                int _id = Integer.parseInt(id);
                binding.tab.addTab(binding.tab.newTab().setText(name).setId(_id - 1));
                recyclerAdapters.add(new MiningItemPager(context, id));
            }
            pagerAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        binding.pager.setCurrentItem(tab.getId());
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
            SocketManage.init(getGem);
        }
    }
}
