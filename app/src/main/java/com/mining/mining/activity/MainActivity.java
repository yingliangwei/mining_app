package com.mining.mining.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.alibaba.fastjson2.JSONObject;
import com.google.android.material.navigation.NavigationView;
import com.mining.mining.R;
import com.mining.mining.activity.task.ActivityTask;
import com.mining.mining.adapter.PagerAdapter;
import com.mining.mining.adapter.RecyclerAdapter;
import com.mining.mining.databinding.ActivityMainBinding;
import com.mining.mining.download.PluginDownload;
import com.mining.mining.pager.home.HomePager;
import com.mining.mining.pager.mining.MiningPager;
import com.mining.mining.pager.my.MyPager;
import com.mining.mining.pager.task.TaskPager;
import com.mining.mining.util.InstallUtil;
import com.xframe.network.OnData;
import com.xframe.network.SocketManage;
import com.xframe.widget.NumberProgressBar;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, OnData, PluginDownload.ProgressListener {
    private ActivityMainBinding binding;
    private final List<RecyclerAdapter> recyclerAdapters = new ArrayList<>();
    private SharedPreferences sharedPreferences;
    private NumberProgressBar progressBar;
    private PluginDownload pluginDownload;
    private String download;
    private String version_code = "1";
    private int progress;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility( View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);//实现状态栏图标和文字颜色为暗色
        sharedPreferences = getSharedPreferences(getPackageName(), Context.MODE_PRIVATE);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        ActivityTask.getInstance().setBinding(binding);
        setContentView(binding.getRoot());
        initPager();
        initNavigation();
        SocketManage.init(this);
    }

    @Override
    public void connect(SocketManage socketManage) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("type", 15);
        jsonObject.put("code", 1);
        socketManage.print(jsonObject.toString());
    }

    private void update(String str) {
        JSONObject jsonObject = JSONObject.parseObject(str);
        download = jsonObject.getString("download");
        String log = jsonObject.getString("log");
        String force = jsonObject.getString("force");
        version_code = jsonObject.getString("version_code");
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("更新温馨提示");
        builder.setMessage(log);
        progressBar = new NumberProgressBar(this);
        builder.setView(progress);
        File downloadDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsoluteFile();
        File downloadFile = new File(downloadDirectory, System.currentTimeMillis() + ".apk");
        pluginDownload = new PluginDownload(download, downloadFile, this);
        builder.setPositiveButton("更新", (dialog, which) -> pluginDownload.run());
        builder.setNegativeButton("浏览器", (dialog, which) -> {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(download));
            startActivity(intent);
            dialog.cancel();
        });
        if (force.equals("0")) {
            builder.setNeutralButton("取消", (dialog, which) -> {
                sharedPreferences.edit().putInt("update", 1).apply();
                sharedPreferences.edit().putString("version_code", version_code).apply();
                dialog.cancel();
            });
        } else {
            builder.setCancelable(false);
        }
        builder.show();
    }


    @Override
    public void handle(String ds) {
        JSONObject jsonObject = JSONObject.parseObject(ds);
        String version_code = jsonObject.getString("version_code");
        String force = jsonObject.getString("force");
        int update = sharedPreferences.getInt("update", 0);
        String _version_code = sharedPreferences.getString("version_code", "");
        if (force.equals("0") && _version_code.equals(version_code)) {
            if (update == 1) {
                return;
            }
        }
        int version = Integer.parseInt(version_code);
        if (version > getVersionCode()) {
            update(jsonObject.toString());
        }
    }

    private long getVersionCode() {
        PackageManager packageManager = getPackageManager();
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(getPackageName(), 0);
            return packageInfo.getLongVersionCode();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private void initNavigation() {
        binding.navigation.setOnItemSelectedListener(this::onNavigationItemSelected);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        for (RecyclerAdapter recyclerAdapter : recyclerAdapters) {
            recyclerAdapter.onDestroy();
        }
    }

    private void initPager() {
        recyclerAdapters.add(new HomePager(this));
        recyclerAdapters.add(new MiningPager(this));
        recyclerAdapters.add(new TaskPager(this));
        recyclerAdapters.add(new MyPager(this));
        PagerAdapter pagerAdapter = new PagerAdapter(recyclerAdapters);
        binding.pager.setAdapter(pagerAdapter);
        binding.pager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                recyclerAdapters.get(position).start();
                recyclerAdapters.get(position).StatusBar(MainActivity.this);
                binding.navigation.getMenu().getItem(position).setChecked(true);
                Navigation(position);
            }
        });
        binding.pager.setOffscreenPageLimit(recyclerAdapters.size());
    }

    private void Navigation(int position) {
        if (position == 1) {
            binding.navigation.setBackground(new ColorDrawable(ContextCompat.getColor(this, R.color.b32)));
        } else {
            binding.navigation.setBackground(new ColorDrawable(Color.WHITE));
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        recyclerAdapters.get(item.getOrder()).StatusBar(MainActivity.this);
        binding.pager.setCurrentItem(item.getOrder());
        Navigation(item.getOrder());
        return true;
    }

    @Override
    public void update(long downloadedBytes, long contentLength, boolean b) {
        progress = (int) (downloadedBytes * 1.0f / contentLength * 100);
        runOnUiThread(() -> {
            if (progressBar != null) {
                progressBar.setVisibility(View.VISIBLE);
                progressBar.setProgress(progress);
            }
        });
    }

    @Override
    public void onSuccess(File file) {
        InstallUtil.installApk(this, file.getAbsolutePath());
    }
}
