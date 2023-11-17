package com.xframe.widget.recycler.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.xframe.widget.R;
import com.xframe.widget.XfRecyclerView;
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
        View view = LayoutInflater.from(context).inflate(R.layout.item_recycler, parent, false);
        return new ViewHolder(context, view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.setIsRecyclable(false);
        ItemRecyclerViewAdapter itemRecyclerViewAdapter = new ItemRecyclerViewAdapter(context, listList.get(position));
        itemRecyclerViewAdapter.setOnRecyclerItemClickListener(onRecyclerItemClickListener);
        //添加Android自带的分割线
        holder.recyclerView.addItemDecoration(new DisplayUtils(context, DividerItemDecoration.VERTICAL));
        holder.recyclerView.setAdapter(itemRecyclerViewAdapter);
    }


    @Override
    public int getItemCount() {
        return listList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final RecyclerView recyclerView;

        public ViewHolder(Context context, @NonNull View itemView) {
            super(itemView);
            recyclerView = itemView.findViewById(R.id.recycler);
            //不能写在onBindViewHolder里面，不然有问题
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
        }
    }
}
