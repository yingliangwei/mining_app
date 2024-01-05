package com.mining.mining.activity.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.mining.mining.databinding.ItemHelpBinding;
import com.mining.mining.entity.HelpEntity;

import java.util.List;

public class HelpAdapter extends RecyclerView.Adapter<HelpAdapter.ViewHolder> {
    private final List<HelpEntity> entities;
    private final Context context;

    public HelpAdapter(Context context, List<HelpEntity> entities) {
        this.context = context;
        this.entities = entities;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(ItemHelpBinding.inflate(LayoutInflater.from(context), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        HelpEntity entity = entities.get(position);
        holder.binding.name.setText(entity.getName());
        holder.binding.text.setText(entity.getText());
        holder.binding.wxId.setText(entity.getWx_id());
        holder.binding.tabName.setText(entity.getTab_name());
        Glide.with(context).load(entity.getImage()).into(holder.binding.image);
        initCode(entity.getCode_image(), 400, holder.binding.codeImage);
    }

    private void initCode(String qrCodeData, int qrCodeImageWidth, ImageView imageView) {
        try {
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(qrCodeData, BarcodeFormat.QR_CODE, qrCodeImageWidth, qrCodeImageWidth);
            Bitmap bitmap = Bitmap.createBitmap(qrCodeImageWidth, qrCodeImageWidth, Bitmap.Config.RGB_565);
            for (int x = 0; x < qrCodeImageWidth; x++) {
                for (int y = 0; y < qrCodeImageWidth; y++) {
                    bitmap.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                }
            }
            imageView.setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return entities.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ItemHelpBinding binding;

        public ViewHolder(@NonNull ItemHelpBinding itemView) {
            super(itemView.getRoot());
            this.binding = itemView;
        }
    }
}
