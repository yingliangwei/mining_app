package com.mining.mining.activity.c2s.usdt.pager.pager.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mining.mining.activity.c2s.usdt.activity.BuyUsdtActivity;
import com.mining.mining.databinding.ItemC2cUsdtBinding;
import com.mining.mining.entity.C2cUsdtEntity;
import com.mining.util.StringUtil;

import java.util.List;

public class C2cUsdtAdapter extends RecyclerView.Adapter<C2cUsdtAdapter.ViewHolder> implements View.OnClickListener {
    private final Context context;
    private View mEmptyTextView;
    private final List<C2cUsdtEntity> entities;
    private final int type;

    public C2cUsdtAdapter(Context context, List<C2cUsdtEntity> entities, int type) {
        this.context = context;
        this.entities = entities;
        this.type = type;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(ItemC2cUsdtBinding.inflate(LayoutInflater.from(context), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        C2cUsdtEntity entity = entities.get(position);
        holder.binding.gem.setText(StringUtil.toRe(entity.getUsdt()));
        holder.binding.usdt.setText(StringUtil.toRe(entity.getPrice()));
        holder.binding.getRoot().setTag(position);
        holder.binding.getRoot().setOnClickListener(this);
        holder.binding.name.setText(entity.getName());
        holder.binding.nameX.setText(StringUtil.getStringStart(entity.getName()));
        String text = switch (entity.getType()) {
            case "1" -> "微信";
            case "2" -> "银行卡";
            default -> "支付宝";
        };
        holder.binding.pay.setText(text);
        if (type == 2) {
            holder.binding.exit.setText("售出");
        }
    }


    @Override
    public int getItemCount() {
        if (entities.size() != 0 && mEmptyTextView != null) {
            mEmptyTextView.setVisibility(View.GONE);
        } else if (mEmptyTextView != null) {
            mEmptyTextView.setVisibility(View.VISIBLE);
        }
        return entities.size();
    }


    public void setEmptyTextView(LinearLayout blank) {
        mEmptyTextView = blank;
    }

    @Override
    public void onClick(View v) {
        int position = (int) v.getTag();
        C2cUsdtEntity entity = entities.get(position);
        Intent intent = new Intent(context, BuyUsdtActivity.class);
        intent.putExtra("id", entity.getId());
        intent.putExtra("type", type);
        context.startActivity(intent);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ItemC2cUsdtBinding binding;

        public ViewHolder(@NonNull ItemC2cUsdtBinding itemView) {
            super(itemView.getRoot());
            this.binding = itemView;
        }
    }
}
