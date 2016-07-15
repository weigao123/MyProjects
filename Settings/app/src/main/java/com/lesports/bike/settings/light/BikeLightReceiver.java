package com.lesports.bike.settings.light;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.view.WindowManager;

public class BikeLightReceiver extends BroadcastReceiver {

    private static boolean pullable = true;
    private static final String name = "pullable";
    private static final String LIGHT_SWITCH = "com.lesports.bike.settings.LIGHT_SWITCH";
    private static final String BOOT_COMPLETED = "android.intent.action.BOOT_COMPLETED";
    private static final String LIGHT_SERVICE_NAME =
            "com.lesports.bike.settings.light.BikeLightService";

    @Override
    public void onReceive(Context context, Intent arg1) {
        String action = arg1.getAction();
        Intent intent = new Intent(context, BikeLightService.class);
        WindowManager mWindowManager = (WindowManager) context.getSystemService(
                Context.WINDOW_SERVICE);
        if (action.equals(BOOT_COMPLETED)) {
            // 启动指定Service
            context.startService(intent);
        } else if (action.equals(LIGHT_SWITCH)) {
            pullable = (Boolean) arg1.getExtra(name);
            if (!pullable) {
                if (BikeLightService.getBikeLightView() != null
                        && BikeLightService.getBikeLightView().getParent() != null) {
                    mWindowManager.removeView(BikeLightService.getBikeLightView());
                }
            } else {
                if (BikeLightService.getBikeLightView() != null
                        && BikeLightService.getParams() != null 
                        &&BikeLightService.getBikeLightView().getParent() == null) {
                    mWindowManager.addView(BikeLightService.getBikeLightView(),
                            BikeLightService.getParams());
                }
            }
        }
    }

}
