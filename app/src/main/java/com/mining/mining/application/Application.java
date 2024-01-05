package com.mining.mining.application;

import android.annotation.SuppressLint;
import android.content.Context;

import com.mining.mining.util.UserSharedUtil;

public class Application extends android.app.Application {
    @SuppressLint("StaticFieldLeak")
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        UserSharedUtil.init(this, "user");
    }


    public static Context getContext() {
        return context;
    }
}
