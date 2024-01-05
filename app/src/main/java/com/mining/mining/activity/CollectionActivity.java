package com.mining.mining.activity;

import android.content.ContentValues;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;

import com.alibaba.fastjson2.JSONObject;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.mining.mining.R;
import com.mining.mining.databinding.ActivityCollectionBinding;
import com.mining.mining.util.SharedUtil;
import com.mining.util.StatusBarUtil;

import java.io.OutputStream;

public class CollectionActivity extends AppCompatActivity implements View.OnClickListener {
    private ActivityCollectionBinding binding;
    private Bitmap bitmap;
    private AppCompatEditText editText;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtil.setImmersive(this, true);
        binding = ActivityCollectionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initToolbar();
        initView();
        initClick();
    }

    private void initClick() {
        binding.sava.setOnClickListener(this);
        binding.setMoney.setOnClickListener(this);
    }

    private void initView() {
        SharedUtil sharedUtil = new SharedUtil(this);
        JSONObject data = sharedUtil.getId();
        if (data == null) {
            Toast.makeText(this, "未登录", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        initCode(data.toString(), 600);
    }

    private void initCode(String qrCodeData, int qrCodeImageWidth) {
        try {
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(qrCodeData, BarcodeFormat.QR_CODE, qrCodeImageWidth, qrCodeImageWidth);
            bitmap = Bitmap.createBitmap(qrCodeImageWidth, qrCodeImageWidth, Bitmap.Config.RGB_565);
            for (int x = 0; x < qrCodeImageWidth; x++) {
                for (int y = 0; y < qrCodeImageWidth; y++) {
                    bitmap.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                }
            }
            binding.image.setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }

    private void initToolbar() {
        binding.toolbar.setNavigationOnClickListener(v -> finish());
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.sava) {
            savaImage();
        } else if (R.id.setMoney == v.getId()) {
            dialog();
        }
    }

    private void savaImage() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.DISPLAY_NAME, "ImageName");
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            values.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_DCIM);
        } else {
            values.put(MediaStore.Images.Media.DATA, Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getPath());
        }
        Uri uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        if (uri == null) {
            Toast.makeText(this, "保存失败", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            OutputStream outputStream = getContentResolver().openOutputStream(uri);
            if (outputStream == null) {
                Toast.makeText(this, "保存失败", Toast.LENGTH_SHORT).show();
                return;
            }
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            outputStream.close();
            MediaScannerConnection.scanFile(this, new String[]{uri.toString()}, null, null);
            Toast.makeText(this, "保存成功", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "保存失败", Toast.LENGTH_SHORT).show();
        }
    }

    private void dialog() {
        editText = new AppCompatEditText(this);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("输入收款金额");
        builder.setView(editText);
        builder.setNegativeButton("取消", (dialog, which) -> {
        });
        builder.setPositiveButton("确认", (dialog, which) -> {
            binding.Money.setVisibility(View.VISIBLE);
            binding.Money.setText(String.format("$%s", editText.getText()));
            SharedUtil sharedUtil = new SharedUtil(CollectionActivity.this);
            JSONObject data = sharedUtil.getId();
            if (data == null) {
                return;
            }
            data.put("money", editText.getText());
            initCode(data.toString(), 300);
        });
        builder.show();
    }
}
