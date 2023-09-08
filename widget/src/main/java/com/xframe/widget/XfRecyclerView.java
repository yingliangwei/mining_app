package com.xframe.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.xframe.widget.entity.RecyclerEntity;
import com.xframe.widget.recycler.OnRecyclerItemClickListener;
import com.xframe.widget.recycler.adapter.RecyclerViewAdapter;

import java.util.ArrayList;
import java.util.List;

public class XfRecyclerView extends RecyclerView {
    private final List<List<RecyclerEntity>> listList = new ArrayList<>();
    private RecyclerViewAdapter recyclerViewAdapter;
    public OnRecyclerItemClickListener recyclerItemClickListener;

    public XfRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public void clear(){
        listList.clear();
    }

    private void init(Context context, AttributeSet attrs) {
        recyclerViewAdapter = new RecyclerViewAdapter(context, listList);
        setLayoutManager(new LinearLayoutManager(context));
        setAdapter(recyclerViewAdapter);
    }

    public void setOnRecyclerItemClickListener(OnRecyclerItemClickListener recyclerItemClickListener) {
        this.recyclerItemClickListener = recyclerItemClickListener;
    }

    public void add(List<List<RecyclerEntity>> listList) {
        this.listList.addAll(listList);
    }

    @SuppressLint("NotifyDataSetChanged")
    public void notifyDataSetChanged() {
        recyclerViewAdapter.setOnRecyclerItemClickListener(this);
        recyclerViewAdapter.notifyDataSetChanged();
    }

}
