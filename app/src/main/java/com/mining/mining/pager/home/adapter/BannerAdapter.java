package com.mining.mining.pager.home.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.mining.mining.R;
import com.mining.mining.activity.PreloadActivity;
import com.mining.mining.databinding.BannerItemTextImageBinding;
import com.mining.mining.entity.BannerEntity;

import java.util.List;

public class BannerAdapter extends com.youth.banner.adapter.BannerAdapter<BannerEntity, BannerAdapter.BannerViewHolder> implements View.OnClickListener {
    public Context context;

    public BannerAdapter(List<BannerEntity> datas, Context context) {
        super(datas);
        this.context = context;
    }

    @Override
    public BannerViewHolder onCreateHolder(ViewGroup parent, int viewType) {
        return new BannerViewHolder(BannerItemTextImageBinding.inflate(LayoutInflater.from(context), parent, false));
    }

    @Override
    public void onBindView(BannerViewHolder holder, BannerEntity data, int position, int size) {
        Glide.with(context).load(data.image).into(holder.binding.image);
        holder.binding.title.setText(data.name);
        holder.binding.image.setTag(position);
        holder.binding.image.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.image) {
            int position = (int) v.getTag();
            BannerEntity entity = getData(position);
            Intent intent = new Intent(context, PreloadActivity.class);
            intent.putExtra("json", entity.json);
            context.startActivity(intent);
        }
    }

    static class BannerViewHolder extends RecyclerView.ViewHolder {
        BannerItemTextImageBinding binding;

        public BannerViewHolder(@NonNull BannerItemTextImageBinding view) {
            super(view.getRoot());
            this.binding = view;
        }
    }
}
