package com.lesports.bike.settings.application;

import android.app.Application;
import android.content.Context;

/**
 * Created by gaowei3 on 2016/5/29.
 */
public class SettingApplication extends Application {

    private static Context context;
    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
    }

    public static Context getContext() {
        return context;
    }
}
