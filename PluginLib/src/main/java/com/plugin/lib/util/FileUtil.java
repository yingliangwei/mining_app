package com.plugin.lib.util;

import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public class FileUtil {
    public static boolean copyFile(String oldPathName, String newPathName) {
        try {
            File oldFile = new File(oldPathName);
            if (!oldFile.exists()) {
                Log.e("copyFile", "copyFile:  oldFile not exist.");
                return false;
            } else if (!oldFile.isFile()) {
                Log.e("copyFile", "copyFile:  oldFile not file.");
                return false;
            } else if (!oldFile.canRead()) {
                Log.e("copyFile", "copyFile:  oldFile cannot read.");
                return false;
            }
            FileInputStream fileInputStream = new FileInputStream(oldPathName);
            FileOutputStream fileOutputStream = new FileOutputStream(newPathName);
            byte[] buffer = new byte[1024];
            int byteRead;
            while (-1 != (byteRead = fileInputStream.read(buffer))) {
                fileOutputStream.write(buffer, 0, byteRead);
            }
            fileInputStream.close();
            fileOutputStream.flush();
            fileOutputStream.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
