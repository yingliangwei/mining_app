package com.plugin.lib;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;

import java.lang.reflect.Field;

@SuppressLint("DiscouragedPrivateApi")
public class PluginLayoutInflater {
    // 1、封装 LayoutInflater 的 from() 方法，传入 ClassLoader
    public static LayoutInflater from(Context context, ClassLoader classLoader) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (layoutInflater == null) {
            throw new AssertionError("LayoutInflater not found.");
        }

        LayoutInflater.Factory2 baseFactory2 = layoutInflater.getFactory2();
        LayoutInflater.Factory baseFactory1 = layoutInflater.getFactory();

        PluginInflaterFactory factory;
        if (checkBaseFactory2(baseFactory2)) {
            // 2、将 ClassLoader 进一步传入自定义 Factory 中
            factory = new PluginInflaterFactory(baseFactory2, classLoader);
            setFactory2(layoutInflater, factory);
        } else if (checkBaseFactory1(baseFactory1)) {
            // 2、将 ClassLoader 进一步传入自定义 Factory 中
            factory = new PluginInflaterFactory(baseFactory1, classLoader);
            setFactory(layoutInflater, factory);
        }

        return layoutInflater;
    }

    private static boolean checkBaseFactory1(LayoutInflater.Factory baseFactory) {
        if (baseFactory == null) {
            return true;
        }

        if (baseFactory instanceof PluginInflaterFactory) {
            return false;
        }

        return true;
    }

    private static boolean checkBaseFactory2(LayoutInflater.Factory2 baseFactory2) {
        if (baseFactory2 == null || baseFactory2 instanceof PluginInflaterFactory) {
            return false;
        }

        return true;
    }

    private static void setFactory(LayoutInflater layoutInflater, PluginInflaterFactory factory) {
        try {
            Field mFactory = LayoutInflater.class.getDeclaredField("mFactory");
            mFactory.setAccessible(true);
            mFactory.set(layoutInflater, factory);
        } catch (NoSuchFieldException | IllegalAccessException e1) {
            e1.printStackTrace();
        }
    }

    private static void setFactory2(LayoutInflater layoutInflater, PluginInflaterFactory factory) {
        try {
            Field mFactory = LayoutInflater.class.getDeclaredField("mFactory2");
            mFactory.setAccessible(true);
            mFactory.set(layoutInflater, factory);
        } catch (NoSuchFieldException | IllegalAccessException e1) {
            e1.printStackTrace();
        }
    }
}