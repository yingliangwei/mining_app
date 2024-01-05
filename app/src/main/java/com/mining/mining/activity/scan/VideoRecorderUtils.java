package com.mining.mining.activity.scan;

import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CaptureRequest;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.util.Size;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.annotation.RequiresApi;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class VideoRecorderUtils {
    private MediaRecorder mediaRecorder;
    private SurfaceHolder.Callback callback;
    private SurfaceView surfaceView;
    private CameraDevice mCameraDevice;
    private int height;
    private int width;
    List<Surface> surfaces = new ArrayList<>();
    public static Size WH_720X480 = new Size(720, 480);
    private CaptureRequest.Builder mPreviewBuilder;
    private CaptureRequest mCaptureRequest;
    private CameraCaptureSession mPreviewSession;
    public void create(SurfaceView surfaceView, CameraDevice cameraDevice, Size size) {
        this.surfaceView = surfaceView;
        mCameraDevice = cameraDevice;
        //创建录制的session会话中的请求
        try {
            mPreviewBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_RECORD);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
        height = size.getHeight();
        width = size.getWidth();
        mediaRecorder = new MediaRecorder();
    }
    public void stopRecord() {
        mediaRecorder.release();
        mediaRecorder = null;
        mediaRecorder = new MediaRecorder();
        surfaces.clear();
    }
    public void stop() {
        if (mediaRecorder != null) {
            mediaRecorder.release();
        }
    }
    public void destroy() {
        if (mediaRecorder != null) {
            mediaRecorder.release();
            mediaRecorder = null;
        }
    }
    /**
     * @param mainActivity
     */
    @RequiresApi(api = Build.VERSION_CODES.R)
    public void startRecord(ScanActivity mainActivity, Handler handler) {

        mediaRecorder.setVideoSource(MediaRecorder.VideoSource.SURFACE);
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);


        mediaRecorder.setVideoSize(width, height);
        mediaRecorder.setVideoFrameRate(24);
        mediaRecorder.setVideoEncodingBitRate(700 * 1024);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
        mediaRecorder.setOrientationHint(90);


        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US);
        String fname = "video_" + sdf.format(new Date()) + ".mp4";
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath() + File.separator + fname);
        Log.d("xxxxxx", "startRecord: "+file.getAbsolutePath());
        mediaRecorder.setOutputFile(file);
        try {
            mediaRecorder.prepare();
            mediaRecorder.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        review(handler);
    }
    public void review(Handler handler) {
        Surface previewSurface = surfaceView.getHolder().getSurface();
        surfaces.add(previewSurface);
        mPreviewBuilder.addTarget(previewSurface);
        Surface recorderSurface = mediaRecorder.getSurface();
        surfaces.add(recorderSurface);
        mPreviewBuilder.addTarget(recorderSurface);
        try {
            mCameraDevice.createCaptureSession(surfaces, new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(CameraCaptureSession session) {
                    try {
                        //创建捕获请求
                        mCaptureRequest = mPreviewBuilder.build();
                        mPreviewSession = session;
                        //设置反复捕获数据的请求，这样预览界面就会一直有数据显示
                        mPreviewSession.setRepeatingRequest(mCaptureRequest, null, handler);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                @Override
                public void onConfigureFailed(CameraCaptureSession session) {
                }
            }, handler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }

    }
    //清除预览Session
    private void closePreviewSession() {
        if (mPreviewSession != null) {
            mPreviewSession.close();
            mPreviewSession = null;
        }
    }
}
