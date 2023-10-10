package com.mining.press.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mining.press.databinding.ItemMainBinding;
import com.mining.press.entity.PressEntity;
import com.mining.util.StringUtil;

import java.util.List;

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.ViewHolder> {
    private final Context context;
    private final List<PressEntity> list;

    public MainAdapter(Context context, List<PressEntity> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(ItemMainBinding.inflate(LayoutInflater.from(context), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PressEntity entity = list.get(position);
        holder.binding.id.setText(String.format("%s期", entity.getId()));
        holder.binding.k.setText(entity.getK());
        holder.binding.stone.setText(StringUtil.toRe(entity.getStone()));
        holder.binding.stoneX.setText(StringUtil.toRe(entity.getStone_x()));
        holder.binding.billK.setText(entity.getBill_k());
        if (entity.getK().equals(entity.getBill_k())) {
            holder.binding.stoneX.setTextColor(context.getColor(android.R.color.holo_green_dark));
            holder.binding.billK.setTextColor(context.getColor(android.R.color.holo_green_dark));
        } else if (entity.getStone_x().startsWith("-")) {
            holder.binding.stoneX.setTextColor(Color.RED);
            holder.binding.billK.setTextColor(Color.RED);
        } else {
            holder.binding.stoneX.setTextColor(holder.binding.id.getTextColors());
            holder.binding.billK.setTextColor(holder.binding.id.getTextColors());
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ItemMainBinding binding;

        public ViewHolder(@NonNull ItemMainBinding itemView) {
            super(itemView.getRoot());
            this.binding = itemView;
        }
    }
}
