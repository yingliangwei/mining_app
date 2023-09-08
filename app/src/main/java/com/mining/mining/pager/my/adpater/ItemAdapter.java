package com.mining.mining.pager.my.adpater;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mining.mining.databinding.ItemMyBinding;
import com.mining.mining.entity.TextDrawableEntity;

import java.util.List;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ViewHolder> {

    private final Activity activity;
    private final List<TextDrawableEntity> entity;

    public ItemAdapter(Activity activity, List<TextDrawableEntity> entity) {
        this.activity = activity;
        this.entity = entity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(ItemMyBinding.inflate(LayoutInflater.from(activity), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TextDrawableEntity drawable = entity.get(position);
        holder.binding.image.setImageDrawable(drawable.drawable);
        holder.binding.name.setText(drawable.name);
    }

    @Override
    public int getItemCount() {
        return entity.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private ItemMyBinding binding;

        public ViewHolder(@NonNull ItemMyBinding itemView) {
            super(itemView.getRoot());
            this.binding = itemView;
        }
    }
}
