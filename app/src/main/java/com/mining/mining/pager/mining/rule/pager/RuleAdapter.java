package com.mining.mining.pager.mining.rule.pager;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mining.mining.databinding.ItemRuleBinding;
import com.mining.mining.entity.RuleEntity;

import java.util.List;

public class RuleAdapter extends RecyclerView.Adapter<RuleAdapter.ViewHolder> {
    private final Context context;
    private final List<RuleEntity> list;

    public RuleAdapter(Context context, List<RuleEntity> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(ItemRuleBinding.inflate(LayoutInflater.from(context), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RuleEntity entity = list.get(position);
        holder.binding.message.setText(entity.getMessage());
        if (entity.getIs_title().equals("0")) {
            holder.binding.title.setText(entity.getTitle());
        } else {
            holder.binding.title.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ItemRuleBinding binding;

        public ViewHolder(@NonNull ItemRuleBinding itemView) {
            super(itemView.getRoot());
            this.binding = itemView;
        }
    }
}
