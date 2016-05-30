package com.lesports.bike.settings.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.lesports.bike.settings.ui.BaseFragment;
import com.lesports.bike.settings.ui.DetailActivity;

import java.util.List;

/**
 * Created by gwball on 2016/5/25.
 */
public class ActivityUtils {

    public static boolean isSingleActivity(Context context) {
        ActivityManager am = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
        List tasks = am.getRunningTasks(1);
        return ((ActivityManager.RunningTaskInfo)tasks.get(0)).numRunning == 1;
    }

    public static void startFragmentActivity(Context context, Class fragmentClass) {
        startFragmentActivity(context, fragmentClass, null);
    }

    public static void startFragmentActivity(Context context, Class fragmentClass, Bundle bundle) {
        Intent intent = new Intent(context, DetailActivity.class);
        intent.putExtra(BaseFragment.FRAGMENT_CLASS, fragmentClass);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        context.startActivity(intent);
    }

    public static void startActivity(Context context, Class activityClass, Bundle bundle) {
        Intent intent = new Intent(context, activityClass);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        context.startActivity(intent);
    }
}
