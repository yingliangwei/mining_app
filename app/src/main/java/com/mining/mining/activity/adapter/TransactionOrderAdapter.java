package com.mining.mining.activity.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mining.mining.databinding.ItemTransactionOrderBinding;

import java.util.List;

public class TransactionOrderAdapter extends RecyclerView.Adapter<TransactionOrderAdapter.ViewHolder> {
    private final Context context;
    private final List<?> list;
    private View mEmptyTextView;

    public TransactionOrderAdapter(Context context, List<?> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(ItemTransactionOrderBinding.inflate(LayoutInflater.from(context), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

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


    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ItemTransactionOrderBinding binding;

        public ViewHolder(@NonNull ItemTransactionOrderBinding itemView) {
            super(itemView.getRoot());
            this.binding = itemView;
        }
    }
}
