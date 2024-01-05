package com.mining.mining.pager.my.adpater;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mining.mining.databinding.ItemMy0Binding;
import com.mining.mining.databinding.ItemMyBinding;
import com.mining.mining.entity.TextDrawableEntity;

import java.util.List;

public class ItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final Activity activity;
    private final List<TextDrawableEntity> entity;

    public ItemAdapter(Activity activity, List<TextDrawableEntity> entity) {
        this.activity = activity;
        this.entity = entity;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        TextDrawableEntity textDrawableEntity = entity.get(viewType);
        if (textDrawableEntity.i == 0) {
            return new ViewHolder(ItemMyBinding.inflate(LayoutInflater.from(activity), parent, false));
        }
        return new ViewHolder2(ItemMy0Binding.inflate(LayoutInflater.from(activity), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        TextDrawableEntity drawable = entity.get(position);
        if (holder instanceof ViewHolder viewHolder) {
            viewHolder.binding.image.setImageDrawable(drawable.drawable);
            viewHolder.binding.name.setText(drawable.name);
        } else if (holder instanceof ViewHolder2 viewHolder2) {
            viewHolder2.binding.image.setImageDrawable(drawable.drawable);
            viewHolder2.binding.name.setText(drawable.name);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return position;
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

    public static class ViewHolder2 extends RecyclerView.ViewHolder {
        private ItemMy0Binding binding;

        public ViewHolder2(@NonNull ItemMy0Binding itemView) {
            super(itemView.getRoot());
            this.binding = itemView;
        }
    }
}
