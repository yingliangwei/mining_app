package com.mining.mining.pager.mining.item;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mining.mining.adapter.RecyclerAdapter;
import com.mining.mining.databinding.PagerItemMiningBinding;
import com.mining.mining.pager.holder.ViewHolder;

public class ItemPager extends RecyclerAdapter {
    private final Activity activity;

    public ItemPager(Activity activity) {
        this.activity = activity;
    }

    private PagerItemMiningBinding binding;

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = PagerItemMiningBinding.inflate(LayoutInflater.from(activity), parent, false);
        return new ViewHolder(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

    }
}
