
package com.mining.mining.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.mining.mining.databinding.ActivityPreloadBinding;
import com.mining.mining.download.PluginDownload;
import com.mining.mining.download.interFace.OnDownload;
import com.mining.util.Handler;
import com.mining.util.OnHandler;
import com.mining.util.StatusBarUtil;
import com.plugin.lib.PluginManager;
import com.plugin.lib.Storage;
import com.plugin.lib.activity.BaseActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

/**
 * 该界面只用于加载网络插件
 */
public class PreloadActivity extends AppCompatActivity implements OnDownload, OnHandler {
    private String json;
    private final Handler handler = new Handler(Looper.getMainLooper(), this);
    private ActivityPreloadBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPreloadBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        StatusBarUtil.setImmersiveStatusBar(this, true);
        init();
    }

    private void init() {
        json = getIntent().getStringExtra("json");
        if (json == null) {
            finish();
        }
        try {
            initData();
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    public void initData() throws JSONException {
        JSONObject jsonObject = new JSONObject(json);
        String download_image = jsonObject.getString("image");
        String download = jsonObject.getString("download");
        String title = jsonObject.getString("name");
        String version = jsonObject.getString("version");

        File file = new File(getDir("apk", Context.MODE_PRIVATE), String.format("%s|%s", title, version));
        if (file.exists()) {
            int versionInt = Integer.parseInt(version) - 1;
            File file1 = new File(getDir("apk", Context.MODE_PRIVATE), String.format("%s|%s", title, versionInt));
            if (file1.exists()) {
                boolean is = file1.delete();
            }
            loadPlugin(file);
        } else {
            PluginDownload download1 = new PluginDownload(download, file, this);
            download1.start();
        }

        Glide.with(this).load(download_image).into(binding.image);
        binding.name.setText(title);
    }

    @Override
    public void onProgressChange(int current, long max) {
        handler.sendMessage(0, String.valueOf(current));
    }

    @Override
    public void handleMessage(int w, String str) {
        if (w == -1) {
            Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
        } else if (w == 0) {
            int current = Integer.parseInt(str);
            if (current == 100) {
                binding.progress.setVisibility(View.GONE);
            } else {
                binding.progress.setVisibility(View.VISIBLE);
            }
            binding.progress.setProgress(current);
        }
    }

    private void loadPlugin(File file) {
        //这里要记录信息
        PluginManager pluginManager = new PluginManager(this, file);
        try {
            pluginManager.load();
            com.plugin.lib.entity.PluginEntity entity = pluginManager.getPluginEntity();
            int id = Storage.getInstance().getEntitiesSize();
            Storage.getInstance().add(id, entity);
            Storage.getInstance().id = id;

            String main = entity.getMain();
            if (main == null) {
                handler.sendMessage(-1, "加载游戏异常");
                finish();
                return;
            }

            Intent intent = new Intent(this, BaseActivity.class);
            intent.putExtra("className", main);
            intent.putExtra("id", id);
            startActivity(intent);

            finish();
        } catch (Exception e) {
            e.printStackTrace();
            handler.sendMessage(-1, "加载游戏异常:" + e.getMessage());
            finish();
        }
    }

    @Override
    public void onSuccess(File file) {
        loadPlugin(file);
    }


    @Override
    public void error(Exception e) {
        handler.sendMessage(1, "下载插件失败!");
        finish();
    }
}
