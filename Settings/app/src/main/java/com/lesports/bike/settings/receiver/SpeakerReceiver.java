package com.lesports.bike.settings.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.lesports.bike.settings.application.SettingApplication;
import com.lesports.bike.settings.control.SpeakerControl;

public class SpeakerReceiver extends BroadcastReceiver {
    private static final String TAG = "SpeakerReceiver";

    @Override
    public void onReceive(Context arg0, Intent intent) {
        // TODO Auto-generated method stub
        Log.d(TAG, "onReceive:" + intent.getAction());
        boolean isDown = intent.getBooleanExtra("speaker_down", false);
        if (isDown) {
            SpeakerControl.fromApplication(SettingApplication.getContext()).start();
        } else {
            SpeakerControl.fromApplication(SettingApplication.getContext()).stop();
        }
    }
    
}