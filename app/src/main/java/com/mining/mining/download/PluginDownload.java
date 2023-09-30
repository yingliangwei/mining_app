package com.mining.mining.download;

import android.content.Context;

import com.mining.mining.download.interFace.OnDownload;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class PluginDownload extends Thread {
    private long max;
    private final String url;
    private final OnDownload onDownload;
    private final File pluginFile;

    public PluginDownload(Context context, String url, OnDownload onDownload) {
        this.onDownload = onDownload;
        this.url = url;
        pluginFile = new File(context.getDir("apk", Context.MODE_PRIVATE), System.currentTimeMillis() + ".zip");
    }

    public PluginDownload(String url, File file, OnDownload onDownload) {
        this.onDownload = onDownload;
        this.url = url;
        this.pluginFile = file;
    }

    @Override
    public void run() {
        try {
            URL url = new URL(this.url);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();

            max = connection.getContentLength();
            if (HttpURLConnection.HTTP_OK == connection.getResponseCode()) {
                read(connection.getInputStream(), pluginFile);
            }
        } catch (Exception e) {
            onDownload.error(e);
            e.fillInStackTrace();
        }
    }

    /**
     * 文件写入
     *
     * @param inputStream 数据流
     * @param file        储存地址
     * @throws IOException
     */
    private void read(InputStream inputStream, File file) throws IOException {
        int size = 0;
        FileOutputStream out = new FileOutputStream(file);
        byte[] bt = new byte[1024];
        int d;
        while ((d = inputStream.read(bt)) > 0) {
            size += d;
            int i = (int) (size * 100 / max);
            onDownload.onProgressChange(i, max);
            out.write(bt, 0, d);
        }
        onDownload.onSuccess(file);
        inputStream.close();
        out.close();
    }

}
