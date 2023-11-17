package com.mining.mining.pager.mining.rule;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.google.android.material.tabs.TabLayout;
import com.mining.mining.adapter.PagerAdapter;
import com.mining.mining.adapter.RecyclerAdapter;
import com.mining.mining.databinding.ActivityRuleBinding;
import com.mining.mining.pager.mining.rule.pager.RulePager;
import com.mining.mining.util.SharedUtil;
import com.mining.util.StatusBarUtil;
import com.xframe.network.OnData;
import com.xframe.network.SocketManage;

import java.util.ArrayList;
import java.util.List;

public class RuleActivity extends AppCompatActivity implements OnData {
    private ActivityRuleBinding binding;
    private PagerAdapter pagerAdapter;
    private final List<RecyclerAdapter> recyclerAdapters = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtil.setImmersiveStatusBar(this, false);
        binding = ActivityRuleBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initToolbar();
        initPager();
        initTab();
        SocketManage.init(this);
    }

    private void initToolbar() {
        binding.toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void initTab() {
        binding.tab.addOnTabSelectedListener(new TabSelectedListener());
    }

    private void getTab_id() {
        String id = getIntent().getStringExtra("id");
        if (id == null) {
            return;
        }
        int _id = Integer.parseInt(id);
        binding.pager.setCurrentItem(_id - 1);
    }

    private void initPager() {
        pagerAdapter = new PagerAdapter(recyclerAdapters);
        binding.pager.setAdapter(pagerAdapter);
        binding.pager.registerOnPageChangeCallback(new PageChangeCallback());
    }

    @Override
    public void error(String error) {
        binding.spinKit.setVisibility(View.GONE);
    }

    @Override
    public void connect(SocketManage socketManage) {
        SharedUtil sharedUtil = new SharedUtil(this);
        JSONObject jsonObject = sharedUtil.getLogin(8, 2);
        socketManage.print(jsonObject.toString());
    }

    @Override
    public void handle(String ds) {
        binding.spinKit.setVisibility(View.GONE);
        JSONObject jsonObject = JSONObject.parseObject(ds);
        int code = jsonObject.getInteger("code");
        if (code == 200) {
            JSONArray data = jsonObject.getJSONArray("data");
            if (data == null) {
                return;
            }
            for (int i = 0; i < data.size(); i++) {
                JSONObject jsonObject1 = data.getJSONObject(i);
                String id = jsonObject1.getString("id");
                String name = jsonObject1.getString("name");
                //因为id不可能为0所以-1
                int _id = Integer.parseInt(id);
                binding.tab.addTab(binding.tab.newTab().setText(name).setId(_id - 1));
                recyclerAdapters.add(new RulePager(this, id));
                pagerAdapter.notifyItemChanged(recyclerAdapters.size() - 1);
            }
            getTab_id();
        }
    }

    private class PageChangeCallback extends ViewPager2.OnPageChangeCallback {
        @Override
        public void onPageSelected(int position) {
            TabLayout.Tab tab = binding.tab.getTabAt(position);
            if (tab == null) {
                return;
            }
            tab.select();
        }
    }

    private class TabSelectedListener implements TabLayout.OnTabSelectedListener {

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
    }
}
