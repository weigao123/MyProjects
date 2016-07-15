package com.lesports.bike.settings.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class AppDataUtils {
    
    public static final String KEY_RINGTONE = "ringtone";
    private static final String PRE_NAME = "app_data";
    public static final String KEY_SAVE_MODE = "save_mode";
    
    public static SharedPreferences getSharedPreferences(Context mContext, String name) {
        return mContext.getSharedPreferences(name,  Context.MODE_PRIVATE);
    }
    
    public static SharedPreferences getAppData(Context mContext) {
        return getSharedPreferences(mContext, PRE_NAME);
    }
    
    public static int getRingtone(Context mContex) {
        return getAppData(mContex).getInt(KEY_RINGTONE, 0);
    }
    
    public static Boolean getSaveMode(Context mContex) {
        return getAppData(mContex).getBoolean(KEY_SAVE_MODE, true);
    }
        
    public static void setRingtone(Context mContex, int ringtone) {
        getAppData(mContex).edit().putInt(KEY_RINGTONE, ringtone).commit();
    }
    
    public static void settSaveMode(Context mContex, Boolean saveMode) {
        getAppData(mContex).edit().putBoolean(KEY_SAVE_MODE, saveMode).commit();
    }
    
}