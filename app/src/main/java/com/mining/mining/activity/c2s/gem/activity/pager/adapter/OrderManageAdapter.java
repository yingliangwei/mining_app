package com.mining.mining.activity.c2s.gem.activity.pager.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mining.mining.R;
import com.mining.mining.activity.c2s.gem.activity.pager.activity.DetailedActivity;
import com.mining.mining.databinding.ItemOrderManagementBinding;
import com.mining.mining.entity.OrderManageEntity;
import com.mining.util.ArithHelper;

import java.util.List;

public class OrderManageAdapter extends RecyclerView.Adapter<OrderManageAdapter.ViewHolder> implements View.OnClickListener {
    private final Context context;
    private final List<OrderManageEntity> entities;
    private final String data_type;

    public OrderManageAdapter(Context context, List<OrderManageEntity> entities, String data_type) {
        this.context = context;
        this.entities = entities;
        this.data_type = data_type;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(ItemOrderManagementBinding.inflate(LayoutInflater.from(context), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OrderManageEntity entity = entities.get(position);
        if (data_type.equals("1")) {
            holder.binding.name.setText(context.getText(R.string.bug_gem));
        } else {
            holder.binding.name.setText(context.getText(R.string.sell_buy));
        }
        holder.binding.number.setText(entity.getNumber());
        holder.binding.usdt.setText(entity.getUsdt());
        holder.binding.userName.setText(entity.getName());
        holder.binding.time.setText(entity.getTime());
        double all = ArithHelper.mul(entity.getUsdt(), entity.getNumber());
        double premium = Double.parseDouble(entity.getPremium());
        double allUsdt = ArithHelper.add(all, premium);
        holder.binding.allUsdt.setText(String.format(context.getText(R.string.susdt).toString(), allUsdt));
        holder.binding.getRoot().setTag(position);
        holder.binding.getRoot().setOnClickListener(this);
    }

    @Override
    public int getItemCount() {
        return entities.size();
    }

    @Override
    public void onClick(View v) {
        int position = (int) v.getTag();
        OrderManageEntity entity = entities.get(position);
        Intent intent = new Intent(context, DetailedActivity.class);
        intent.putExtra("data_type", data_type);
        intent.putExtra("data_id", entity.getId());
        context.startActivity(intent);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ItemOrderManagementBinding binding;

        public ViewHolder(@NonNull ItemOrderManagementBinding itemView) {
            super(itemView.getRoot());
            this.binding = itemView;
        }
    }
}
