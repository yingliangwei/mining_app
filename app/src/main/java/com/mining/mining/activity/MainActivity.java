package com.mining.mining.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.navigation.NavigationView;
import com.mining.mining.adapter.PagerAdapter;
import com.mining.mining.adapter.RecyclerAdapter;
import com.mining.mining.databinding.ActivityMainBinding;
import com.mining.mining.download.PluginDownload;
import com.mining.mining.download.interFace.OnDownload;
import com.mining.mining.pager.home.HomePager;
import com.mining.mining.pager.mining.MiningPager;
import com.mining.mining.pager.my.MyPager;
import com.mining.mining.util.InstallUtil;
import com.mining.util.Handler;
import com.mining.util.MessageEvent;
import com.mining.util.OnHandler;
import com.xframe.network.OnData;
import com.xframe.network.SocketManage;
import com.xframe.widget.NumberProgressBar;
import com.xframe.widget.updateDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, OnData, OnHandler, PluginDownload.ProgressListener {
    private ActivityMainBinding binding;
    private final List<RecyclerAdapter> recyclerAdapters = new ArrayList<>();
    private final Handler handler = new Handler(Looper.myLooper(), this);
    private SharedPreferences sharedPreferences;
    private NumberProgressBar progressBar;
    private PluginDownload pluginDownload;
    private String download;
    private String version_code = "1";
    private int progress;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 设置状态栏字体黑色
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        sharedPreferences = getSharedPreferences(getPackageName(), Context.MODE_PRIVATE);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        EventBus.getDefault().register(this);
        initPager();
        initNavigation();
        SocketManage.init(this);
    }



    @Override
    public void connect(SocketManage socketManage) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("type", 15);
            jsonObject.put("code", 1);
            socketManage.print(jsonObject.toString());
        } catch (Exception e) {
            e.fillInStackTrace();
        }
    }

    @Override
    public void handleMessage(int w, String str) {
        if (w == 1) {
            update(str);
        }
    }

    private void update(String str) {
        try {
            JSONObject jsonObject = new JSONObject(str);
            download = jsonObject.getString("download");
            String log = jsonObject.getString("log");
            String force = jsonObject.getString("force");
            version_code = jsonObject.getString("version_code");
            updateDialog alertDialog = new updateDialog(this);
            alertDialog.setTitle("更新温馨提示");
            alertDialog.setMessage(log);
            progressBar = alertDialog.getProgress();
            File downloadDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsoluteFile();
            File downloadFile = new File(downloadDirectory, System.currentTimeMillis() + ".apk");
            pluginDownload = new PluginDownload(download, downloadFile, this);
            alertDialog.setOnOk("本地更新", (dialog, which) -> pluginDownload.run());
            alertDialog.setOnNo("浏览器更新", (dialog, which) -> {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(download));
                startActivity(intent);
            });
            if (force.equals("0")) {
                alertDialog.setOnClose("不在提醒", (dialog, which) -> {
                    sharedPreferences.edit().putInt("update", 1).apply();
                    sharedPreferences.edit().putString("version_code", version_code).apply();
                    dialog.cancel();
                });
            }
            alertDialog.show();
        } catch (Exception e) {
            e.fillInStackTrace();
        }
    }


    @Override
    public void handle(String ds) {
        try {
            JSONObject jsonObject = new JSONObject(ds);
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
                System.out.println(getVersionCode());
                handler.sendMessage(1, jsonObject.toString());
            }
        } catch (Exception e) {
            e.fillInStackTrace();
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
        EventBus.getDefault().unregister(this);
        for (RecyclerAdapter recyclerAdapter : recyclerAdapters) {
            recyclerAdapter.onDestroy();
        }
    }

    private void initPager() {
        recyclerAdapters.add(new HomePager(this));
        recyclerAdapters.add(new MiningPager(this));
        recyclerAdapters.add(new MyPager(this));
        PagerAdapter pagerAdapter = new PagerAdapter(recyclerAdapters);
        binding.pager.setAdapter(pagerAdapter);
        binding.pager.setOffscreenPageLimit(recyclerAdapters.size());
        binding.pager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                recyclerAdapters.get(position).StatusBar(MainActivity.this);
                binding.navigation.getMenu().getItem(position).setChecked(true);
            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        recyclerAdapters.get(item.getOrder()).StatusBar(MainActivity.this);
        binding.pager.setCurrentItem(item.getOrder());
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

    @Override
    public void error(Exception e) {

    }


    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onEvent(MessageEvent event) {
        if (event.getW() == 2) {
            binding.pager.setCurrentItem(0);
        }
    }
}
