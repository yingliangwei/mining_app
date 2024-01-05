package com.mining.mining.util;

import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.annotation.NonNull;

public class PhoneUtil {
    public static String getDeviceId(@NonNull Context context) {
        String deviceId = "";
        try {
            // 获取设备的唯一标识
            PackageManager packageManager = context.getPackageManager();
            String[] packages = packageManager.getPackagesForUid(android.os.Process.myUid());
            if (packages != null && packages.length > 0) {
                String packageName = packages[0];
                deviceId = packageManager.getPackageInfo(packageName, 0).firstInstallTime + "";
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.e("", "Failed to get device ID", e);
        }
        return deviceId;
    }
}
