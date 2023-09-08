package com.xframe.widget.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class FileUtil {

    private static final String mformatType = "yy-MM-dd HH:mm:ss";
    public static String getFileLastModifiedTime(File file) {
        Calendar cal = Calendar.getInstance();
        long time = file.lastModified();
        SimpleDateFormat formatter = new SimpleDateFormat(mformatType);
        cal.setTimeInMillis(time);
        // 输出：修改时间[2] 2009-08-17 10:32:38
        return formatter.format(cal.getTime());
    }

    /**
     * @param destDirName 创建文件夹
     * @return
     */
    public static boolean createDir(String destDirName) {
        File dir = new File(destDirName);
        if (dir.exists()) {// 判断目录是否存在
            System.out.println("创建目录失败，目标目录已存在！");
            return false;
        }
        if (!destDirName.endsWith(File.separator)) {// 结尾是否以"/"结束
            destDirName = destDirName + File.separator;
        }
        if (dir.mkdirs()) {// 创建目标目录
            System.out.println("创建目录成功！" + destDirName);
            return true;
        } else {
            System.out.println("创建目录失败！");
            return false;
        }
    }

    /**
     * 复制单个文件
     *
     * @param oldPath String 原文件路径 如：data/video/xxx.mp4
     * @param newPath String 复制后路径 如：data/oss/xxx.mp4
     * @return boolean
     */
    public static void copyFile(String oldPath, String newPath) {
        try {
            int bytesum = 0;
            int byteread;
            File oldfile = new File(oldPath);
            File newFile = new File(newPath);
            if (!newFile.exists()) {
                newFile.createNewFile();
            }
            if (oldfile.exists()) { //文件存在时
                InputStream inStream = new FileInputStream(oldPath); //读入原文件
                FileOutputStream fs = new FileOutputStream(newPath);
                byte[] buffer = new byte[1444];
                while ((byteread = inStream.read(buffer)) != -1) {
                    bytesum += byteread; //字节数 文件大小
                    System.out.println(bytesum);
                    fs.write(buffer, 0, byteread);
                }
                inStream.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * @param zipPtath        zip压缩
     * @param outputDirectory 写入
     * @throws IOException
     */
    public static void unzipFile(String zipPtath, String outputDirectory) throws IOException {
        // 创建解压目标目录
        File file = new File(outputDirectory);
        // 如果目标目录不存在，则创建
        if (!file.exists()) {
            file.mkdirs();
        }
        // 打开压缩文件
        InputStream inputStream = new FileInputStream(zipPtath);
        ZipInputStream zipInputStream = new ZipInputStream(inputStream);
        // 读取一个进入点
        ZipEntry zipEntry = zipInputStream.getNextEntry();
        // 使用1Mbuffer
        byte[] buffer = new byte[1024 * 1024];
        // 解压时字节计数
        int count = 0;
        // 如果进入点为空说明已经遍历完所有压缩包中文件和目录
        while (zipEntry != null) {
            if (!zipEntry.isDirectory()) {  //如果是一个文件
                // 如果是文件
                String fileName = zipEntry.getName();
                fileName = fileName.substring(fileName.lastIndexOf("/") + 1);  //截取文件的名字 去掉原文件夹名字
                file = new File(outputDirectory + File.separator + fileName);  //放到新的解压的文件路径
                if (fileName.endsWith(".so") && !file.exists()) {
                    file.createNewFile();
                    FileOutputStream fileOutputStream = new FileOutputStream(file);
                    while ((count = zipInputStream.read(buffer)) > 0) {
                        fileOutputStream.write(buffer, 0, count);
                    }
                    fileOutputStream.close();
                }
            }
            // 定位到下一个文件入口
            zipEntry = zipInputStream.getNextEntry();
        }
        zipInputStream.close();
    }

    /**
     * 删除文件夹里面的所有文件
     *
     * @param path String 文件夹路径 如 c:/fqf
     */
    public void delAllFile(String path) {
        File file = new File(path);
        if (!file.exists()) {
            return;
        }
        if (!file.isDirectory()) {
            return;
        }
        String[] tempList = file.list();
        File temp;
        for (String s : tempList) {
            if (path.endsWith(File.separator)) {
                temp = new File(path + s);
            } else {
                temp = new File(path + File.separator + s);
            }
            if (temp.isFile()) {
                temp.delete();
            }
            if (temp.isDirectory()) {
                delAllFile(path + "/" + s);//先删除文件夹里面的文件
                delFolder(path + "/" + s);//再删除空文件夹
            }
        }
    }

    /**
     * @param folderPath 删除单个界面
     */
    public void delFolder(String folderPath) {

        try {
            delAllFile(folderPath); //删除完里面所有内容
            String filePath = folderPath;
            filePath = filePath.toString();
            File myFilePath = new File(filePath);
            myFilePath.delete(); //删除空文件夹
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
