package com.plugin.lib;

import android.accessibilityservice.AccessibilityService;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.graphics.Bitmap;
import android.graphics.BlendMode;
import android.graphics.BlendModeColorFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.AdaptiveIconDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.util.AttributeSet;
import android.util.Xml;

import com.plugin.lib.entity.PluginEntity;
import com.plugin.lib.util.FileUtil;

import org.xmlpull.v1.XmlPullParser;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class PluginManager {
    private final Context context;
    private File PluginFile1;//储存到本地
    private final File PluginFile;//插件地址
    private final File dexFile;//dex储存地址
    private final File nativeFile;
    private ApkClassLoader dexClassLoader;
    private PackageInfo packageInfo;
    private Resources mResources;
    private AssetManager assertManagerObj;
    private Resources.Theme theme;
    private final Resources.Theme contextTheme;
    private File imageFile;
    private XmlResourceParser AndroidManifest;
    private boolean isCopy = false;


    public void setCopy(boolean copy) {
        isCopy = copy;
    }

    public boolean isCopy() {
        return isCopy;
    }

    public PluginManager(Context context, File file) {
        contextTheme = context.getTheme();
        this.PluginFile = file;
        this.context = context;

        dexFile = context.getDir("dex", Context.MODE_PRIVATE);
        if (!dexFile.exists()) {
            dexFile.mkdir();
        }
        nativeFile = context.getDir("native", Context.MODE_PRIVATE);
        if (!nativeFile.exists()) {
            nativeFile.mkdir();
        }
        PluginFile1 = context.getDir("apk", Context.MODE_PRIVATE);
        if (!PluginFile1.exists()) {
            PluginFile1.mkdir();
        }
        imageFile = context.getDir("images", Context.MODE_PRIVATE);
        if (!imageFile.exists()) {
            imageFile.mkdir();
        }
    }

    public void load() throws Exception {
        copy();
        init();
        initResources();
    }

    public void copy() throws Exception {
        if (!PluginFile.exists()) {
            throw new Exception("插件为空");
        }
        if (PluginFile.getAbsolutePath().contains(PluginFile1.getAbsolutePath())) {
            this.PluginFile1 = PluginFile;
            return;
        }
        if (isCopy) {
            PluginFile1 = new File(PluginFile1, System.currentTimeMillis() + ".zip");
            boolean isCopy = FileUtil.copyFile(PluginFile.getAbsolutePath(), PluginFile1.getAbsolutePath());
            if (!isCopy) {
                throw new Exception("复制到本地失败");
            }
        } else {
            PluginFile1 = PluginFile;
        }
    }

    public void initResources() throws Exception {
        Class<AssetManager> assetManagerClass = AssetManager.class;
        assertManagerObj = assetManagerClass.newInstance();
        Method addAssetPathMethod = assetManagerClass.getMethod("addAssetPath", String.class);
        addAssetPathMethod.setAccessible(true);
        // 塞入原来宿主的资源
        int cookie = (int) addAssetPathMethod.invoke(assertManagerObj, PluginFile1.getAbsolutePath());
        AndroidManifest = assertManagerObj.openXmlResourceParser(cookie, "AndroidManifest.xml");
        Resources superRes = context.getResources();
        mResources = new Resources(assertManagerObj, superRes.getDisplayMetrics(), superRes.getConfiguration());
        theme = mResources.newTheme();
        theme.setTo(contextTheme);
        initInnerRIdValue(theme, packageInfo.packageName + ".R$style", true);
        //initInnerRIdValue(theme, "androidx.appcompat.R$style", false);
    }

    public AccessibilityService getAccessibilityServiceEntity() {
        String accessibilityService = getAccessibilityService();
        if (accessibilityService == null) {
            return null;
        }
        try {
            Class<?> aClass = dexClassLoader.loadClass(accessibilityService);
            Object newInstance = aClass.newInstance();
            if (newInstance instanceof AccessibilityService) {
                return (AccessibilityService) newInstance;
            }
        } catch (Exception e) {
            e.fillInStackTrace();
        }
        return null;
    }

    public String getAccessibilityService() {
        String activity = null;
        try {
            int eventType = AndroidManifest.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG) {
                    String tagName = AndroidManifest.getName();
                    if (tagName.equals("service")) {
                        AttributeSet attributeSet3 = Xml.asAttributeSet(AndroidManifest);
                        String name = getAttributeValue(attributeSet3, "name");
                        AndroidManifest.next();
                        tagName = AndroidManifest.getName();
                        if (tagName.equals("intent-filter")) {
                            AndroidManifest.next();
                            tagName = AndroidManifest.getName();
                            if (tagName.equals("action")) {
                                AttributeSet attributeSet = Xml.asAttributeSet(AndroidManifest);
                                String action = getAttributeValue(attributeSet, "name");
                                if (action.equals("android.accessibilityservice.AccessibilityService")) {
                                    return name;
                                }
                            }
                        }
                    }
                }
                eventType = AndroidManifest.next();
            }
        } catch (Exception e) {
            e.fillInStackTrace();
        }
        return activity;
    }

    public String getMainActivity() {
        String activity = null;
        try {
            int eventType = AndroidManifest.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG) {
                    String tagName = AndroidManifest.getName();
                    if (tagName.equals("activity")) {
                        AttributeSet attributeSet3 = Xml.asAttributeSet(AndroidManifest);
                        String name = getAttributeValue(attributeSet3, "name");
                        AndroidManifest.next();
                        tagName = AndroidManifest.getName();
                        if (tagName.equals("intent-filter")) {
                            AndroidManifest.next();
                            tagName = AndroidManifest.getName();
                            System.out.println("tag1:" + tagName);
                            if (tagName.equals("action")) {
                                AttributeSet attributeSet = Xml.asAttributeSet(AndroidManifest);
                                String action = getAttributeValue(attributeSet, "name");
                                AndroidManifest.next();
                                AndroidManifest.next();
                                tagName = AndroidManifest.getName();
                                if (tagName.equals("category")) {
                                    AttributeSet attributeSet1 = Xml.asAttributeSet(AndroidManifest);
                                    String category = getAttributeValue(attributeSet1, "name");
                                    if (category.equals("android.intent.category.LAUNCHER") && action.equals("android.intent.action.MAIN")) {
                                        activity = name;
                                        return name;
                                    }
                                }
                            }
                        }
                    }
                }
                eventType = AndroidManifest.next();
            }
        } catch (Exception e) {
            e.fillInStackTrace();
        }
        return activity;
    }

    private String getAttributeValue(AttributeSet set, String name) {
        for (int i = 0; i < set.getAttributeCount(); i++) {
            String nameX = set.getAttributeName(i);
            if (nameX.equals(name)) {
                return set.getAttributeValue(i);
            }
        }
        return "";
    }

    private void initInnerRIdValue(Resources.Theme theme, String rStrnig, boolean force) {
        try {
            Class<?> cls = dexClassLoader.loadClass(rStrnig);
            Field[] field = cls.getDeclaredFields();
            for (Field field1 : field) {
                int value = field1.getInt(null);
                theme.applyStyle(value, force);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public void init() {
        //解压提取so到本地路径
        if (PluginFile1.exists() && !nativeFile.exists()) {
            try {
                unzipFile(PluginFile.getAbsolutePath(), nativeFile.getAbsolutePath());
            } catch (IOException e) {
                e.fillInStackTrace();
            }
        }
        dexClassLoader = new ApkClassLoader(context.getClassLoader(), PluginFile1.getAbsolutePath(), dexFile.getAbsolutePath(), nativeFile.getAbsolutePath(), context.getClassLoader());
        packageInfo = context.getPackageManager().getPackageArchiveInfo(PluginFile1.getAbsolutePath(), PackageManager.GET_ACTIVITIES | PackageManager.GET_SERVICES);
    }


    public void unzipFile(String zipPtath, String outputDirectory) throws IOException {
        try {
            // 创建解压目标目录
            File file = new File(outputDirectory);
            // 如果目标目录不存在，则创建
            if (!file.exists()) {
                boolean is = file.mkdirs();
            }
            // 打开压缩文件
            InputStream inputStream;
            inputStream = Files.newInputStream(Paths.get(zipPtath));
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
                        boolean is = file.createNewFile();
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
        } catch (Exception e) {
            e.fillInStackTrace();
        }
    }

    public PluginEntity getPluginEntity() {
        String title = mResources.getString(packageInfo.applicationInfo.labelRes);
        Long versionCode = packageInfo.getLongVersionCode();
        String pack = packageInfo.packageName;
        PluginEntity entity = new PluginEntity();
        entity.setContextTheme(contextTheme);
        entity.setResources(mResources);
        entity.setPackageInfo(packageInfo);
        entity.setDexClassLoader(dexClassLoader);
        entity.setAssetManager(assertManagerObj);
        entity.setTheme(theme);
        entity.setPluginFile(PluginFile1);
        entity.setImage(imageFile);
        entity.setMain(getMainActivity());
        entity.setTitle(title);
        entity.setVersionCode(versionCode);
        entity.setAccessibilityService(getAccessibilityServiceEntity());
        entity.setPackName(pack);
        return entity;
    }

    /**
     * 将Drawable转化为Bitmap
     *
     * @param drawable
     * @return
     */
    public Bitmap drawableToBitmap(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        } else {
            if (drawable instanceof AdaptiveIconDrawable) {
                Drawable backgroundDr = ((AdaptiveIconDrawable) drawable).getBackground();
                Drawable foregroundDr = ((AdaptiveIconDrawable) drawable).getForeground();
                Drawable[] drr = new Drawable[2];
                drr[0] = backgroundDr;
                drr[1] = foregroundDr;

                LayerDrawable layerDrawable = new LayerDrawable(drr);
                // 设置 LayerDrawable 的底色
                layerDrawable.setColorFilter(new BlendModeColorFilter(Color.WHITE, BlendMode.SRC_ATOP));

                int width = layerDrawable.getIntrinsicWidth();
                int height = layerDrawable.getIntrinsicHeight();
                Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                // 在 Bitmap 上绘制一个矩形，以设置底色
                Canvas canvas = new Canvas(bitmap);
                layerDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
                layerDrawable.draw(canvas);
                return bitmap;
            }
        }
        return null;
    }

    /**
     * 将Bitmap以指定格式保存到指定路径
     */
    public void saveBitmap(Bitmap bitmap, File file) {
        try {
            // 创建一个 FileOutputStream 对象，用于写入文件
            FileOutputStream outputStream = new FileOutputStream(file);
            // 创建一个 Bitmap 对象，表示要写入的 Drawable
            // 将 Bitmap 写入文件
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            // 关闭 FileOutputStream 对象
            outputStream.close();
        } catch (Exception e) {
            e.fillInStackTrace();
        }
    }

    public void setImageFile(File imageFile) {
        this.imageFile = imageFile;
    }

    public void setTheme(Resources.Theme theme) {
        this.theme = theme;
    }
}
