
package com.lesports.bike.settings.application;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.view.WindowManager;

/**
 * Created by gaowei3 on 2016/5/29.
 */
public class SettingApplication extends Application {

    private static final String BASEURL = "http://10.154.156.221:8081";

    private static Context context;
    private WindowManager.LayoutParams wmParams = new WindowManager.LayoutParams();

    public WindowManager.LayoutParams getMywmParams() {
        return wmParams;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        // BatteryControl.getInstance();
    }

    public static Context getContext() {
        return context;
    }

    public static String getBaseUrl() {
        return BASEURL;
    }

    public static String getAccesToken() {
        Context aimContext = null;
        try {
            aimContext = context.createPackageContext("com.lesports.bike", Context.CONTEXT_IGNORE_SECURITY);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return null;
        }
        SharedPreferences sp = aimContext.getSharedPreferences("login_setting", Context.MODE_WORLD_READABLE + Context.MODE_WORLD_WRITEABLE);
        return sp.getString("sso_tk", "");
    }
}
