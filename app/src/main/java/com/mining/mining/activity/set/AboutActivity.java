package com.mining.mining.activity.set;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.mining.mining.R;
import com.mining.mining.databinding.ActivityAboutBinding;
import com.mining.mining.download.PluginDownload;
import com.mining.mining.util.InstallUtil;
import com.mining.util.StatusBarUtil;
import com.xframe.network.OnData;
import com.xframe.network.SocketManage;
import com.xframe.widget.NumberProgressBar;
import com.xframe.widget.entity.RecyclerEntity;
import com.xframe.widget.recycler.OnRecyclerItemClickListener;
import com.xframe.widget.updateDialog;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class AboutActivity extends AppCompatActivity implements OnRecyclerItemClickListener, OnData, PluginDownload.ProgressListener {
    public ActivityAboutBinding binding;
    private final List<List<RecyclerEntity>> entity = new ArrayList<>();
    private NumberProgressBar progressBar;
    private int progress;
    private PluginDownload pluginDownload;
    private String download;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtil.setImmersive(this, true);
        binding = ActivityAboutBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initView();
        initRecycler();
    }

    private void initRecycler() {
        List<RecyclerEntity> entities = new ArrayList<>();
        entities.add(new RecyclerEntity(R.mipmap.complaint, "检查版本", 0, "", "", "inspect"));
        entities.add(new RecyclerEntity(R.mipmap.agreement, "用户协议", 0, "", "", "user"));
        entities.add(new RecyclerEntity(R.mipmap.agreement, "隐私协议", 0, "", "", "privacy"));
        entity.add(entities);
        binding.recycler.setOnRecyclerItemClickListener(this);
        binding.recycler.add(entity);
        binding.recycler.notifyDataSetChanged();
    }

    private void initView() {
        binding.version.setText(String.format("Version %s", getAppVersionName()));
    }

    public String getAppVersionName() {
        PackageManager packageManager = getPackageManager();
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(getPackageName(), 0);
            return packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void onItemClick(RecyclerEntity entity, int position) {
        if (entity.key.equals("inspect")) {
            SocketManage.init(this);
        }
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
    public void handle(String ds) {
        try {
            JSONObject jsonObject = new JSONObject(ds);
            String version_code = jsonObject.getString("version_code");
            String force = jsonObject.getString("force");
            int version = Integer.parseInt(version_code);
            if (version > getVersionCode()) {
                update(jsonObject.toString());
            } else {
                Toast.makeText(this, "当前版本以是最新版本", Toast.LENGTH_SHORT).show();
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


    private void update(String str) {
        try {
            JSONObject jsonObject = new JSONObject(str);
            download = jsonObject.getString("download");
            String log = jsonObject.getString("log");
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
            alertDialog.setOnClose("下次更新", (dialog, which) -> dialog.cancel());
            alertDialog.show();
        } catch (Exception e) {
            e.fillInStackTrace();
        }
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
}
