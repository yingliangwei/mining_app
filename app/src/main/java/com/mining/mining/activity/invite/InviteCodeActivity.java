package com.mining.mining.activity.invite;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BlendMode;
import android.graphics.BlendModeColorFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.AdaptiveIconDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.mining.mining.R;
import com.mining.mining.databinding.ActivityInviteCodeBinding;
import com.mining.util.Handler;
import com.mining.util.OnHandler;
import com.mining.util.PlatformUtil;
import com.mining.util.StatusBarUtil;
import com.xframe.network.OnData;
import com.xframe.network.SocketManage;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import javax.security.auth.callback.Callback;

public class InviteCodeActivity extends AppCompatActivity implements Runnable, View.OnClickListener, OnData {
    private ActivityInviteCodeBinding binding;
    private Bitmap bitmap1, b;
    private String download, image;
    private int qrCodeImageWidth, left, top;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityInviteCodeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        StatusBarUtil.setImmersiveStatusBar(this, false);
        initView();
        SocketManage.init(this);
    }

    private void initView() {
        binding.wxH.setOnClickListener(this);
        binding.wxP.setOnClickListener(this);
        binding.wxD.setOnClickListener(this);
    }

    @Override
    public void connect(SocketManage socketManage) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("type", 16);
            socketManage.print(jsonObject.toString());
        } catch (Exception e) {
            e.fillInStackTrace();
        }
    }

    @Override
    public void handle(String ds) {
        try {
            JSONObject jsonObject = new JSONObject(ds);
            JSONObject data = jsonObject.getJSONObject("data");
            image = data.getString("image");
            download = data.getString("download");
            String left = data.getString("left");
            String top = data.getString("top");
            String qrCodeImageWidth = data.getString("qrCodeImageWidth");
            this.top = Integer.parseInt(top);
            this.left = Integer.parseInt(left);
            this.qrCodeImageWidth = Integer.parseInt(qrCodeImageWidth);
            new Thread(this).start();
        } catch (Exception e) {
            e.fillInStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            b = Glide.with(InviteCodeActivity.this)
                    .asBitmap()
                    .load(image)
                    .submit()
                    .get();
            runOnUiThread(() -> binding.image.setImageBitmap(b));
            initCode(download, qrCodeImageWidth);
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void initCode(String qrCodeData, int qrCodeImageWidth) {
        try {
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(qrCodeData, BarcodeFormat.QR_CODE, qrCodeImageWidth, qrCodeImageWidth);
            Bitmap bitmap = Bitmap.createBitmap(qrCodeImageWidth, qrCodeImageWidth, Bitmap.Config.RGB_565);
            for (int x = 0; x < qrCodeImageWidth; x++) {
                for (int y = 0; y < qrCodeImageWidth; y++) {
                    bitmap.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                }
            }
            bitmap1 = combineBitmap(drawableToBitmap(binding.image.getDrawable()), bitmap);
            runOnUiThread(() -> binding.image.setImageBitmap(bitmap1));
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }

    /**
     * 将Drawable转化为Bitmap
     *
     * @param drawable
     * @return
     */
    public Bitmap drawableToBitmap(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        } else {
            if (drawable instanceof AdaptiveIconDrawable) {
                Drawable backgroundDr = ((AdaptiveIconDrawable) drawable).getBackground();
                Drawable foregroundDr = ((AdaptiveIconDrawable) drawable).getForeground();
                Drawable[] drr = new Drawable[2];
                drr[0] = backgroundDr;
                drr[1] = foregroundDr;

                LayerDrawable layerDrawable = new LayerDrawable(drr);
                // 设置 LayerDrawable 的底色
                layerDrawable.setColorFilter(new BlendModeColorFilter(Color.WHITE, BlendMode.SRC_ATOP));

                int width = layerDrawable.getIntrinsicWidth();
                int height = layerDrawable.getIntrinsicHeight();
                Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                // 在 Bitmap 上绘制一个矩形，以设置底色
                Canvas canvas = new Canvas(bitmap);
                layerDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
                layerDrawable.draw(canvas);
                return bitmap;
            }
        }
        return null;
    }


    /**
     * @param background 背景图
     * @param foreground 前景图
     * @return
     */
    public Bitmap combineBitmap(Bitmap background, Bitmap foreground) {
        if (background == null) {
            return null;
        }
        binding.image.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        int bgHeight = binding.image.getMeasuredHeight();
        int bgWidth = binding.image.getMeasuredWidth();
        Bitmap newmap = Bitmap.createBitmap(bgWidth, bgHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(newmap);
        canvas.drawBitmap(background, 0, 0, null);
        canvas.drawBitmap(foreground, this.left, this.top, null);//设置二维码所在的位置 这个可以写死
        canvas.save();
        canvas.restore();
        return newmap;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.wx_h) {
            String file = saveBitmapToGallery(this, bitmap1);
            if (file == null) {
                return;
            }
            shareWechatFriend(this, "成人达已的事业\n你付出的每一滴汗水都是会得到回报\n矿石盛典等你来到:https://www.example.com", new File(file));
        } else if (v.getId() == R.id.wx_p) {
            String file = saveBitmapToGallery(this, bitmap1);
            if (file == null) {
                return;
            }
            shareToTimeLine(this, "成人达已的事业\n你付出的每一滴汗水都是会得到回报\n矿石盛典等你来到:https://www.example.com", new File(file));
        } else if (v.getId() == R.id.wx_d) {
            saveBitmapToGallery(this, bitmap1);
            Toast.makeText(this, "保存成功", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 直接分享图片到微信好友
     *
     * @param context
     * @param picFile
     */
    public void shareWechatFriend(Context context, String content, File picFile) {
        Intent intent = new Intent();
        ComponentName cop = new ComponentName("com.tencent.mm", "com.tencent.mm.ui.tools.ShareImgUI");
        intent.setComponent(cop);
        intent.setAction(Intent.ACTION_SEND);
        intent.setType("image/*");
        Uri uri = FileProvider.getUriForFile(context, "com.mining.mining.fileprovider", picFile);
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        intent.putExtra("Kdescription", !TextUtils.isEmpty(content) ? content : "");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(Intent.createChooser(intent, "Share"));
    }

    /**
     * 直接分享图片到微信朋友圈好友
     *
     * @param context
     * @param picFile
     */
    public void shareToTimeLine(Context context, String content, File picFile) {
        Intent intent = new Intent();
        ComponentName cop = new ComponentName("com.tencent.mm", "com.tencent.mm.ui.tools.ShareImgUI");
        intent.setComponent(cop);
        intent.setAction(Intent.ACTION_SEND_MULTIPLE);
        intent.setType("image/*");
        Uri uri = FileProvider.getUriForFile(context, "com.mining.mining.fileprovider", picFile);
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        intent.putExtra("Kdescription", !TextUtils.isEmpty(content) ? content : "");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(Intent.createChooser(intent, "Share"));
    }

    public String saveBitmapToGallery(Context context, Bitmap bitmap) {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, getString(R.string.app_name));
        values.put(MediaStore.Images.Media.DESCRIPTION, "My Image Description");
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");

        Uri externalUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        Uri internalUri = MediaStore.Images.Media.INTERNAL_CONTENT_URI;

        ContentResolver contentResolver = context.getContentResolver();
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Images.Media.DISPLAY_NAME, "My Image");
        contentValues.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        contentValues.put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis());
        contentValues.put(MediaStore.Images.Media.DATE_MODIFIED, System.currentTimeMillis());

        Uri insertUri = contentResolver.insert(externalUri, contentValues);
        if (insertUri == null) {
            return null;
        }
        try (OutputStream outputStream = contentResolver.openOutputStream(insertUri)) {
            if (outputStream != null) {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        MediaScannerConnection.scanFile(context, new String[]{insertUri.toString()}, null, null);
        Cursor cursor = contentResolver.query(insertUri, null, null, null, null);
        if (cursor == null) {
            return null;
        }
        cursor.moveToFirst();
        int data = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
        if (data == -1) {
            return null;
        }
        String filePath = cursor.getString(data);
        cursor.close();

        return filePath;
    }

}
