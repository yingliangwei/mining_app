package com.mining.mining.activity.c2s;

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
import com.mining.mining.activity.c2s.pager.AddC2cPager;
import com.mining.mining.activity.c2s.pager.HomeC2sPager;
import com.mining.mining.adapter.PagerAdapter;
import com.mining.mining.adapter.RecyclerAdapter;
import com.mining.mining.databinding.ActivityC2sBinding;
import com.mining.mining.util.SharedUtil;
import com.mining.util.StatusBarUtil;
import com.xframe.network.OnData;
import com.xframe.network.SocketManage;

import java.util.ArrayList;
import java.util.List;

public class C2CActivity extends AppCompatActivity implements NavigationBarView.OnItemSelectedListener, OnData {
    private ActivityC2sBinding binding;
    private final List<RecyclerAdapter> recyclerAdapters = new ArrayList<>();
    private PagerAdapter pagerAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 设置状态栏字体黑色
        StatusBarUtil.setImmersive(this, true);
        binding = ActivityC2sBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initViewPager();
        initBottom();
        SocketManage.init(this);
    }

    private void initBottom() {
        binding.bottom.getMenu().findItem(R.id.add).setVisible(false);
        binding.bottom.setOnItemSelectedListener(this);
    }

    @Override
    public void connect(SocketManage socketManage) {
        try {
            SharedUtil sharedUtil = new SharedUtil(this);
            JSONObject jsonObject = sharedUtil.getLogin(4, 14);
            socketManage.print(jsonObject.toString());
        } catch (Exception e) {
            e.fillInStackTrace();
        }
    }

    @Override
    public void handle(String ds) {
        JSONObject jsonObject = JSONObject.parseObject(ds);
        int code = jsonObject.getInteger("code");
        if (code == 200) {
            int is = jsonObject.getInteger("is");
            if (is != 0) {
                binding.bottom.getMenu().findItem(R.id.add).setVisible(true);
                recyclerAdapters.add(new AddC2cPager(this));
                pagerAdapter.notifyItemChanged(1);
                binding.pager.setCurrentItem(recyclerAdapters.size());
                binding.pager.setCurrentItem(0);
                binding.bottom.setVisibility(View.VISIBLE);
            }
        }
    }

    private void initViewPager() {
        recyclerAdapters.add(new HomeC2sPager(this));
        pagerAdapter = new PagerAdapter(recyclerAdapters);
        binding.pager.setAdapter(pagerAdapter);
        binding.pager.setCurrentItem(recyclerAdapters.size());
        binding.pager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                recyclerAdapters.get(position).StatusBar(C2CActivity.this);
                binding.bottom.getMenu().getItem(position).setChecked(true);
            }
        });
        binding.pager.setCurrentItem(0);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        recyclerAdapters.get(item.getOrder()).StatusBar(C2CActivity.this);
        binding.pager.setCurrentItem(item.getOrder());
        return true;
    }
}
