package com.lesports.bike.settings.utils;

import android.content.Context;

/**
 * Created by gaowei3 on 2016/5/26.
 */
public class DensityUtils {

    public static int dp2px(Context var0, float var1) {
        float var2 = var0.getResources().getDisplayMetrics().density;
        return (int)(var1 * var2 + 0.5F);
    }

    public static int px2dp(Context var0, float var1) {
        float var2 = var0.getResources().getDisplayMetrics().density;
        return (int)(var1 / var2 + 0.5F);
    }

    public static int sp2px(Context var0, float var1) {
        float var2 = var0.getResources().getDisplayMetrics().scaledDensity;
        return (int)(var1 * var2 + 0.5F);
    }

    public static int px2sp(Context var0, float var1) {
        float var2 = var0.getResources().getDisplayMetrics().scaledDensity;
        return (int)(var1 / var2 + 0.5F);
    }
}
