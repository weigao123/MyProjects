package com.lesports.bike.settings.light;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;
import android.view.WindowManager;

import com.lesports.bike.settings.application.SettingApplication;

public class BikeLightService extends Service {

    private WindowManager mWindowManager;
    private static WindowManager.LayoutParams param;
    private static BikeLightView mBikeLightView;
    private int screenHeight;
    private int statusBarHeight;
    private static final String TAG = "BikeLightService";

    @Override
    public void onCreate() {
        Log.d(TAG, "service onCreate");
        showView();
        registerReceivers();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // TODO Auto-generated method stub
        flags = START_STICKY;
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        Intent localIntent = new Intent();
        localIntent.setClass(this, BikeLightService.class); // 销毁时重新启动Service
        this.startService(localIntent);
    }

    private void showView() {
        // 获取WindowManager
        mWindowManager = (WindowManager) getApplicationContext().getSystemService(
                Context.WINDOW_SERVICE);
        screenHeight = mWindowManager.getDefaultDisplay().getHeight();
        statusBarHeight = getStatusBarHeight();
        mBikeLightView = new BikeLightView(getApplicationContext(), screenHeight, statusBarHeight);
        Log.d(TAG, "new BikeLightView");

        // 设置LayoutParams(全局变量）相关参数
        param = ((SettingApplication) getApplication()).getMywmParams();
        param.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT; // 系统提示类型,重要
        param.flags = param.flags | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS; // 排版不受限制
        param.flags = param.flags | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
        param.softInputMode = WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE;
        param.flags = param.flags | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        param.privateFlags |=
                WindowManager.LayoutParams.PRIVATE_FLAG_FORCE_HARDWARE_ACCELERATED;
        param.softInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING;
        param.x = 0;
        param.y = screenHeight - statusBarHeight - 3;
        mWindowManager.addView(mBikeLightView, param);
    }

    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }
    
    private void registerReceivers() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        getApplicationContext().registerReceiver(mBroadcastReceiver, filter);
    }

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (Intent.ACTION_SCREEN_OFF.equals(action)) {
                hideBikeLightView();
            }
        }
    };

    private void hideBikeLightView() {
        if (BikeLightView.viewOpenned) {
            param.x = 0;
            param.y = screenHeight - statusBarHeight - 3;
            BikeLightView.viewOpenned = false;
            mWindowManager.updateViewLayout(mBikeLightView, param);
        }
    }

    public static BikeLightView getBikeLightView() {
        return mBikeLightView;
    }

    public static WindowManager.LayoutParams getParams() {
        return param;
    }

}