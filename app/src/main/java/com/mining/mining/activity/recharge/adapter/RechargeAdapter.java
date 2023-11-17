package com.mining.mining.activity.recharge.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mining.mining.activity.recharge.RechargeInformationActivity;
import com.mining.mining.databinding.ItemRechargeBinding;
import com.mining.mining.entity.RechargeEntity;
import com.mining.util.StringUtil;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class RechargeAdapter extends RecyclerView.Adapter<RechargeAdapter.ViewHolder> implements View.OnClickListener {
    private final Context context;
    private final List<RechargeEntity> lists;

    public RechargeAdapter(Context context, List<RechargeEntity> lists) {
        this.context = context;
        this.lists = lists;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(ItemRechargeBinding.inflate(LayoutInflater.from(context), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RechargeEntity entity = lists.get(position);
        if (entity.getType().equals("0")) {
            holder.binding.name.setText("等待充币到账");
        } else {
            holder.binding.name.setText("充值成功");
        }
        String format = "yyyy-MM-dd HH:mm:ss"; // 时间字符串的格式
        if (isExpired(entity.getTime(), format)) {
            // 时间已过期
            holder.binding.name.setText("过期");
        } else {
            holder.binding.getRoot().setId(position);
            holder.binding.getRoot().setOnClickListener(this);
        }
        holder.binding.time.setText(entity.getTime());
        holder.binding.usdt.setText(StringUtil.toRe(entity.getUsdt()));
    }

    public static boolean isExpired(String expirationTime, String format) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        LocalDateTime expirationDateTime = LocalDateTime.parse(expirationTime, formatter);
        LocalDateTime now = LocalDateTime.now();

        return now.isAfter(expirationDateTime);
    }


    @Override
    public int getItemCount() {
        return lists.size();
    }

    @Override
    public void onClick(View v) {
        int position = v.getId();
        RechargeEntity entity = lists.get(position);
        Intent intent = new Intent(context, RechargeInformationActivity.class);
        intent.putExtra("json", entity.getJson());
        context.startActivity(intent);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ItemRechargeBinding binding;

        public ViewHolder(@NonNull ItemRechargeBinding itemView) {
            super(itemView.getRoot());
            this.binding = itemView;
        }
    }
}
