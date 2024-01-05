package com.plugin.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import java.io.File;

public class PluginActivity extends Activity implements PluginInterface {
    protected Activity thisContex;
    private Object o;

    public Object getO() {
        if (o == null) {
            return this;
        } else {
            return o;
        }
    }


    @Override
    public SharedPreferences getSharedPreferences(String name, int mode) {
        if (thisContex != null) {
            return thisContex.getSharedPreferences(name, mode);
        }
        return super.getSharedPreferences(name, mode);
    }

    @Override
    public void attachContext(Activity context) {
        thisContex = context;
        super.attachBaseContext(context);
    }

    public Activity getActivity() {
        if (thisContex == null) {
            return this;
        } else {
            return thisContex;
        }
    }

    @Override
    public File getExternalCacheDir() {
        if (thisContex != null) {
            return thisContex.getExternalCacheDir();
        }
        return super.getExternalCacheDir();
    }

    @Override
    public void onCreate(Bundle bundle) {
        if (thisContex == null) {
            super.onCreate(bundle);
        }
    }

    @Override
    public boolean setImmersiveStatusBar() {
        return true;
    }

    @Override
    public void attachInstance(Object o) {
        this.o = o;
    }

    @Override
    public AssetManager getAssets() {
        if (thisContex == null) {
            return super.getAssets();
        }
        return thisContex.getAssets();
    }

    @Override
    public void setContentView(int layoutResID) {
        if (thisContex == null) {
            super.setContentView(layoutResID);
            return;
        }
        thisContex.setContentView(layoutResID);
    }

    @Override
    public void setContentView(View view) {
        if (thisContex == null) {
            super.setContentView(view);
            return;
        }
        thisContex.setContentView(view);
    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        if (thisContex == null) {
            super.setContentView(view, params);
            return;
        }
        thisContex.setContentView(view, params);
    }

    @Override
    public LayoutInflater getLayoutInflater() {
        if (thisContex == null) {
            return super.getLayoutInflater();
        }
        return thisContex.getLayoutInflater();
    }

    @Override
    public Window getWindow() {
        if (thisContex == null) {
            return super.getWindow();
        }
        return thisContex.getWindow();
    }

    @Override
    public Object getSystemService(String name) {
        if (thisContex == null) {
            return super.getSystemService(name);
        }
        return thisContex.getSystemService(name);
    }

    @Override
    public Resources getResources() {
        if (thisContex == null) {
            return super.getResources();
        }
        return thisContex.getResources();
    }

    @Override
    public Resources.Theme getTheme() {
        if (thisContex == null) {
            return super.getTheme();
        }
        return thisContex.getTheme();
    }

    @Override
    public <T extends View> T findViewById(int id) {
        if (thisContex == null) {
            return super.findViewById(id);
        }
        return thisContex.findViewById(id);
    }


    @Override
    public ClassLoader getClassLoader() {
        if (thisContex == null) {
            return super.getClassLoader();
        }
        return thisContex.getClassLoader();
    }

    @Override
    public WindowManager getWindowManager() {
        if (thisContex == null) {
            return super.getWindowManager();
        }
        return thisContex.getWindowManager();
    }

    @Override
    public ApplicationInfo getApplicationInfo() {
        if (thisContex == null) {
            return super.getApplicationInfo();
        }
        return thisContex.getApplicationInfo();
    }

    @Override
    public void finish() {
        if (thisContex == null) {
            super.finish();
            return;
        }
        thisContex.finish();
    }

    @Override
    public void onStart() {
        if (thisContex == null) {
            super.onStart();
        }
    }

    @Override
    public void onResume() {
        if (thisContex == null) {
            super.onResume();
        }
    }


    @Override
    public void onPause() {
        if (thisContex == null) {
            super.onPause();
        }
    }

    @Override
    public void onStop() {
        if (thisContex == null) {
            super.onStop();
        }
    }


    @Override
    public void onRestart() {
        if (thisContex == null) {
            super.onRestart();
        }
    }

    @Override
    public void onDestroy() {
        if (thisContex == null) {
            super.onDestroy();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (thisContex == null) {
            super.onSaveInstanceState(outState);
        }
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return false;
    }

    @Override
    public void onBackPressed() {
        if (thisContex == null) {
            super.onBackPressed();
            return;
        }
        thisContex.onBackPressed();
    }

    @Override
    public void sendBroadcast(Intent intent) {
        if (thisContex == null) {
            super.sendBroadcast(intent);
            return;
        }
        thisContex.sendBroadcast(intent);
    }

    @Override
    public Intent registerReceiver(BroadcastReceiver receiver, IntentFilter filter) {
        if (thisContex == null) {
            return super.registerReceiver(receiver, filter);
        }
        return thisContex.registerReceiver(receiver, filter);
    }

    @Override
    public void unregisterReceiver(BroadcastReceiver receiver) {
        if (thisContex == null) {
            super.unregisterReceiver(receiver);
            return;
        }
        thisContex.unregisterReceiver(receiver);
    }

    @Override
    public void startActivity(Intent intent) {
        if (thisContex == null) {
            super.startActivity(intent);
        } else {
            thisContex.startActivity(intent);
        }
    }

}
