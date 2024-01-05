package com.mining.mining.activity.scan;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureFailure;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.OutputConfiguration;
import android.hardware.camera2.params.SessionConfiguration;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.SparseIntArray;
import android.view.MenuItem;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.alibaba.fastjson2.JSONObject;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.mining.mining.R;
import com.mining.mining.activity.TransferActivity;
import com.mining.mining.activity.WebActivity;
import com.mining.mining.databinding.ActivityScanBinding;
import com.mining.util.StatusBarUtil;

import java.io.File;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ScanActivity extends AppCompatActivity implements SurfaceHolder.Callback, Toolbar.OnMenuItemClickListener {
    private ActivityScanBinding binding;
    private SurfaceHolder holder;
    private Handler handler;
    private CameraManager cameraManager;
    private ImageReader imageReader;
    private CameraDevice cameraDevice;
    private CameraCaptureSession cameraCaptureSession;
    private CaptureRequest.Builder previewBuilder;
    private CaptureRequest.Builder captureBuilder;
    private boolean is;
    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();
    private final Timer timer = new Timer();
    private final TimerTask task = new TimerTask() {
        @Override
        public void run() {
            if (is) {
                return;
            }
            takePhoto();
        }
    };

    /**
     * 打开相机的回调，
     */
    private final CameraDevice.StateCallback stateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(@NonNull CameraDevice camera) {
            //打开相机后开启预览
            //打开相机后开启预览
            cameraDevice = camera;
            //打开照相机时初始化,videoRecord
            VideoRecorderUtils videoRecorderUtils = new VideoRecorderUtils();
            videoRecorderUtils.create(binding.surface, cameraDevice, VideoRecorderUtils.WH_720X480);
            startPreview();
        }

        @Override
        public void onDisconnected(@NonNull CameraDevice camera) {
            camera.close();
            cameraDevice = null;
        }

        @Override
        public void onError(@NonNull CameraDevice camera, int error) {
            camera.close();
            cameraDevice = null;
            finish();
        }
    };

    private void startPreview() {
        try {
            //构建预览请求
            previewBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            //设置预览输出的界面
            previewBuilder.addTarget(holder.getSurface());
            //创建相机的会话Session
            List<OutputConfiguration> outputConfigs = new ArrayList<>();
            outputConfigs.add(new OutputConfiguration(holder.getSurface()));
            outputConfigs.add(new OutputConfiguration(imageReader.getSurface()));
            Executor executor = Executors.newSingleThreadExecutor();
            SessionConfiguration config = new SessionConfiguration(SessionConfiguration.SESSION_REGULAR, outputConfigs, executor, sessionStateCallback);
            cameraDevice.createCaptureSession(config);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * session的回调
     */
    private final CameraCaptureSession.StateCallback sessionStateCallback = new CameraCaptureSession.StateCallback() {
        @Override
        public void onConfigured(@NonNull CameraCaptureSession session) {
            //会话已经建立，可以开始预览了
            cameraCaptureSession = session;
            //设置自动对焦
            previewBuilder.set(CaptureRequest.CONTROL_AF_MODE, CameraMetadata.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
            //发送预览请求
            try {
                cameraCaptureSession.setRepeatingRequest(previewBuilder.build(), null, handler);
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onConfigureFailed(@NonNull CameraCaptureSession session) {
            //关闭会话
            session.close();
            cameraCaptureSession = null;
            cameraDevice.close();
            cameraDevice = null;
        }
    };

    /**
     * 拍照
     */
    private void takePhoto() {
        if (cameraDevice == null) {
            return;
        }
        try {
            //创建拍照请求的Request
            captureBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
            //设置拍照的画面
            captureBuilder.addTarget(imageReader.getSurface());
            //自动对焦
            captureBuilder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
            //自动曝光
            captureBuilder.set(CaptureRequest.CONTROL_AE_MODE, CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH);
            //获取手机方向
            int rotation = getWindowManager().getDefaultDisplay().getRotation();
            ORIENTATIONS.append(Surface.ROTATION_0, 90);
            ORIENTATIONS.append(Surface.ROTATION_90, 0);
            ORIENTATIONS.append(Surface.ROTATION_180, 270);
            ORIENTATIONS.append(Surface.ROTATION_270, 180);
            //根据设备方向计算设置照片的方向
            captureBuilder.set(CaptureRequest.JPEG_ORIENTATION, ORIENTATIONS.get(rotation));
            //拍照
            cameraCaptureSession.capture(captureBuilder.build(), captureCallback, handler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private final CameraCaptureSession.CaptureCallback captureCallback = new CameraCaptureSession.CaptureCallback() {
        @Override
        public void onCaptureProgressed(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull CaptureResult partialResult) {
            super.onCaptureProgressed(session, request, partialResult);
        }

        @Override
        public void onCaptureCompleted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull TotalCaptureResult result) {
            super.onCaptureCompleted(session, request, result);
            try {
                //自动对焦
                captureBuilder.set(CaptureRequest.CONTROL_AF_TRIGGER, CaptureRequest.CONTROL_AF_TRIGGER_CANCEL);
                //重新打开预览
                cameraCaptureSession.setRepeatingRequest(previewBuilder.build(), null, handler);
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onCaptureFailed(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull CaptureFailure failure) {
            super.onCaptureFailed(session, request, failure);
            cameraCaptureSession.close();
            cameraCaptureSession = null;
            cameraDevice.close();
            cameraDevice = null;
        }
    };


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtil.setImmersive(this, true);
        binding = ActivityScanBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initToolbar();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 1);
        } else {
            initSurface();
            // 每隔1秒执行一次任务
            long delay = 0;
            long period = 2000;
            timer.scheduleAtFixedRate(task, delay, period);
        }
    }

    // 处理权限请求结果
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                initSurface();
                // 每隔1秒执行一次任务
                long delay = 0;
                long period = 2000;
                timer.scheduleAtFixedRate(task, delay, period);
            } else {
                // 用户拒绝了相机权限
                Toast.makeText(this, "请授权相机权限", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        timer.cancel();
    }

    private void initSurface() {
        holder = binding.surface.getHolder();
        holder.addCallback(this);
        initCameraManager();
    }

    private void initToolbar() {
        binding.toolbar.setNavigationOnClickListener(v -> finish());
        binding.toolbar.setOnMenuItemClickListener(this);
    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder holder) {
        //打开相机
        openCamera();
        int height = binding.surface.getHeight();
        int width = binding.surface.getWidth();
        if (height > width) {
            float justH = width * 4.f / 3;
            //设置View在水平方向的缩放比例,保证宽高比为3:4
            binding.surface.setScaleX(height / justH);
        } else {
            float justW = height * 4.f / 3;
            binding.surface.setScaleY(width / justW);
        }
    }

    private void openCamera() {
        try {
            //获取摄像头属性描述
            // 要打开的摄像头ID
            int mCameraId = CameraCharacteristics.LENS_FACING_FRONT;
            CameraCharacteristics cameraCharacteristics = cameraManager.getCameraCharacteristics(String.valueOf(mCameraId));
            //获取摄像头支持的配置属性
            StreamConfigurationMap map = cameraCharacteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
            Integer level = cameraCharacteristics.get(CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL);
            initImageReader();
            //打开相机,先获取权限
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            //打开摄像头
            cameraManager.openCamera(String.valueOf(mCameraId), stateCallback, handler);
        } catch (CameraAccessException exception) {
            exception.printStackTrace();
        }
    }

    private void initImageReader() {
        imageReader = ImageReader.newInstance(1920, 1080, ImageFormat.JPEG, 2);
        imageReader.setOnImageAvailableListener(reader -> {
            Image img = reader.acquireNextImage();
            if (img == null || img.getPlanes()[0].getBuffer() == null) {
                // 图像已关闭，或者尚未准备好
                return;
            }
            ByteBuffer buffer = img.getPlanes()[0].getBuffer();
            byte[] data = new byte[buffer.remaining()];
            buffer.get(data);
            Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
            scan(bitmap);
            img.close();
        }, handler);
    }

    private void handleData(String text) {
        startMp3();
        if (text.startsWith("http")) {
            Intent intent = new Intent(this, WebActivity.class);
            intent.putExtra("url", text);
            startActivity(intent);
            finish();
        }
        if (text.startsWith("{") || text.startsWith("[")) {
            JSONObject jsonObject = JSONObject.parseObject(text);
            if (jsonObject == null) {
                return;
            }
            Intent intent = new Intent(this, TransferActivity.class);
            String id = jsonObject.getString("id");
            String money = jsonObject.getString("money");
            intent.putExtra("id", id);
            intent.putExtra("money", money);
            startActivity(intent);
        } else {
            Intent intent = new Intent(this, ScanTextActivity.class);
            intent.putExtra("text", text);
            startActivity(intent);
        }
        finish();
    }


    private void scan(Bitmap bitmap) {
        bitmap = getSmallerBitmap(bitmap, 160000);
        int[] pixels = new int[bitmap.getWidth() * bitmap.getHeight()];
        bitmap.getPixels(pixels, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
        LuminanceSource source = new RGBLuminanceSource(bitmap.getWidth(), bitmap.getHeight(), pixels);
        BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(source));
        MultiFormatReader multiFormatReader = new MultiFormatReader();
        Map<DecodeHintType, String> hints = new HashMap<>();
        hints.put(DecodeHintType.ALSO_INVERTED, "utf-8");
        try {
            Result result = multiFormatReader.decode(binaryBitmap, hints);
            String content = result.getText();
            handleData(content);
            System.out.println(content);
        } catch (Exception e) {
            System.out.println("识别失败");
            // 识别失败
            e.printStackTrace();
        }
    }

    private void startMp3() {
        MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.scan_code);
        mediaPlayer.start();
    }

    public Bitmap getSmallerBitmap(Bitmap bitmap, double tarSize) {
        double size = bitmap.getWidth() * bitmap.getHeight() / tarSize;  // 160000  // 2000000
        if (size <= 1) {
            return bitmap; // 如果小于目标尺寸，则直接返回原图
        } else {
            float scale = (float) (1.0 / Math.sqrt(size));
            Matrix matrix = new Matrix();
            matrix.postScale(scale, scale);
            return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        }
    }

    private void initCameraManager() {
        cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        HandlerThread handlerThread = new HandlerThread("Camera2");
        handlerThread.start();
        handler = new Handler(handlerThread.getLooper());
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder holder) {

    }

    ActivityResultLauncher<Intent> resultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    is = false;
                    Uri selectedImageUri = result.getData().getData();
                    String image = getRealPathFromUri(this, selectedImageUri);
                    if (image == null) {
                        return;
                    }
                    File file = new File(image);
                    if (!file.exists()) {
                        return;
                    }
                    Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                    if (bitmap == null) {
                        Toast.makeText(this, "没有储存权限", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    scan(bitmap);
                }
            });

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

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        resultLauncher.launch(intent);
        is = true;
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        if (item.getItemId() == R.id.select) {
            openGallery();
        }
        return false;
    }

}
