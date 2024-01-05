package com.mining.mining.activity.c2s.usdt.activity;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.alibaba.fastjson2.JSONObject;
import com.google.android.material.navigation.NavigationBarView;
import com.mining.mining.R;
import com.mining.mining.activity.c2s.usdt.pager.AddUsdtC2cPager;
import com.mining.mining.activity.c2s.usdt.pager.HomeUsdtC2cPager;
import com.mining.mining.adapter.PagerAdapter;
import com.mining.mining.adapter.RecyclerAdapter;
import com.mining.mining.databinding.ActivityUsdtC2cBinding;
import com.mining.mining.util.SharedUtil;
import com.mining.util.StatusBarUtil;
import com.xframe.network.OnData;
import com.xframe.network.SocketManage;

import java.util.ArrayList;
import java.util.List;

public class UsdtC2cActivity extends AppCompatActivity implements NavigationBarView.OnItemSelectedListener, OnData {
    private ActivityUsdtC2cBinding binding;
    private final List<RecyclerAdapter> recyclerAdapters = new ArrayList<>();
    private PagerAdapter pagerAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtil.setImmersive(this, true);
        binding = ActivityUsdtC2cBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initPager();
        initView();
        SocketManage.init(this);
    }

    private void initView() {
        binding.bottom.setVisibility(View.GONE);
        binding.bottom.setOnItemSelectedListener(this);
    }

    private void initPager() {
        recyclerAdapters.add(new HomeUsdtC2cPager(this));
        pagerAdapter = new PagerAdapter(recyclerAdapters);
        binding.pager.setAdapter(pagerAdapter);
        binding.pager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                recyclerAdapters.get(position).StatusBar(UsdtC2cActivity.this);
                binding.bottom.getMenu().getItem(position).setChecked(true);
            }
        });
        binding.pager.setCurrentItem(0);
    }

    @Override
    public void connect(SocketManage socketManage) {
        SharedUtil sharedUtil = new SharedUtil(this);
        JSONObject jsonObject = sharedUtil.getLogin(4, 14);
        jsonObject.put("data_type", "2");
        socketManage.print(jsonObject.toString());
    }

    @Override
    public void handle(String ds) {
        JSONObject jsonObject = JSONObject.parseObject(ds);
        int code = jsonObject.getInteger("code");
        if (code == 200) {
            int is = jsonObject.getInteger("is");
            if (is != 0) {
                binding.bottom.getMenu().findItem(R.id.add).setVisible(true);
                recyclerAdapters.add(new AddUsdtC2cPager(this));
                pagerAdapter.notifyItemChanged(1);
                binding.pager.setCurrentItem(recyclerAdapters.size());
                binding.pager.setCurrentItem(0);
                binding.bottom.setVisibility(View.VISIBLE);
            }
        }
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        recyclerAdapters.get(item.getOrder()).StatusBar(this);
        binding.pager.setCurrentItem(item.getOrder());
        return true;
    }
}
