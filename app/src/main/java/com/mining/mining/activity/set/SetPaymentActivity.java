package com.mining.mining.activity.set;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.fastjson2.JSONObject;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.mining.mining.R;
import com.mining.mining.databinding.ActivityPaymentCodeBinding;
import com.mining.mining.util.SharedUtil;
import com.mining.util.Handler;
import com.mining.util.OnHandler;
import com.mining.util.StatusBarUtil;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.Region;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.util.Auth;
import com.xframe.network.OnData;
import com.xframe.network.SocketManage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


public class SetPaymentActivity extends AppCompatActivity implements OnData, View.OnClickListener, ActivityResultCallback<ActivityResult> {
    private ActivityPaymentCodeBinding binding;
    private String zfb_image, wx_image;
    private String ak, sk, image;
    private int i;//0支付宝 1微信
    private final ActivityResultLauncher<Intent> resultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), this);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtil.setImmersive(this, true);
        binding = ActivityPaymentCodeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initToolbar();
        initView();
        SocketManage.init(this);
    }

    private String getRealPathFromUri(Context context, Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);
        if (cursor == null) return null;
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String path = cursor.getString(column_index);
        cursor.close();
        return path;
    }

    private void initView() {
        binding.zfbSava.setOnClickListener(this);
        binding.wxSava.setOnClickListener(this);
        binding.bankSava.setOnClickListener(this);
        binding.wxImage.setOnClickListener(this);
        binding.zfbImage.setOnClickListener(this);
    }

    private void initToolbar() {
        binding.toolbar.setNavigationOnClickListener(v -> finish());
    }

    @Override
    public void connect(SocketManage socketManage) {
        SharedUtil sharedUtil = new SharedUtil(this);
        JSONObject jsonObject = sharedUtil.getLogin(7, 7);
        socketManage.print(jsonObject.toString());
    }

    @Override
    public void handle(String ds) {
        JSONObject jsonObject = JSONObject.parseObject(ds);
        int code = jsonObject.getInteger("code");
        if (code != 200) {
            String msg = jsonObject.getString("msg");
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
            return;
        }
        JSONObject zfb = jsonObject.getJSONObject("zfb");
        JSONObject wx = jsonObject.getJSONObject("wx");
        JSONObject bank = jsonObject.getJSONObject("bank");
        JSONObject qiniu = jsonObject.getJSONObject("qiniu");
        initQiniu(qiniu);
        initZfb(zfb);
        initWx(wx);
        initBank(bank);
    }

    private void initQiniu(JSONObject qiniu) {
        if (qiniu == null) {
            return;
        }
        ak = qiniu.getString("ak");
        sk = qiniu.getString("sk");
        image = qiniu.getString("url");
    }

    private void initZfb(JSONObject jsonObject) {
        if (jsonObject == null) {
            return;
        }
        String code = jsonObject.getString("code");
        String image = jsonObject.getString("image");
        this.zfb_image = image;
        binding.zfbCode.setText(code);
        Glide.with(this).load(image).into(binding.zfbImage);
    }

    private void initWx(JSONObject jsonObject) {
        if (jsonObject == null) {
            return;
        }
        String code = jsonObject.getString("code");
        String image = jsonObject.getString("image");
        this.wx_image = image;
        binding.wxCode.setText(code);
        Glide.with(this).load(image).into(binding.wxImage);
    }

    private void initBank(JSONObject jsonObject) {
        if (jsonObject == null) {
            return;
        }
        String code = jsonObject.getString("code");
        String text = jsonObject.getString("text");
        binding.bankCode.setText(code);
        binding.bankText.setText(text);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.zfb_sava) {
            String code = binding.zfbCode.getText().toString();
            if (code.length() == 0 || zfb_image == null) {
                Toast.makeText(this, "检查内容是否填写,二维码是否上传", Toast.LENGTH_SHORT).show();
                return;
            }
            SharedUtil sharedUtil = new SharedUtil(this);
            JSONObject jsonObject = sharedUtil.getLogin(7, 6);
            jsonObject.put("name", code);
            jsonObject.put("image", zfb_image);
            jsonObject.put("code_type", 1);
            setPayment setPayment = new setPayment(this, jsonObject);
            SocketManage.init(setPayment);
        } else if (v.getId() == R.id.wx_sava) {
            String code = binding.wxCode.getText().toString();
            if (code.length() == 0 || wx_image == null) {
                Toast.makeText(this, "检查内容是否填写,二维码是否上传", Toast.LENGTH_SHORT).show();
                return;
            }
            SharedUtil sharedUtil = new SharedUtil(this);
            JSONObject jsonObject = sharedUtil.getLogin(7, 6);
            jsonObject.put("name", code);
            jsonObject.put("image", zfb_image);
            jsonObject.put("code_type", 2);
            setPayment setPayment = new setPayment(this, jsonObject);
            SocketManage.init(setPayment);
        } else if (v.getId() == R.id.bank_sava) {
            String code = binding.bankCode.getText().toString();
            String text = binding.bankText.getText().toString();
            if (code.length() == 0 || text.length() == 0) {
                Toast.makeText(this, "检查内容是否为空", Toast.LENGTH_SHORT).show();
                return;
            }
            SharedUtil sharedUtil = new SharedUtil(this);
            JSONObject jsonObject = sharedUtil.getLogin(7, 6);
            jsonObject.put("name", code);
            jsonObject.put("text", text);
            jsonObject.put("code_type", 3);
            setPayment setPayment = new setPayment(this, jsonObject);
            SocketManage.init(setPayment);
        } else if (v.getId() == R.id.wx_image) {
            if (ak == null || sk == null) {
                Toast.makeText(this, "密匙错误", Toast.LENGTH_SHORT).show();
                return;
            }
            i = 1;
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
            resultLauncher.launch(intent);
        } else if (v.getId() == R.id.zfb_image) {
            if (ak == null || sk == null) {
                Toast.makeText(this, "密匙错误", Toast.LENGTH_SHORT).show();
                return;
            }
            i = 0;
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
            resultLauncher.launch(intent);
        }
    }

    @Override
    public void onActivityResult(ActivityResult result) {
        if (result.getResultCode() == RESULT_OK && result.getData() != null) {
            Uri selectedImageUri = result.getData().getData();
            String image = getRealPathFromUri(this, selectedImageUri);
            if (image == null) {
                return;
            }
            new Thread(new UploadImage(this, image)).start();
        }
    }

    private class UploadImage implements Runnable, OnHandler {
        private final String image;
        private final Context context;
        private final Handler handler = new Handler(Looper.myLooper(), this);

        @Override
        public void handleMessage(int w, String str) {
            if (w == 0) {
                DefaultPutRet putRet = new Gson().fromJson(str, DefaultPutRet.class);
                if (i == 0) {
                    zfb_image = SetPaymentActivity.this.image + putRet.key;
                    System.out.println(zfb_image);
                    Glide.with(context).load(zfb_image).into(binding.zfbImage);
                    Toast.makeText(context, "支付宝收款码上传成功", Toast.LENGTH_SHORT).show();
                } else {
                    wx_image = SetPaymentActivity.this.image + putRet.key;
                    Glide.with(context).load(zfb_image).into(binding.wxImage);
                    Toast.makeText(context, "微信收款码上传成功", Toast.LENGTH_SHORT).show();
                }
            }
        }

        public UploadImage(Context context, String image) {
            this.image = image;
            this.context = context;
        }

        @Override
        public void run() {
            byte[] data = convertFileToBytes(image);
            String accessKey = ak;
            String secretKey = sk;
            String bucket = "a10";
            Auth auth = Auth.create(accessKey, secretKey);
            String upToken = auth.uploadToken(bucket);
            Configuration cfg = new Configuration(Region.region2());
            UploadManager uploadManager = new UploadManager(cfg);
            try {
                Response response = uploadManager.put(data, null, upToken);
                handler.sendMessage(0, response.bodyString());
            } catch (QiniuException e) {
                e.printStackTrace();
            }
        }

        public static byte[] convertFileToBytes(String filePath) {
            byte[] fileBytes = null;
            try {
                Path path = Paths.get(filePath);
                fileBytes = Files.readAllBytes(path);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return fileBytes;
        }

    }

    private record setPayment(Context context, JSONObject jsonObject) implements OnData {

        @Override
        public void connect(SocketManage socketManage) {
            socketManage.print(jsonObject.toString());
        }

        @Override
        public void handle(String ds) {
            JSONObject jsonObject1 = JSONObject.parseObject(ds);
            String msg = jsonObject1.getString("msg");
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
        }
    }
}
