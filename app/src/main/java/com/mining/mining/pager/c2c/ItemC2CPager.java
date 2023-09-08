package com.mining.mining.pager.c2c;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mining.mining.adapter.RecyclerAdapter;
import com.mining.mining.databinding.PagerItemC2cBinding;
import com.mining.mining.pager.c2c.adapter.C2cAdapter;
import com.mining.mining.pager.holder.ViewHolder;

import java.util.ArrayList;
import java.util.List;

public class ItemC2CPager extends RecyclerAdapter {

    private PagerItemC2cBinding binding;
    private final Activity activity;
    private C2cAdapter c2cAdapter;
    private final List<String> list = new ArrayList<>();

    public ItemC2CPager(Activity activity) {
        this.activity = activity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = PagerItemC2cBinding.inflate(LayoutInflater.from(activity), parent, false);
        return new ViewHolder(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
    }

}
