package com.xframe.network.util;

import android.annotation.SuppressLint;

public class Application {
    private android.app.Application currentApplication;

    public static Application getInstance() {
        return My.getApplication();
    }

    /**
     * 获取全局的application
     *
     * @return 返回application
     */
    @SuppressLint("PrivateApi")
    public android.app.Application getNewApplication() {
        try {
            if (currentApplication == null) {
                currentApplication = (android.app.Application) Class.forName("android.app.ActivityThread").getMethod("currentApplication").invoke(null, (Object[]) null);
            }
            return currentApplication;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static class My {
        private static Application application;

        public static Application getApplication() {
            if (application == null) {
                application = new Application();
                application.getNewApplication();
            }
            return application;
        }
    }
}
