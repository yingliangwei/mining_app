package com.plugin.lib;

import android.util.Log;

import com.plugin.activity.Library;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Enumeration;

import dalvik.system.BaseDexClassLoader;
import dalvik.system.DexClassLoader;

public class ApkClassLoader extends BaseDexClassLoader {
    private final ClassLoader mGrandParent;
    private Method mFindLibraryMethod;
    private final ClassLoader classLoader;

    public ApkClassLoader(String dexPath, String optimizedDirectory, String librarySearchPath, ClassLoader parent) {
        super(dexPath, new File(optimizedDirectory), librarySearchPath, parent);
        mGrandParent = parent;
        this.classLoader = parent;
    }

    @Override
    public URL getResource(String name) {
        System.out.println(name);
        return super.getResource(name);
    }

    @Override
    public Enumeration<URL> getResources(String name) throws IOException {
        System.out.println(name);
        return super.getResources(name);
    }



    @Override
    public InputStream getResourceAsStream(String name) {
        System.out.println(name);
        return super.getResourceAsStream(name);
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        System.out.println(name);
        try {
            Class<?> c = classLoader.loadClass(name);
            if (c != null) {
                loadLibraries(c);
                return c;
            }
        } catch (Exception e) {
            e.fillInStackTrace();
        }
        return super.findClass(name);
    }


    private void loadLibraries(Class<?> clazz) {
        // 获取类的注解信息
        Library libAnnotation = clazz.getAnnotation(Library.class);
        if (libAnnotation != null) {
            // 加载库文件
            String[] libraries = libAnnotation.value();
            for (String library : libraries) {
                try {
                    System.loadLibrary(library);
                } catch (UnsatisfiedLinkError e) {
                    // 忽略未找到库文件的错误
                    Log.e("MyClassLoader", "loadLibrary failed: " + e.getMessage());
                }
            }
        }
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
