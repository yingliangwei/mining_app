package com.mining.mining.pager.c2c.adapter;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mining.mining.activity.c2s.BuyActivity;
import com.mining.mining.activity.c2s.C2CActivity;
import com.mining.mining.activity.c2s.SellActivity;
import com.mining.mining.databinding.ItemC2cBinding;
import com.mining.mining.entity.C2cEntity;

import java.util.List;

public class C2cAdapter extends RecyclerView.Adapter<C2cAdapter.ViewHolder> implements View.OnClickListener {
    public final List<C2cEntity> list;
    public final Activity activity;
    private final int type;

    public C2cAdapter(List<C2cEntity> list, Activity activity, int type) {
        this.activity = activity;
        this.type = type;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(ItemC2cBinding.inflate(LayoutInflater.from(activity), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        C2cEntity entity = list.get(position);
        holder.binding.gem.setText(entity.getArticle());
        holder.binding.usdt.setText(entity.getUsdt());
        holder.binding.buy.setTag(position);
        holder.binding.buy.setOnClickListener(this);
        if (type != 1) {
            holder.binding.buy.setText("出售");
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public void onClick(View v) {
        int position = (int) v.getTag();
        C2cEntity entity = list.get(position);
        Intent intent;
        if (type == 1) {
            intent = new Intent(activity, BuyActivity.class);
        } else {
            intent = new Intent(activity, SellActivity.class);
        }
        intent.putExtra("id", entity.getId());
        activity.startActivity(intent);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ItemC2cBinding binding;

        public ViewHolder(@NonNull ItemC2cBinding itemView) {
            super(itemView.getRoot());
            this.binding = itemView;
        }
    }
}
