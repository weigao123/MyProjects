package com.lesports.bike.settings.receiver;

import android.app.KeyguardManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbManager;
import android.util.Log;

import com.lesports.bike.settings.ui.USBConnectActivity;

public class USBReceiver extends BroadcastReceiver {
    
    private Context mContext;
    private static boolean powerOff =false;
    private static boolean mConnected = false;
    private KeyguardManager mKeyguardManager = null;

    @Override
    public void onReceive(Context arg0, Intent arg1) {
        // TODO Auto-generated method stub
        mContext = arg0;
        mKeyguardManager = (KeyguardManager) mContext.getSystemService(Context.KEYGUARD_SERVICE);
        String action = arg1.getAction();
        if (action.equals(UsbManager.ACTION_USB_STATE)) {
            if (arg1.getBooleanExtra(UsbManager.USB_CONNECTED, false)
                    && !mKeyguardManager.isKeyguardLocked() && !mConnected) {
                mConnected = true;
                powerOff=false;
                startUSBConnectActivity();
            }
        } else if (action.equals(Intent.ACTION_POWER_DISCONNECTED)) {
            if (mConnected) {
                powerOff=true;
                mConnected = false;
            }
        }
    }
    
    private void startUSBConnectActivity() {
          Intent intent = new Intent();
          intent.setClass(mContext, USBConnectActivity.class);
          intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                  | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
          mContext.startActivity(intent);     
    }
    
    
}