package com.lesports.bike.settings.speed;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class SaveModeReceiver extends BroadcastReceiver {
    private static final String BOOT_COMPLETED = "android.intent.action.BOOT_COMPLETED";

    @Override
    public void onReceive(Context arg0, Intent arg1) {
        // TODO Auto-generated method stub
        if(BOOT_COMPLETED.equals(arg1.getAction())) {
            Intent intent = new Intent(arg0, BikeSpeedService.class);
            arg0.startService(intent);
        }
    }
    
}