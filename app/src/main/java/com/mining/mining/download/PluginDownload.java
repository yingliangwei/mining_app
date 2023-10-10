package com.mining.mining.download;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okio.Buffer;
import okio.Okio;
import okio.Sink;
import okio.Source;

public class PluginDownload  implements Callback {
    private long max;
    private final String url;
    private final ProgressListener progressListener;
    private final File pluginFile;
    private long downloadedBytes;

    public PluginDownload(Context context, String url, ProgressListener progressListener) {
        this.progressListener = progressListener;
        this.url = url;
        pluginFile = new File(context.getDir("apk", Context.MODE_PRIVATE), System.currentTimeMillis() + ".zip");
    }

    public PluginDownload(String url, File file, ProgressListener progressListener) {
        this.progressListener = progressListener;
        this.url = url;
        this.pluginFile = file;
    }

    public void run() {
        OkHttpClient client = new OkHttpClient().newBuilder()
                .retryOnConnectionFailure(true)
                .connectTimeout(50L, TimeUnit.SECONDS)
                .readTimeout(60L, TimeUnit.SECONDS)
                .build();
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Range", "bytes=" + downloadedBytes + "-")
                .build();
        client.newCall(request).enqueue(this);
    }

    @Override
    public void onFailure(@NonNull Call call, @NonNull IOException e) {
        progressListener.error(e);
    }

    @Override
    public void onResponse(Call call, Response response) {
        try {
            long contentLength = response.body().contentLength();
            Sink sink = Okio.sink(pluginFile);
            Source source = response.body().source();
            Buffer buffer = new Buffer();
            long bytesRead;
            while ((bytesRead = source.read(buffer, 8192)) != -1) {
                sink.write(buffer, bytesRead);
                downloadedBytes += bytesRead;
                progressListener.update(downloadedBytes,contentLength,false);
            }
            // 关闭Sink和Source
            sink.close();
            source.close();
            progressListener.onSuccess(pluginFile);
        } catch (Exception e) {
            progressListener.error(e);
            e.fillInStackTrace();
        }
    }

    public interface ProgressListener {

        void update(long downloadedBytes, long contentLength, boolean b);

        void onSuccess(File file);

        void error(Exception e);
    }
}
