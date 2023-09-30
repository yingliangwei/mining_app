package com.plugin.lib;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import dalvik.system.BaseDexClassLoader;
import dalvik.system.DexClassLoader;

public class ApkClassLoader extends DexClassLoader {
    private final ClassLoader mGrandParent;
    private Method mFindLibraryMethod;
    private final ClassLoader classLoader;

    public ApkClassLoader(ClassLoader classLoader, String dexPath, String optimizedDirectory, String librarySearchPath, ClassLoader parent) {
        super(dexPath, optimizedDirectory, librarySearchPath, parent);
        mGrandParent = parent;
        this.classLoader = classLoader;
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        if (name.contains("Activity") || name.contains("activity")) {
            return super.findClass(name);
        }
        try {
            Class<?> c = classLoader.loadClass(name);
            if (c != null) {
                return c;
            }
        } catch (Exception e) {
            e.fillInStackTrace();
        }
        return super.findClass(name);
    }


    // 加入以下代码
    @Override
    public String findLibrary(String name) {
        String path = super.findLibrary(name);
        if (path != null) {
            return path;
        }
        if (mGrandParent instanceof BaseDexClassLoader) {
            return ((BaseDexClassLoader) mGrandParent).findLibrary(name);
        }
        try {
            if (mFindLibraryMethod == null) {
                mFindLibraryMethod = ClassLoader.class.getDeclaredMethod("findLibrary", String.class);
                mFindLibraryMethod.setAccessible(true);
            }
            // 如果插件获取不到，则交由父加载器进行获取
            return (String) mFindLibraryMethod.invoke(mGrandParent, name);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
    }
}
