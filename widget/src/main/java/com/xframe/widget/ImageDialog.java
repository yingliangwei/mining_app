package com.xframe.widget;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;

import java.io.OutputStream;

public class ImageDialog extends Dialog implements View.OnLongClickListener {
    private View view;

    public ImageDialog(@NonNull Context context) {
        super(context);
        init();
        initView();
    }

    private void initView() {
        view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_image, new LinearLayout(getContext()));
        ImageView imageView = findViewById(R.id.image);
        imageView.setOnLongClickListener(this);
        setContentView(view);
    }

    @Override
    public <T extends View> T findViewById(int id) {
        return view.findViewById(id);
    }

    private void init() {
        // 设置Dialog的大小
        Window window = getWindow();
        if (window == null) {
            return;
        }
        window.setBackgroundDrawable(new ColorDrawable(Color.BLACK));
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
    }

    @Override
    public boolean onLongClick(View v) {
        ImageView imageView = findViewById(v.getId());
        imageView.getDrawable();
        // 将Drawable对象转换为Bitmap对象
        Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        savaImage(bitmap);
        return true;
    }

    private void savaImage(Bitmap bitmap) {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.DISPLAY_NAME, "ImageName");
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            values.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_DCIM);
        } else {
            values.put(MediaStore.Images.Media.DATA, Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getPath());
        }
        Uri uri = getContext().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        if (uri == null) {
            Toast.makeText(getContext(), "保存失败", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            OutputStream outputStream = getContext().getContentResolver().openOutputStream(uri);
            if (outputStream == null) {
                Toast.makeText(getContext(), "保存失败", Toast.LENGTH_SHORT).show();
                return;
            }
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            outputStream.close();
            MediaScannerConnection.scanFile(getContext(), new String[]{uri.toString()}, null, null);
            Toast.makeText(getContext(), "保存成功", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(getContext(), "保存失败", Toast.LENGTH_SHORT).show();
        }
    }

}
