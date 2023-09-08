package com.mining.mining.pager.mining;

import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.AppBarLayout;
import com.mining.mining.R;
import com.mining.mining.adapter.PagerAdapter;
import com.mining.mining.adapter.RecyclerAdapter;
import com.mining.mining.databinding.PagerMiningBinding;
import com.mining.mining.pager.holder.ViewHolder;
import com.mining.mining.pager.mining.item.ItemPager;
import com.mining.mining.util.StatusBarUtil;
import com.mining.mining.util.TabLayoutUtil;

import java.util.ArrayList;
import java.util.List;

public class MiningPager extends RecyclerAdapter implements TabLayoutUtil.OnSelectedListener, AppBarLayout.OnOffsetChangedListener {
    private Activity context;
    private PagerMiningBinding binding;
    private boolean isShow = true;
    private int scrollRange = -1;

    private final List<RecyclerAdapter> recyclerAdapters = new ArrayList<>();

    public MiningPager(Activity context) {
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
        initTag();
        initView();
        initPager();
    }

    private void initAppBar() {
        binding.appbar.addOnOffsetChangedListener(this);
    }

    private void initPager() {
        recyclerAdapters.add(new ItemPager(context));
        recyclerAdapters.add(new ItemPager(context));
        recyclerAdapters.add(new ItemPager(context));
        PagerAdapter adapter = new PagerAdapter(recyclerAdapters);
        binding.pager.setAdapter(adapter);
        binding.pager.setOffscreenPageLimit(recyclerAdapters.size());
    }

    private void initView() {
        binding.animationView.playAnimation();
        //binding.animationView1.playAnimation();
    }

    private void initTag() {
        binding.tag.addTab(binding.tag.newTab().setText("普通矿山"));
        binding.tag.addTab(binding.tag.newTab().setText("宝石矿山"));
        binding.tag.addTab(binding.tag.newTab().setText("宝藏矿山"));
        TabLayoutUtil.build(binding.tag).setOnSelectedListener(this).enableChangeStyle();
    }

    @Override
    public void StatusBar(Activity activity) {
        StatusBarUtil.setImmersiveStatusBar(activity, false);
    }

    @Override
    public void onSelected(String tabStr) {
        if (tabStr.contains("普通")) {
            binding.tag.setBackground(context.getDrawable(R.mipmap.bg_boring_ape_indicator_left));
        } else if (tabStr.contains("宝石")) {
            binding.tag.setBackground(context.getDrawable(R.mipmap.bg_boring_ape_indicator_center));
        } else if (tabStr.contains("宝藏")) {
            binding.tag.setBackground(context.getDrawable(R.mipmap.bg_boring_ape_indicator_right));
        }
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        if (scrollRange == -1) {
            scrollRange = appBarLayout.getTotalScrollRange();
        }
        System.out.println(verticalOffset + "|" + scrollRange);
        if (scrollRange + verticalOffset - 90 == 0) {
            binding.collapsing.setTitle(context.getString(R.string.app_mining));
            binding.toolbar.setBackgroundColor(Color.WHITE);
            StatusBarUtil.setImmersiveStatusBar(context, true);
            StatusBarUtil.setStatusBarColor(context, Color.WHITE);
            isShow = true;
        } else if (isShow) {
            StatusBarUtil.setStatusBarColor(context, Color.TRANSPARENT);
            binding.toolbar.setBackgroundColor(Color.TRANSPARENT);
            binding.collapsing.setTitle("");//careful there should a space between double quote otherwise it wont work
            StatusBarUtil.setImmersiveStatusBar(context, false);
            isShow = false;
        }
    }
}
