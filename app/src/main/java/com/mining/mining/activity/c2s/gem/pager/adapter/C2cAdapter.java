package com.mining.mining.activity.c2s.gem.pager.adapter;


import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mining.mining.activity.c2s.gem.activity.BuyActivity;
import com.mining.mining.activity.c2s.gem.activity.SellActivity;
import com.mining.mining.databinding.ItemC2cBinding;
import com.mining.mining.entity.C2cEntity;
import com.mining.util.StringUtil;

import java.util.List;

public class C2cAdapter extends RecyclerView.Adapter<C2cAdapter.ViewHolder> implements View.OnClickListener {
    public final List<C2cEntity> list;
    public final Context activity;
    private final int type;
    private View mEmptyTextView;

    public C2cAdapter(List<C2cEntity> list, Context activity, int type) {
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
        holder.binding.usdt.setText(StringUtil.toRe(entity.getUsdt()));
        holder.binding.getRoot().setTag(position);
        holder.binding.getRoot().setOnClickListener(this);
        holder.binding.name.setText(entity.getName());
        holder.binding.nameX.setText(StringUtil.getStringStart(entity.getName()));
        if (entity.getIs_authentication() == 1) {
            holder.binding.v.setVisibility(View.VISIBLE);
        }
    }

    public void setEmptyTextView(View emptyTextView) {
        mEmptyTextView = emptyTextView;
    }

    @Override
    public int getItemCount() {
        if (list.size() != 0 && mEmptyTextView != null) {
            mEmptyTextView.setVisibility(View.GONE);
        } else if (mEmptyTextView != null) {
            mEmptyTextView.setVisibility(View.VISIBLE);
        }
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
