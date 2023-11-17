package com.plugin.lib.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toolbar;

import com.plugin.activity.PluginInterface;
import com.plugin.lib.PluginLayoutInflater;
import com.plugin.lib.R;
import com.plugin.lib.Storage;
import com.plugin.lib.entity.PluginEntity;
import com.plugin.lib.util.StatusBarUtil;

public class BaseActivity extends Activity {
    private PluginEntity entity;
    private PluginInterface pluginInterface;
    private final int id = Storage.getInstance().id;
    public String log;
    private int mid = 0;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
        entity = Storage.getInstance().getPluginEntity(id);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtil.setImmersiveStatusBar(this, false);
        String className = getIntent().getStringExtra("className");
        mid = getIntent().getIntExtra("mid", 0);
        if (id == -1) {
            return;
        }
        if (entity == null) {
            return;
        }
        try {
            Class<?> aClass = entity.getDexClassLoader().loadClass(className);
            Object newInstance = aClass.newInstance();
            if (newInstance instanceof PluginInterface) {
                pluginInterface = (PluginInterface) newInstance;
                pluginInterface.attachContext(this);
                Bundle bundle = new Bundle();
                pluginInterface.onCreate(bundle);
            } else {
                log = "PluginInterface instanceof" + newInstance.getClass().getName();
                init();
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            log = e.getMessage();
            System.out.println(e.getMessage());
        }
    }

    private void init() {
        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        linearLayout.setOrientation(LinearLayout.VERTICAL);

        Toolbar toolbar = new Toolbar(this);
        toolbar.setTitle("异常");
        toolbar.setNavigationIcon(R.mipmap.close);
        toolbar.setNavigationOnClickListener(v -> finish());
        linearLayout.addView(toolbar);

        LinearLayout linearLayout1 = new LinearLayout(this);
        linearLayout1.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        linearLayout1.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setGravity(Gravity.CENTER);
        linearLayout.addView(linearLayout1);

        TextView textView = new TextView(this);
        textView.setText(String.format("包名：%s", entity.getPackageInfo().applicationInfo.packageName));
        textView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        linearLayout1.addView(textView);

        TextView textView1 = new TextView(this);
        textView1.setText(String.format("加载类名：%s", getIntent().getStringExtra("className")));
        textView1.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        linearLayout1.addView(textView1);

        TextView textView2 = new TextView(this);
        textView2.setText(String.format("异常错误：%s", log));
        textView2.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        linearLayout1.addView(textView2);

        setContentView(linearLayout);
    }


    @Override
    public Resources getResources() {
        if (pluginInterface != null) {
            return entity.getResources();
        }
        return super.getResources();
    }

    @Override
    public AssetManager getAssets() {
        if (pluginInterface != null) {
            return entity.getAssetManager();
        }
        return super.getAssets();
    }

    @Override
    public ClassLoader getClassLoader() {
        if (pluginInterface != null) {
            return entity.getDexClassLoader();
        }
        return super.getClassLoader();
    }

    @Override
    public ApplicationInfo getApplicationInfo() {
        if (pluginInterface != null) {
            return entity.getPackageInfo().applicationInfo;
        }
        return super.getApplicationInfo();
    }

    @Override
    public void startActivity(Intent intent) {
        if (intent.getAction() != null) {
            //跳转其他应用
            super.startActivity(intent);
            return;
        }
        Intent newIntent = new Intent(this, BaseActivity.class);
        if (intent.getComponent() == null) {
            return;
        }
        newIntent.putExtra("className", intent.getComponent().getClassName());
        newIntent.putExtra("mid", mid + 1);
        newIntent.putExtra("id", id);
        super.startActivity(newIntent);
    }

    @Override
    public void setContentView(View view) {
        try {
            super.setContentView(view);
        } catch (Exception e) {
            e.fillInStackTrace();
        }
    }

    @Override
    public Resources.Theme getTheme() {
        if (pluginInterface != null) {
            return entity.getTheme();
        }
        return super.getTheme();
    }

    @Override
    protected void onStart() {
        if (pluginInterface != null) {
            pluginInterface.onStart();
        }
        super.onStart();
    }

    @Override
    public LayoutInflater getLayoutInflater() {
        if (pluginInterface != null) {
            return PluginLayoutInflater.from(this, entity.getDexClassLoader());
        }
        return super.getLayoutInflater();
    }

    @Override
    protected void onResume() {
        if (pluginInterface != null) {
            pluginInterface.onResume();
        }
        super.onResume();
    }

    @Override
    protected void onPause() {
        if (pluginInterface != null) {
            pluginInterface.onPause();
        }
        super.onPause();
    }

    @Override
    protected void onStop() {
        if (pluginInterface != null) {
            pluginInterface.onStop();
        }
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        if (pluginInterface != null) {
            pluginInterface.onDestroy();
        }
        super.onDestroy();
    }

    @Override
    public void finish() {
        super.finish();
        //判断是否是最会一个页面
        if (mid == 0) {
            Storage.getInstance().remove(id);
        }
    }

    @Override
    protected void onRestart() {
        if (pluginInterface != null) {
            pluginInterface.onRestart();
        }
        super.onRestart();
    }

}
