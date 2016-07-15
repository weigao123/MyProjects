package com.lesports.bike.settings.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemProperties;

import com.lesports.bike.settings.service.PttService;
import com.lesports.bike.settings.ui.PttFragment;
import com.lesports.bike.settings.utils.L;

/**
 * Created by gaowei3 on 2016/5/29.
 */
public class PttReceiver extends BroadcastReceiver {
    private static final String TAG = PttReceiver.class.getSimpleName();
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent newIntent = new Intent(context, PttService.class);
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            L.d(TAG, "Boot complete");
            if (SystemProperties.getInt(PttFragment.PTT_STATUS, 0) == 1) {
                newIntent.putExtra("boot", true);
                context.startService(newIntent);
            }
        } else if ("com.lesports.bike.SWITCH_PTT".equals(intent.getAction())) {
            L.d(TAG, "SWITCH_PTT");
            newIntent.putExtra("switch", intent.getBooleanExtra("state", false));
            context.startService(newIntent);
        }
    }
}
