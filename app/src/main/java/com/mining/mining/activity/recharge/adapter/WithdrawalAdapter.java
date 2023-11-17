package com.mining.mining.activity.recharge.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mining.mining.databinding.ItemWithdrawlBinding;
import com.mining.mining.entity.WithdrawalEntity;
import com.mining.util.StringUtil;

import java.util.List;

public class WithdrawalAdapter extends RecyclerView.Adapter<WithdrawalAdapter.ViewHolder> {
    private final Context context;
    private final List<WithdrawalEntity> entities;

    public WithdrawalAdapter(Context context, List<WithdrawalEntity> entities) {
        this.context = context;
        this.entities = entities;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(ItemWithdrawlBinding.inflate(LayoutInflater.from(context), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        WithdrawalEntity entity = entities.get(position);
        switch (entity.getType()) {
            case "0":
                holder.binding.name.setText("审核中");
                break;
            case "1":
                holder.binding.name.setText("拒绝提现");
                break;
            case "2":
                holder.binding.name.setText("提现成功");
                break;
            case "3":
                holder.binding.name.setText("提现失败");
                break;
        }
        holder.binding.text.setText(entity.getText());
        holder.binding.usdt.setText(StringUtil.toRe(entity.getUsdt()));
        holder.binding.time.setText(entity.getTime());
    }

    @Override
    public int getItemCount() {
        return entities.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ItemWithdrawlBinding binding;

        public ViewHolder(@NonNull ItemWithdrawlBinding itemView) {
            super(itemView.getRoot());
            this.binding = itemView;
        }
    }
}
