package com.mining.mining.adapter;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PagerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final List<RecyclerAdapter> adapterList;

    public PagerAdapter(List<RecyclerAdapter> adap) {
        this.adapterList = adap;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return adapterList.get(viewType).onCreateViewHolder(parent, viewType);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        adapterList.get(position).onBindViewHolder(holder, position);
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return adapterList.size();
    }
}
