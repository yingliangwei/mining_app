package com.mining.listen.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mining.listen.databinding.ItemMainBinding;
import com.mining.listen.entity.MainEntity;

import java.util.List;

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.ViewHolder> {
    private final List<MainEntity> entities;
    private final Context context;

    public MainAdapter(Context context, List<MainEntity> entities) {
        this.context = context;
        this.entities = entities;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(ItemMainBinding.inflate(LayoutInflater.from(context), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MainEntity entity = entities.get(position);
        holder.binding.amt.setText(entity.getAmt());
        holder.binding.text.setText(entity.getText());
        holder.binding.ccy.setText(entity.getCcy());
        holder.binding.json.setText(entity.getJson());
        holder.binding.pTime.setText(entity.getPTime());
    }

    @Override
    public int getItemCount() {
        return entities.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public ItemMainBinding binding;

        public ViewHolder(@NonNull ItemMainBinding itemView) {
            super(itemView.getRoot());
            this.binding = itemView;
        }
    }
}
