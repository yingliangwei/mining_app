
package com.mining.mining.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.fastjson2.JSONObject;
import com.bumptech.glide.Glide;
import com.mining.mining.databinding.ActivityPreloadBinding;
import com.mining.mining.download.PluginDownload;
import com.mining.mining.util.SharedUtil;
import com.mining.util.Handler;
import com.mining.util.OnHandler;
import com.mining.util.StatusBarUtil;
import com.plugin.lib.PluginManager;
import com.plugin.lib.Storage;
import com.plugin.lib.activity.BaseActivity;
import com.xframe.network.OnData;
import com.xframe.network.SocketManage;

import java.io.File;

/**
 * 该界面只用于加载网络插件
 */
public class PreloadActivity extends AppCompatActivity implements PluginDownload.ProgressListener, OnHandler, OnData {
    private final Handler handler = new Handler(Looper.getMainLooper(), this);
    private ActivityPreloadBinding binding;
    private String id;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPreloadBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        StatusBarUtil.setImmersiveStatusBar(this, true);
        init();
    }

    private void init() {
        String json = getIntent().getStringExtra("json");
        if (json == null) {
            finish();
        }
        initData(json);
        SocketManage.init(this);
    }

    public void initData(String json) {
        JSONObject jsonObject = JSONObject.parseObject(json);
        String download_image = jsonObject.getString("image");
        String download = jsonObject.getString("download");
        String title = jsonObject.getString("name");
        String version = jsonObject.getString("version");
        String bg_image = jsonObject.getString("bg_image");
        id = jsonObject.getString("id");
        File file = new File(getDir("apk", Context.MODE_PRIVATE), String.format("%s|%s.zip", title, version));
        if (file.exists()) {
            loadPlugin(file);
        } else {
            delete(getDir("apk", Context.MODE_PRIVATE), title);
            PluginDownload download1 = new PluginDownload(download, file, this);
            download1.run();
        }
        Glide.with(this).load(download_image).into(binding.image);
        Glide.with(this).load(bg_image).into(binding.bgImage);
        binding.name.setText(title);
        binding.name.setText(title);
    }

    /**
     * 删除类似文件名的文件
     *
     * @param _file 目录
     * @param name  文件名
     */
    private void delete(File _file, String name) {
        File[] files = _file.listFiles();
        if (files == null) {
            return;
        }
        for (File file : files) {
            if (file.getName().startsWith(name)) {
                boolean is = file.delete();
            }
        }
    }


    @Override
    public void connect(SocketManage socketManage) {
        SharedUtil sharedUtil = new SharedUtil(this);
        JSONObject jsonObject = sharedUtil.getLogin(12, 9);
        jsonObject.put("plugin_id", id);
        socketManage.print(jsonObject.toString());
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
            file.delete();
            e.printStackTrace();
            handler.sendMessage(-1, "加载游戏异常:" + e.getMessage());
            finish();
        }
    }

    @Override
    public void update(long downloadedBytes, long contentLength, boolean b) {
        int progress = (int) (downloadedBytes * 1.0f / contentLength * 100);
        handler.sendMessage(0, String.valueOf(progress));
    }

    @Override
    public void onSuccess(File file) {
        loadPlugin(file);
    }

    @Override
    public void error(Exception e) {
        handler.sendMessage(-1, "下载失败");
        finish();
    }

}
