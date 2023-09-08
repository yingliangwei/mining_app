package com.xframe.widget.recycler.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.xframe.widget.XfRecyclerView;
import com.xframe.widget.databinding.ItemRecyclerBinding;
import com.xframe.widget.entity.RecyclerEntity;
import com.xframe.widget.recycler.util.DisplayUtils;

import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
    private final List<List<RecyclerEntity>> listList;
    private final Context context;

    public XfRecyclerView onRecyclerItemClickListener;

    public RecyclerViewAdapter(Context context, List<List<RecyclerEntity>> listList) {
        this.listList = listList;
        this.context = context;
    }

    public void setOnRecyclerItemClickListener(XfRecyclerView onRecyclerItemClickListener) {
        this.onRecyclerItemClickListener = onRecyclerItemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(context, ItemRecyclerBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.setIsRecyclable(false);
        ItemRecyclerViewAdapter itemRecyclerViewAdapter = new ItemRecyclerViewAdapter(context, listList.get(position));
        itemRecyclerViewAdapter.setOnRecyclerItemClickListener(onRecyclerItemClickListener);
        //添加Android自带的分割线
        holder.binding.recycler.addItemDecoration(new DisplayUtils(context, DividerItemDecoration.VERTICAL));
        holder.binding.recycler.setAdapter(itemRecyclerViewAdapter);
    }


    @Override
    public int getItemCount() {
        return listList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ItemRecyclerBinding binding;

        public ViewHolder(Context context, @NonNull ItemRecyclerBinding itemView) {
            super(itemView.getRoot());
            //不能写在onBindViewHolder里面，不然有问题
            itemView.recycler.setLayoutManager(new LinearLayoutManager(context));
            this.binding = itemView;
        }
    }
}
