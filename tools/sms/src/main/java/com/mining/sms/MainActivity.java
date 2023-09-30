package com.mining.sms;

import static android.os.Build.VERSION.SDK_INT;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.provider.Telephony;

import androidx.annotation.Nullable;

import com.mining.sms.service.FirstService;

import java.util.List;

public class MainActivity extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initPermission(this);
    }

    private void initFirstService() {
        if (!serverIsRunning(this, FirstService.class.getName())) {
            Intent intent = new Intent(this, FirstService.class);
            if (SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(intent);
            } else {
                startService(intent);
            }
        }
    }

    public boolean serverIsRunning(Context context, String componentName) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (activityManager == null) {
            return false;
        }
        List<ActivityManager.RunningServiceInfo> runningServices
                = activityManager.getRunningServices(Integer.MAX_VALUE);
        if (runningServices.size() <= 0) {
            return false;
        }
        for (ActivityManager.RunningServiceInfo serviceInfo : runningServices) {
            if (componentName.equals(serviceInfo.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 10 && resultCode == 0) {
            //解决默认短信
            initPermission(this);
        } else if (requestCode == 10 && resultCode == -1) {
            ignoreBatteryOptimization(this);
            //启动后台
            initFirstService();
        } else if (requestCode == 11 && resultCode == 0) {
            //再次申请电池优化
            ignoreBatteryOptimization(this);
        }
    }

    /**
     * 忽略电池优化
     */
    void ignoreBatteryOptimization(Activity activity) {
        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        boolean hasIgnored;
        hasIgnored = powerManager.isIgnoringBatteryOptimizations(activity.getPackageName());
        //  判断当前APP是否有加入电池优化的白名单，如果没有，弹出加入电池优化的白名单的设置对话框。
        if (!hasIgnored) {
            Intent intent = new Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
            intent.setData(Uri.parse("package:" + activity.getPackageName()));
            startActivityForResult(intent, 11);
        }
    }

    //默认短信
    void initPermission(Activity activity) {
        String defaultSmsApp = Telephony.Sms.getDefaultSmsPackage(this);
        //获取手机当前设置的默认短信应用的包名
        String packageName = activity.getPackageName();
        if (defaultSmsApp == null) {
            System.out.println("defaultSmsApp null");
            return;
        }
        if (!defaultSmsApp.equals(packageName)) {
            Intent intent = new Intent(Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT);
            intent.putExtra(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME, packageName);
            startActivityForResult(intent, 10);
        }
    }
}
