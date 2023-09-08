package com.mining.mining.pager.home;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mining.mining.adapter.RecyclerAdapter;
import com.mining.mining.databinding.PagerHomeBinding;
import com.mining.mining.pager.holder.ViewHolder;
import com.mining.mining.util.StatusBarUtil;

public class HomePager extends RecyclerAdapter {
    private final Activity context;
    private PagerHomeBinding binding;

    public HomePager(Activity context) {
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = PagerHomeBinding.inflate(LayoutInflater.from(context), parent, false);
        return new ViewHolder(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        initTab();
    }

    private void initTab() {
        binding.tab.addTab(binding.tab.newTab().setText("热门"));
        binding.tab.addTab(binding.tab.newTab().setText("最新"));
    }

    @Override
    public void StatusBar(Activity activity) {
        StatusBarUtil.setImmersiveStatusBar(context, true);
    }
}
