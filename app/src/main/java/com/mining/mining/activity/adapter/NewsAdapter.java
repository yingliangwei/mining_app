package com.mining.mining.activity.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mining.mining.R;
import com.mining.mining.activity.NewsDetailActivity;
import com.mining.mining.databinding.ItemNewsBinding;
import com.mining.mining.entity.NewsEntity;

import java.util.List;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.ViewHolder> {
    private final Context context;
    private final List<NewsEntity> entities;

    public NewsAdapter(Context context, List<NewsEntity> entities) {
        this.context = context;
        this.entities = entities;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(ItemNewsBinding.inflate(LayoutInflater.from(context), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        NewsEntity entity = entities.get(position);
        holder.binding.title.setText(entity.getTitle());
        holder.binding.message.setText(entity.getName());
        holder.binding.time.setText(entity.getTime());
        if (entity.getIsNew() == 1) {
            holder.binding.isNews.setVisibility(View.GONE);
        }
        holder.binding.click.setTag(position);
        holder.binding.click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = (int) v.getTag();
                NewsEntity entity = entities.get(position);
                Intent intent = new Intent(context, NewsDetailActivity.class);
                intent.putExtra("json", entity.json);
                context.startActivity(intent);
                holder.binding.isNews.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public int getItemCount() {
        return entities.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ItemNewsBinding binding;

        public ViewHolder(@NonNull ItemNewsBinding itemView) {
            super(itemView.getRoot());
            this.binding = itemView;
        }
    }
}
