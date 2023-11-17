package com.plugin.lib.entity;

import android.accessibilityservice.AccessibilityService;
import android.content.pm.PackageInfo;
import android.content.res.AssetManager;
import android.content.res.Resources;

import com.plugin.lib.ApkClassLoader;

import java.io.File;

import dalvik.system.DexClassLoader;

public class PluginEntity {
    public AccessibilityService accessibilityService;
    public DexClassLoader dexClassLoader;
    public PackageInfo packageInfo;
    public Resources mResources;
    public AssetManager assetManager;
    public Resources.Theme theme,contextTheme;
    public File plugin_file;
    public File image;
    public String title;
    public Long versionCode;
    public String PackName;
    public String time;
    public int id;
    public long size;
    public String packageName;
    public String mClass;
    public int uid;
    public String versionName;
    public String Main;


    public void setContextTheme(Resources.Theme contextTheme) {
        this.contextTheme = contextTheme;
    }

    public Resources.Theme getContextTheme() {
        return contextTheme;
    }

    public String getMain() {
        return Main;
    }

    public void setMain(String main) {
        Main = main;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public int getUid() {
        return uid;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getMClass() {
        return mClass;
    }

    public void setMClass(String mClass) {
        this.mClass = mClass;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public long getSize() {
        return size;
    }

    public AccessibilityService getAccessibilityService() {
        return accessibilityService;
    }

    public void setAccessibilityService(AccessibilityService accessibilityService) {
        this.accessibilityService = accessibilityService;
    }

    public String user;

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setImage(File image) {
        this.image = image;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setVersionCode(Long versionCode) {
        this.versionCode = versionCode;
    }

    public void setPackName(String packName) {
        PackName = packName;
    }

    public File getImage() {
        return image;
    }

    public Long getVersionCode() {
        return versionCode;
    }

    public String getPackName() {
        return PackName;
    }

    public String getTitle() {
        return title;
    }

    public void setPluginFile(File pluginFile) {
        this.plugin_file = pluginFile;
    }

    public File getPluginFile() {
        return plugin_file;
    }

    public void setTheme(Resources.Theme theme) {
        this.theme = theme;
    }

    public Resources.Theme getTheme() {
        return theme;
    }

    public DexClassLoader getDexClassLoader() {
        return dexClassLoader;
    }

    public void setDexClassLoader(DexClassLoader dexClassLoader) {
        this.dexClassLoader = dexClassLoader;
    }

    public PackageInfo getPackageInfo() {
        return packageInfo;
    }

    public void setPackageInfo(PackageInfo packageInfo) {
        this.packageInfo = packageInfo;
    }

    public Resources getResources() {
        return mResources;
    }

    public void setResources(Resources mResources) {
        this.mResources = mResources;
    }

    public AssetManager getAssetManager() {
        return assetManager;
    }

    public void setAssetManager(AssetManager assetManager) {
        this.assetManager = assetManager;
    }

}
