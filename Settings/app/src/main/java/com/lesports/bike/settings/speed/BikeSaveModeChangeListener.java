package com.lesports.bike.settings.speed;

import android.content.Context;
import android.provider.Settings;
import android.util.Log;

import com.lesports.bike.settings.application.SettingApplication;
import com.lesports.bike.settings.utils.AppDataUtils;

public class BikeSaveModeChangeListener implements SpeedChangeListener {
    
    private Context context;
    private Boolean isSaveMode;
    
    public BikeSaveModeChangeListener( Context context) {
        this.context = context;
    }

    @Override
    public void onChanged(int mspc) {
        
        // TODO Auto-generated method stub
        if (mspc > 0) {
            Settings.System.putInt(context.getContentResolver(),
                    android.provider.Settings.System.SCREEN_OFF_TIMEOUT, -1);
        } else {
            if (isSaveMode(context)) {
                Settings.System.putInt(context.getContentResolver(),
                        android.provider.Settings.System.SCREEN_OFF_TIMEOUT, 60000);
            } else {
                Settings.System.putInt(SettingApplication.getContext().getContentResolver(),
                        android.provider.Settings.System.SCREEN_OFF_TIMEOUT, -1);
            }
        }
        
    }
    
    private boolean isSaveMode(Context context) {
        if (context != null)
            isSaveMode = AppDataUtils.getSaveMode(context);
        return isSaveMode;
    }
    
}