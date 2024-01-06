package com.mining.mining.pager.mining;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager2.widget.ViewPager2;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.google.android.material.tabs.TabLayout;
import com.mining.mining.R;
import com.mining.mining.activity.c2s.gem.activity.C2CActivity;
import com.mining.mining.activity.task.ActivityTask;
import com.mining.mining.adapter.PagerAdapter;
import com.mining.mining.adapter.RecyclerAdapter;
import com.mining.mining.databinding.PagerMiningBinding;
import com.mining.mining.entity.MessageEvent;
import com.mining.mining.pager.holder.ViewHolder;
import com.mining.mining.pager.mining.pager.MiningItemPager;
import com.mining.mining.util.SharedUtil;
import com.mining.util.StatusBarUtil;
import com.mining.util.StringUtil;
import com.xframe.network.OnData;
import com.xframe.network.SocketManage;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

public class MiningPager extends RecyclerAdapter implements TabLayout.OnTabSelectedListener, OnData, View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {
    private final Activity context;
    private PagerMiningBinding binding;
    private final List<RecyclerAdapter> recyclerAdapters = new ArrayList<>();

    public MiningPager(Activity context) {
        super(context);
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = PagerMiningBinding.inflate(LayoutInflater.from(context), parent, false);
        return new ViewHolder(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        EventBus.getDefault().register(this);
        initTab();
        initView();
        initSmart();
        SocketManage.init(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessage(MessageEvent event) {
        if (event.isClass(getClass())) {
            if (event.w == 0) {
                SocketManage.init(this);
            } else if (event.w == 1) {
                SocketManage.init(new getGem());
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessage(String message) {
        if (message.equals("gem")) {
            SocketManage.init(new getGem());
        }
    }

    private void initSmart() {
        binding.Swipe.setOnRefreshListener(this);
    }

    private void initView() {
        binding.game.setOnClickListener(this);
        binding.transaction.setOnClickListener(this);
    }

    @Override
    public void StatusBar(Activity activity) {
        StatusBarUtil.setImmersiveStatusBar(activity, false);
    }

    private void initPager() {
        PagerAdapter pagerAdapter = new PagerAdapter(recyclerAdapters);
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
        //binding.pager.setUserInputEnabled(false);
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

    @Override
    public void handle(String ds) {
        
        JSONObject jsonObject = JSONObject.parseObject(ds);
        int code = jsonObject.getInteger("code");
        if (code == 200) {
            String gem = jsonObject.getString("gem");
            String day_gem = jsonObject.getString("day_gem");
            binding.dayGem.setText(StringUtil.toRe(day_gem));
            binding.gem.setText(StringUtil.toRe(gem));
            JSONArray data = jsonObject.getJSONArray("data");
            if (data == null) {
                return;
            }
            recyclerAdapters.clear();
            binding.tab.removeAllTabs();
            for (int i = 0; i < data.size(); i++) {
                JSONObject jsonObject1 = data.getJSONObject(i);
                String id = jsonObject1.getString("id");
                String name = jsonObject1.getString("name");
                //因为id不可能为0所以-1
                int _id = Integer.parseInt(id);
                binding.tab.addTab(binding.tab.newTab().setText(name).setId(_id - 1));
                recyclerAdapters.add(new MiningItemPager(context, id));
            }
            initPager();
            binding.Swipe.setRefreshing(false);
        }
    }

    @Override
    public void error(String error) {
        binding.Swipe.setRefreshing(false);
        
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.game) {
            ActivityTask.getInstance().getBinding().pager.setCurrentItem(0);
        } else if (v.getId() == R.id.transaction) {
            context.startActivity(new Intent(context, C2CActivity.class));
        }
    }

    @Override
    public void onRefresh() {
        SocketManage.init(this);
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

    private class getGem implements OnData {
        @Override
        public void connect(SocketManage socketManage) {
            SharedUtil sharedUtil = new SharedUtil(getContext());
            JSONObject jsonObject = sharedUtil.getLogin(8, 1);
            socketManage.print(jsonObject.toString());
        }

        @Override
        public void handle(String ds) {
            JSONObject jsonObject = JSONObject.parseObject(ds);
            int code = jsonObject.getInteger("code");
            if (code == 200) {
                String gem = jsonObject.getString("gem");
                String day_gem = jsonObject.getString("day_gem");
                binding.gem.setText(StringUtil.toRe(gem));
                binding.dayGem.setText(StringUtil.toRe(day_gem));
            }
        }
    }
}
