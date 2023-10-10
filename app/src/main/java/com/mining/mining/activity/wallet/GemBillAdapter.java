package com.mining.mining.activity.wallet;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mining.mining.R;
import com.mining.mining.databinding.ItemUsdtBillBinding;
import com.mining.mining.entity.GemBillEntity;
import com.mining.util.StringUtil;

import java.util.List;

public class GemBillAdapter extends RecyclerView.Adapter<GemBillAdapter.ViewHolder> {
    private final Context context;
    private final List<GemBillEntity> list;
    private View mEmptyTextView;

    public GemBillAdapter(Context context, List<GemBillEntity> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(ItemUsdtBillBinding.inflate(LayoutInflater.from(context), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        GemBillEntity entity = list.get(position);
       holder.binding.name.setText(entity.getName());
        holder.binding.usdt.setText(StringUtil.toRe(entity.getGem()));
        if (entity.getGem().startsWith("-")) {
            holder.binding.usdt.setTextColor(context.getColor(android.R.color.holo_green_dark));
        }else {
            holder.binding.usdt.setTextColor(Color.RED);
        }
        holder.binding.nameX.setText(entity.getName_x());
        holder.binding.commission.setText(context.getString(R.string.app_commission, StringUtil.toRe(entity.getCommission())));
        holder.binding.balance.setText(context.getString(R.string.app_balance, StringUtil.toRe(entity.getBalance())));
        holder.binding.time.setText(entity.getTime());

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
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ItemUsdtBillBinding binding;

        public ViewHolder(@NonNull ItemUsdtBillBinding itemView) {
            super(itemView.getRoot());
            this.binding = itemView;
        }
    }
}
