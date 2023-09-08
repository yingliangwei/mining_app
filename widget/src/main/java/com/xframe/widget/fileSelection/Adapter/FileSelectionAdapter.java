package com.xframe.widget.fileSelection.Adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.xframe.widget.R;
import com.xframe.widget.databinding.ItemFileSelectionHomeBinding;
import com.xframe.widget.util.FileSizeUtil;
import com.xframe.widget.util.FileUtil;

import java.io.File;
import java.util.List;

public class FileSelectionAdapter extends RecyclerView.Adapter<FileSelectionAdapter.ViewHolder> {
    private final List<File> entities;

    public FileSelectionAdapter(List<File> entities) {
        this.entities = entities;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(ItemFileSelectionHomeBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        File file = entities.get(position);
        if (!file.isDirectory()) {
            holder.binding.image.setImageDrawable(holder.binding.getRoot().getContext().getDrawable(R.mipmap.file));
            holder.binding.card.setBackgroundColor(Color.parseColor("#6699cc"));
            holder.binding.bodysize.setText(String.format("%s", FileSizeUtil.getAutoFileOrFilesSize(file.getAbsolutePath())));
            holder.binding.bodysize.setVisibility(View.VISIBLE);
        } else {
            holder.binding.image.setImageDrawable(holder.binding.getRoot().getContext().getDrawable(R.mipmap.folder));
            holder.binding.card.setBackgroundColor(Color.BLACK);
            holder.binding.bodysize.setVisibility(View.GONE);
        }
        holder.binding.name.setText(file.getName());
        holder.binding.time.setText(FileUtil.getFileLastModifiedTime(file));
    }

    @Override
    public int getItemCount() {
        return entities.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        public ItemFileSelectionHomeBinding binding;

        public ViewHolder(@NonNull ItemFileSelectionHomeBinding itemView) {
            super(itemView.getRoot());
            this.binding = itemView;
        }
    }
}
