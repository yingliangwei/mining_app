package com.plugin.activity;

import android.app.Activity;
import android.os.Bundle;


public interface PluginInterface {
    void onCreate(Bundle saveInstance);

    void attachInstance(Object o);

    void attachContext(Activity context);

    void onStart();

    void onResume();

    void onRestart();

    void onDestroy();

    void onStop();

    void onPause();

}