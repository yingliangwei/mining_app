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
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.alibaba.fastjson2.JSONObject;
import com.google.android.material.navigation.NavigationView;
import com.mining.mining.R;
import com.mining.mining.activity.login.LoginActivity;
import com.mining.mining.activity.set.AboutActivity;
import com.mining.mining.activity.set.CardActivity;
import com.mining.mining.activity.set.ModifyNameActivity;
import com.mining.mining.activity.set.SetPayPassActivity;
import com.mining.mining.activity.task.ActivityTask;
import com.mining.mining.adapter.PagerAdapter;
import com.mining.mining.adapter.RecyclerAdapter;
import com.mining.mining.databinding.ActivityMainBinding;
import com.mining.mining.download.PluginDownload;
import com.mining.mining.entity.MessageEvent;
import com.mining.mining.pager.home.HomePager;
import com.mining.mining.pager.mining.MiningPager;
import com.mining.mining.pager.my.MyPager;
import com.mining.mining.pager.task.TaskPager;
import com.mining.mining.util.InstallUtil;
import com.mining.mining.util.SharedUtil;
import com.mining.util.StringUtil;
import com.xframe.network.OnData;
import com.xframe.network.SocketManage;
import com.xframe.widget.NumberProgressBar;
import com.xframe.widget.entity.RecyclerEntity;
import com.xframe.widget.recycler.OnRecyclerItemClickListener;
import com.xframe.widget.updateDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import q.rorbin.badgeview.QBadgeView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, OnData, PluginDownload.ProgressListener, OnRecyclerItemClickListener {
    private ActivityMainBinding binding;
    private final List<RecyclerAdapter> recyclerAdapters = new ArrayList<>();
    private SharedPreferences sharedPreferences;
    private NumberProgressBar progressBar;
    private PluginDownload pluginDownload;
    private String download;
    private String version_code = "1";
    private int progress;
    private final List<List<RecyclerEntity>> entity = new ArrayList<>();
    private final List<RecyclerEntity> entities = new ArrayList<>();
    private final List<RecyclerEntity> entities3 = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        // 设置状态栏字体黑色
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        sharedPreferences = getSharedPreferences(getPackageName(), Context.MODE_PRIVATE);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        ActivityTask.getInstance().setBinding(binding);
        setContentView(binding.getRoot());
        initPager();
        initNavigation();
        initRecycler();
        initDrawerLayout();
        SocketManage.init(this);
        SocketManage.init(new getUserData());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessage(MessageEvent event) {
        if (event.isClass(HomePager.class)) {
            binding.drawer.open();
        }
    }

    private void initDrawerLayout() {
        binding.toolbar.setOnMenuItemClickListener(item -> {
            binding.drawer.closeDrawer(GravityCompat.START);
            return false;
        });
    }

    private void initRecycler() {
        entities.add(new RecyclerEntity(R.mipmap.nick, "昵称", 0, "未设置", "", "name"));
        entities.add(new RecyclerEntity(R.mipmap.phone, "绑定手机", 0, "未绑定", "", "phone", true));

        entities3.add(new RecyclerEntity(R.mipmap.pass, "修改支付密码", 0, "", "", "pay"));
        entities3.add(new RecyclerEntity(R.mipmap.card, "实名制", 0, "未实名", "", "card"));

        List<RecyclerEntity> entities1 = new ArrayList<>();
        entities1.add(new RecyclerEntity(R.mipmap.log_off, "注销账号", 0, "", "", "log_off"));
        entities1.add(new RecyclerEntity(R.mipmap.complaint, "关于", 0, "", "", "g"));

        entity.add(entities);
        entity.add(entities3);
        entity.add(entities1);
        binding.recycle.setOnRecyclerItemClickListener(this);
        binding.recycle.add(entity);
        binding.recycle.notifyDataSetChanged();
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
        updateDialog alertDialog = new updateDialog(this);
        Window window = alertDialog.getWindow();
        if (window != null) {
            window.setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.bg_blue));
        }
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

    @Override
    public void onItemClick(RecyclerEntity entity, int position) {
        if (entity.getKey() == null) {
            return;
        }
        switch (entity.getKey()) {
            case "name", "uid" -> {
                Intent intent = new Intent(this, ModifyNameActivity.class);
                intent.putExtra("name", entity.text);
                startActivity(intent);
            }
            case "pay" -> startActivity(new Intent(this, SetPayPassActivity.class));
            case "back" -> {
                SharedPreferences sharedPreferences = getSharedPreferences("user", Context.MODE_PRIVATE);
                sharedPreferences.edit().clear().apply();
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
            case "g" -> startActivity(new Intent(this, AboutActivity.class));
            case "card" -> {
                if (entity.isArray) {
                    return;
                }
                startActivity(new Intent(this, CardActivity.class));
            }
            case "log_off" -> startActivity(new Intent(this, LogOffActivity.class));
        }
    }

    private class getUserData implements OnData {
        @Override
        public void connect(SocketManage socketManage) {
            SharedUtil sharedUtil = new SharedUtil(MainActivity.this);
            JSONObject jsonObject = sharedUtil.getLogin(7, 1);
            socketManage.print(jsonObject.toString());
        }

        @Override
        public void handle(String ds) {
            JSONObject jsonObject = JSONObject.parseObject(ds);
            int code = jsonObject.getInteger("code");
            if (code == 200) {
                JSONObject data = jsonObject.getJSONObject("data");
                initData(data);
            }
        }

        private void initData(JSONObject jsonObject) {
            String name = jsonObject.getString("name");
            String phone = jsonObject.getString("phone");
            String id = jsonObject.getString("id");
            int card = jsonObject.getInteger("card");
            binding.name.setText(name);
            binding.nameX.setText(StringUtil.getStringStart(name));
            binding.id.setText(id);
            entities.clear();
            entities3.clear();
            entities.add(new RecyclerEntity(R.mipmap.nick, "昵称", 0, name, "", "name"));
            entities.add(new RecyclerEntity(R.mipmap.phone, "绑定手机", 0, phone, "", "phone", true));
            entities3.add(new RecyclerEntity(R.mipmap.pass, "修改支付密码", 0, "", "", "pay"));
            if (card == 1) {
                entities3.add(new RecyclerEntity(R.mipmap.card, "实名制", 0, "以实名", "", "card", true));
            } else {
                entities3.add(new RecyclerEntity(R.mipmap.card, "实名制", 0, "未实名", "", "card"));
            }
            binding.recycle.notifyDataSetChanged();
        }
    }
}
