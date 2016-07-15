package com.lesports.bike.settings.speed;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import com.lesports.bike.settings.application.SettingApplication;

public class BikeSpeedService extends Service {
    private BikeSpeedManager bikeSpeedManager;
    
    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
        bikeSpeedManager = BikeSpeedManager.fromApplication(
                getApplicationContext());
        bikeSpeedManager.registerSpeedListener(
                new BikeSaveModeChangeListener(getApplicationContext()));
    }

    @Override
    public IBinder onBind(Intent arg0) {
        // TODO Auto-generated method stub
        return null;
    }
    
}