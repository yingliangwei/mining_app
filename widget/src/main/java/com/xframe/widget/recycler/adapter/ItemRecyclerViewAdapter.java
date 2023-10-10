package com.xframe.widget.recycler.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.xframe.widget.R;
import com.xframe.widget.XfRecyclerView;
import com.xframe.widget.entity.RecyclerEntity;

import java.util.List;

public class ItemRecyclerViewAdapter extends RecyclerView.Adapter<ItemRecyclerViewAdapter.ViewHolder> {
    private final List<RecyclerEntity> list;
    private final Context context;
    private XfRecyclerView xfRecyclerView;


    public ItemRecyclerViewAdapter(Context context, List<RecyclerEntity> list) {
        this.list = list;
        this.context = context;
    }

    public void setOnRecyclerItemClickListener(XfRecyclerView recyclerItemClickListener) {
        this.xfRecyclerView = recyclerItemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_item_recycler, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("ResourceType")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RecyclerEntity entity = list.get(position);
        holder.name.setText(entity.name);
        if (entity.drawable != 0) {
            holder.imageView.setImageDrawable(context.getDrawable(entity.drawable));
            holder.imageView.setLayoutParams(new LinearLayout.LayoutParams(150, 150));
        } else if (!entity.src_url.equals("")) {
            Glide.with(context).load(entity.src_url).into(holder.imageView);
        }
        if (!entity.text.equals("")) {
            holder.sub.setText(entity.text);
        }
        if (entity.isArray) {
            holder.imageView.setVisibility(View.GONE);
        }
        if (xfRecyclerView.recyclerItemClickListener != null) {
            holder.ba.setOnClickListener(v -> xfRecyclerView.recyclerItemClickListener.onItemClick(entity, position));
        }
        if (entity.icon != 0) {
            holder.icon.setVisibility(View.VISIBLE);
            holder.icon.setImageDrawable(context.getDrawable(entity.icon));
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView, icon;
        public TextView name, sub;
        public LinearLayout ba;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ba = itemView.findViewById(R.id.ba);
            imageView = itemView.findViewById(R.id.image);
            name = itemView.findViewById(R.id.name);
            sub = itemView.findViewById(R.id.subText);
            icon = itemView.findViewById(R.id.icon);
        }
    }
}
