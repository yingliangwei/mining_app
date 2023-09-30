package com.mining.mining.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mining.util.StatusBarUtil;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final Context context;

    public RecyclerAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

    }

    public void onDestroy() {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public void StatusBar(Activity activity) {
        StatusBarUtil.setImmersiveStatusBar(activity, true);
    }

    public Context getContext() {
        return context;
    }
}
