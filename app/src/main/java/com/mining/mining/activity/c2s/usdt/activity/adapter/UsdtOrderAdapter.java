package com.mining.mining.activity.c2s.usdt.activity.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mining.mining.R;
import com.mining.mining.activity.c2s.usdt.activity.order.activity.DetailedActivity;
import com.mining.mining.databinding.ItemOrderManagementBinding;
import com.mining.mining.entity.UsdtOrderEntity;
import com.mining.mining.util.StringUtil;

import java.util.List;

public class UsdtOrderAdapter extends RecyclerView.Adapter<UsdtOrderAdapter.ViewHolder> implements View.OnClickListener {
    private final Context context;
    private final List<UsdtOrderEntity> strings;
    private final String data_type;
    private final boolean usdt_type;

    public UsdtOrderAdapter(Context context, List<UsdtOrderEntity> strings, String data_type, boolean usdt_type) {
        this.context = context;
        this.strings = strings;
        this.data_type = data_type;
        this.usdt_type = usdt_type;
    }

    public UsdtOrderAdapter(Context context, List<UsdtOrderEntity> strings, String data_type) {
        this.context = context;
        this.strings = strings;
        this.data_type = data_type;
        this.usdt_type = false;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(ItemOrderManagementBinding.inflate(LayoutInflater.from(context), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        UsdtOrderEntity orderEntity = strings.get(position);
        if (data_type.equals("1")) {
            holder.binding.name.setText(R.string.app_buy_usdt);
        } else {
            holder.binding.name.setText(R.string.app_sell_usdt);
        }
        switch (orderEntity.type) {
            case "0" -> {
                holder.binding.type.setText("等待付款");
                if (StringUtil.isTimeExceeded(orderEntity.time)) {
                    holder.binding.type.setText("超时");
                }
            }
            case "1" -> holder.binding.type.setText("等待商家确认");
            case "2" -> holder.binding.type.setText("已完成");
        }
        holder.binding.time.setText(orderEntity.time);
        holder.binding.userName.setText(orderEntity.name);
        holder.binding.usdt.setText(orderEntity.rmb);
        holder.binding.allUsdt.setText(String.format("%sRMB", orderEntity.rmb));
        holder.binding.number.setText(orderEntity.usdt);
        holder.binding.getRoot().setTag(position);
        holder.binding.getRoot().setOnClickListener(this);

        String text = switch (orderEntity.payType) {
            case "1" -> "微信";
            case "2" -> "银行卡";
            default -> "支付宝";
        };
        holder.binding.pay.setText(text);
    }

    @Override
    public int getItemCount() {
        return strings.size();
    }

    @Override
    public void onClick(View v) {
        int position = (int) v.getTag();
        UsdtOrderEntity entity = strings.get(position);
        Intent intent = new Intent(context, DetailedActivity.class);
        if (usdt_type) {
            intent = new Intent(context, com.mining.mining.activity.c2s.usdt.activity.root.DetailedActivity.class);
        }
        intent.putExtra("data_type", data_type);
        intent.putExtra("data_id", entity.id);
        context.startActivity(intent);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final ItemOrderManagementBinding binding;

        public ViewHolder(@NonNull ItemOrderManagementBinding itemView) {
            super(itemView.getRoot());
            this.binding = itemView;
        }
    }
}
