package com.mining.mining.pager.home.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.mining.mining.activity.PreloadActivity;
import com.mining.mining.databinding.ItemVerticalHomeBinding;
import com.mining.mining.entity.PluginEntity;

import java.util.List;

public class ItemVerticalAdapter extends RecyclerView.Adapter<ItemVerticalAdapter.ViewHolder> implements View.OnClickListener {
    private final Context context;
    private final List<PluginEntity> list;
    private View mEmptyTextView;

    public ItemVerticalAdapter(Context context, List<PluginEntity> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(ItemVerticalHomeBinding.inflate(LayoutInflater.from(context), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PluginEntity entity = list.get(position);
        Glide.with(context).load(entity.getImage()).into(holder.binding.image);
        holder.binding.title.setText(entity.getName());
        holder.binding.getRoot().setId(position);
        holder.binding.getRoot().setOnClickListener(this);
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

    @Override
    public void onClick(View v) {
        PluginEntity entity = list.get(v.getId());
        Intent intent = new Intent(context, PreloadActivity.class);
        intent.putExtra("json", entity.getJson());
        context.startActivity(intent);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ItemVerticalHomeBinding binding;

        public ViewHolder(@NonNull ItemVerticalHomeBinding itemView) {
            super(itemView.getRoot());
            this.binding = itemView;
        }
    }
}
