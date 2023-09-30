package com.mining.mining.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;

import androidx.core.content.FileProvider;

import java.io.File;

public class InstallUtil {

    public static void installApk(Context context, String fileName) {

        File file = new File(fileName);

        int index = fileName.lastIndexOf(".");

        String nameExtra = fileName.substring(index + 1, fileName.length());

        if (nameExtra.equals("apk")) {

            Intent intent = new Intent(Intent.ACTION_VIEW);

            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            if (Build.VERSION.SDK_INT >= 7) {//20200616 android10以上版本安装没有权限报错问题解决

                Uri apkUri = FileProvider.getUriForFile(context, "com.mining.mining.fileprovider", file); //与manifest中定义的provider中的authorities保持一致

                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                intent.setDataAndType(apkUri, "application/vnd.android.package-archive");

            } else {
                intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
            }

            context.startActivity(intent);

        }

    }

}
