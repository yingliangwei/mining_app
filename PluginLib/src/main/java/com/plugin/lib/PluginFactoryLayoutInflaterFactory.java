package com.plugin.lib;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.util.ArrayMap;
import android.util.AttributeSet;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;

import java.lang.reflect.Constructor;
import java.util.Map;

public class PluginFactoryLayoutInflaterFactory implements LayoutInflater.Factory {
    private final Activity activity;

    public PluginFactoryLayoutInflaterFactory(Activity activity) {
        this.activity = activity;
    }

    // 默认前缀包名列表
    private static final String[] sClassPrefixList = {"android.widget.", "android.view.", "android.webkit."};
    private static final Object[] mConstructorArgs = new Object[2];
    private static final Map<String, Constructor<? extends View>> sConstructorMap = new ArrayMap<>();
    private static final Class<?>[] sConstructorSignature = new Class[]{Context.class, AttributeSet.class};

    @Override
    public View onCreateView(String name, Context context, AttributeSet attrs) {
        View view = createViewFromTag(context, name, attrs);
        AnalysisXml(view, attrs, context, name);
        return view;
    }

    private void AnalysisXml(View view, AttributeSet attrs, Context context, String name) {


    }


    private int getId(String str) {
        return Integer.parseInt(str.replace("@", ""));
    }

    private String getString(String str) {
        int id = Integer.parseInt(str.replace("@", ""));
        return getResources().getString(id);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private Drawable getDrawable(String str) {
        int id = Integer.parseInt(str.replace("@", ""));
        return getResources().getDrawable(id, getResources().newTheme());
    }

    public Resources getResources() {
        return activity.getResources();
    }

    public View createViewFromTag(Context context, String name, AttributeSet attrs) {
        if (name.equals("view")) {
            name = attrs.getAttributeValue(null, "class");
        }
        try {
            mConstructorArgs[0] = context;
            mConstructorArgs[1] = attrs;

            if (-1 == name.indexOf('.')) {
                for (String s : sClassPrefixList) {
                    final View view = createView(context, name, s);
                    if (view != null) {
                        return view;
                    }
                }
                return null;
            } else {
                return createView(context, name, null);
            }
        } catch (Exception e) {
            // We do not want to catch these, lets return null and let the actual LayoutInflater
            // try
            return null;
        } finally {
            // Don't retain references on context.
            mConstructorArgs[0] = null;
            mConstructorArgs[1] = null;
        }
    }

    private View createView(Context context, String name, String prefix) throws InflateException {
        Constructor<? extends View> constructor = sConstructorMap.get(name);
        try {
            if (constructor == null) {
                // Class not found in the cache, see if it's real, and try to add it
                Class<? extends View> clazz = context.getClassLoader().loadClass(prefix != null ? (prefix + name) : name).asSubclass(View.class);
                constructor = clazz.getConstructor(sConstructorSignature);
                sConstructorMap.put(name, constructor);
            }
            constructor.setAccessible(true);
            return constructor.newInstance(mConstructorArgs);
        } catch (Exception e) {
            // We do not want to catch these, lets return null and let the actual LayoutInflater
            // try
            return null;
        }
    }
}
