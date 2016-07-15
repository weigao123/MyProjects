
package com.lesports.bike.settings.utils;

import android.app.ActivityManager;
import android.app.ActivityManagerNative;
import android.app.IActivityManager;
import android.app.backup.BackupManager;
import android.content.Context;
import android.content.res.Configuration;
import android.os.RemoteException;

import java.lang.reflect.Method;
import java.util.Locale;

/**
 * Created by zhouying on 16-5-25.
 */
public class LanguageUtil {
    public static void updateLocale(Context context, Locale locale) {
        try {
            ActivityManager mActivityManager = (ActivityManager) context
                    .getSystemService(Context.ACTIVITY_SERVICE);
            IActivityManager am = ActivityManagerNative.getDefault();
            Configuration config = am.getConfiguration();
            config.setLocale(locale);

            am.updateConfiguration(config);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

}
