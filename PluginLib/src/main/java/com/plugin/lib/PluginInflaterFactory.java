package com.plugin.lib;


import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

// 3、继承 Factory 和 Factory2 自定义 Factory
public class PluginInflaterFactory implements LayoutInflater.Factory, LayoutInflater.Factory2 {
    private static final String TAG = "PluginInflaterFactory";
    private LayoutInflater.Factory mBaseFactory;
    private LayoutInflater.Factory2 mBaseFactory2;
    private final ClassLoader mClassLoader;

    public PluginInflaterFactory(LayoutInflater.Factory base, ClassLoader classLoader) {
        if (null == classLoader) {
            throw new IllegalArgumentException("classLoader is null");
        }
        mBaseFactory = base;
        mClassLoader = classLoader;
    }

    public PluginInflaterFactory(LayoutInflater.Factory2 base2, ClassLoader classLoader) {
        if (null == classLoader) {
            throw new IllegalArgumentException("classLoader is null");
        }

        mBaseFactory2 = base2;
        mClassLoader = classLoader;
    }

    // 4、实现 onCreateView() 方法
    @Override
    public View onCreateView(String s, Context context, AttributeSet attributeSet) {
        if (!s.contains(".")) {
            return null;
        }

        View v = getView(s, context, attributeSet);
        if (v != null) {
            return v;
        }

        if (mBaseFactory != null && !(mBaseFactory instanceof PluginInflaterFactory)) {
            v = mBaseFactory.onCreateView(s, context, attributeSet);
        }

        return v;
    }

    // 4、实现 onCreateView() 方法
    @Override
    public View onCreateView(View parent, String name, Context context, AttributeSet attrs) {
        if (!name.contains(".")) {
            return null;
        }

        View v = getView(name, context, attrs);
        if (v != null) {
            return v;
        }

        if (mBaseFactory2 != null && !(mBaseFactory2 instanceof PluginInflaterFactory)) {
            v = mBaseFactory2.onCreateView(parent, name, context, attrs);
        }

        return v;
    }

    // 5、自己实现 ClassLoader 创建 View 的过程
    private View getView(String name, Context context, AttributeSet attrs) {
        View v = null;
        try {
            Class<?> clazz = mClassLoader.loadClass(name);
            Constructor<?> c = clazz.getConstructor(Context.class, AttributeSet.class);
            v = (View) c.newInstance(context, attrs);
        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException |
                 InstantiationException | InvocationTargetException ignored) {

        }
        return v;
    }
}