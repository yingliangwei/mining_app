package com.mining.mining.activity.invite;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mining.mining.databinding.ItemInviteBinding;
import com.mining.mining.entity.InviteEntity;
import com.mining.util.StringUtil;

import java.util.List;

public class InviteAdapter extends RecyclerView.Adapter<InviteAdapter.ViewHolder> {
    private final Activity context;
    private final List<InviteEntity> list;
    private View mEmptyTextView;

    public InviteAdapter(Activity context, List<InviteEntity> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        @NonNull ItemInviteBinding binding = ItemInviteBinding.inflate(LayoutInflater.from(context), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        InviteEntity entity = list.get(position);
        holder.binding.name.setText(entity.getName());
        holder.binding.nameX.setText(StringUtil.getStringStart(entity.getName()));
        holder.binding.id.setText(entity.getInvite_id());
        holder.binding.text.setText(StringUtil.toRe(entity.getGem()));
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
        public ItemInviteBinding binding;

        public ViewHolder(@NonNull ItemInviteBinding itemView) {
            super(itemView.getRoot());
            this.binding = itemView;
        }
    }
}
